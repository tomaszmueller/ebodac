package org.motechproject.ebodac.service.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Enrollment;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.SubjectEnrollments;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.enums.EnrollmentStatus;
import org.motechproject.ebodac.domain.enums.VisitType;
import org.motechproject.ebodac.exception.EbodacEnrollmentException;
import org.motechproject.ebodac.helper.SubjectAgeRangeHelper;
import org.motechproject.ebodac.repository.EnrollmentDataService;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.messagecampaign.exception.CampaignNotFoundException;
import org.motechproject.messagecampaign.service.MessageCampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service("ebodacEnrollmentService")
public class EbodacEnrollmentServiceImpl implements EbodacEnrollmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EbodacEnrollmentServiceImpl.class);

    public static final String LONG_TERM_FOLLOW_UP_CAMPAIGN = ".* Long-term Follow-up visit";
    public static final String FOLLOW_UP_CAMPAIGN = ".* Vaccination.*Follow-up visit";

    private SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    private EnrollmentDataService enrollmentDataService;

    private SubjectDataService subjectDataService;

    private MessageCampaignService messageCampaignService;

    private ConfigService configService;

    @Override
    public void enrollScreening(Subject subject) {
        try {
            enrollNew(subject, VisitType.SCREENING.getMotechValue(), DateUtil.now().toLocalDate(), new Time(DateUtil.now().toLocalTime()));
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
        }
    }

    @Override
    public void enrollSubject(Subject subject) {
        List<Visit> visits = subject.getVisits();

        if (visits != null) {
            for (Visit visit : visits) {
                if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
                    if (visit.getDate() != null) {
                        enrollSubject(visit);
                    }
                } else if (visit.getDate() == null) {
                    enrollSubject(visit);
                }
            }
        }
    }

    @Override
    public void enrollSubject(String subjectId) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subjectId);
        checkSubjectEnrollmentsStatus(subjectEnrollments, subjectId);

        Subject subject = subjectEnrollments.getSubject();

        boolean disconVac = checkSubjectRequiredDataAndDisconVacDate(subject);
        boolean subjectNotBoostered = subject.getBoosterVaccinationDate() == null;

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            if (!EnrollmentStatus.UNENROLLED.equals(enrollment.getStatus()) && !EnrollmentStatus.INITIAL.equals(enrollment.getStatus())) {
                enrollment.setPreviousStatus(EnrollmentStatus.ENROLLED);
                continue;
            }
            if (enrollment.getReferenceDate() == null) {
                throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because reference date is empty",
                        "ebodac.enroll.error.emptyReferenceDate", subject.getSubjectId(), enrollment.getCampaignName());
            } else if (disconVac && checkIfCampaignInDisconVacList(enrollment)) {
                enrollment.setStatus(EnrollmentStatus.UNENROLLED_FROM_BOOSTER);
                enrollment.setPreviousStatus(EnrollmentStatus.ENROLLED);
                continue;
            }
            if (subjectNotBoostered && checkIfCampaignInBoosterRelatedMessagesList(enrollment)) {
                continue;
            }

            enrollment.setStatus(EnrollmentStatus.ENROLLED);
            if (!checkDuplicatedEnrollments(subject, enrollment)) {
                scheduleJobsForEnrollment(enrollment, true);
            }
            enrollmentDataService.update(enrollment);
        }
        updateSubjectEnrollments(subjectEnrollments);
    }

    @Override
    public void enrollSubjectToCampaign(String subjectId, String campaignName) {
        enrollUnenrolled(subjectId, campaignName, null);
    }

    @Override
    public void enrollSubjectToCampaignWithNewDate(String subjectId, String campaignName, LocalDate date) {
        enrollUnenrolled(subjectId, campaignName, date);
    }

    @Override
    public void enrollOrCompleteCampaignForSubject(Visit visit) {
        if (visit.getDate() != null) {
            if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
                enrollSubject(visit);
            } else {
                completeCampaignForSubjectWithStatus(visit, EnrollmentStatus.COMPLETED);
            }
        } else if (!VisitType.PRIME_VACCINATION_DAY.equals(visit.getType()) && visit.getMotechProjectedDate() != null) {
            enrollOrReenrollSubject(visit);
        }
    }

    @Override
    public void unenrollSubject(String subjectId) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subjectId);
        if (subjectEnrollments == null || !EnrollmentStatus.ENROLLED.equals(subjectEnrollments.getStatus())) {
            throw new EbodacEnrollmentException("Cannot unenroll Participant, because Participant with id: %s is not enrolled in any campaign",
                    "ebodac.unenroll.error.noEnrolledSubject", subjectId);
        }
        for (Enrollment enrollment: subjectEnrollments.getEnrollments()) {
            if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
                unscheduleJobsForEnrollmentAndChangeParent(enrollment);

                enrollment.setStatus(EnrollmentStatus.UNENROLLED);
                enrollmentDataService.update(enrollment);
            } else {
                enrollment.setPreviousStatus(EnrollmentStatus.UNENROLLED);
            }
        }
        subjectEnrollments.setDateOfUnenrollment(LocalDate.now());
        updateSubjectEnrollments(subjectEnrollments);
    }

    @Override
    public void unenrollSubject(String subjectId, String campaignName) {
        if (!unscheduleJobsAndSetStatusForEnrollment(subjectId, campaignName, EnrollmentStatus.UNENROLLED)) {
            throw new EbodacEnrollmentException("Cannot unenroll Participant, because no Participant with id: %s registered in Campaign with name: %s",
                    "ebodac.unenroll.error.subjectNotEnrolledInCampaign", subjectId, campaignName);
        }
    }

    @Override
    public void reenrollSubject(Visit visit) {
        if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
            throw new EbodacEnrollmentException("Prime Vaccination Day Visit date cannot be changed", "ebodac.reenroll.error.primeVaccinationDateChanged");
        } else if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())) {
            String campaignName = visit.getType().getMotechValue();
            reenrollSubjectWithNewDate(visit.getSubject().getSubjectId(), campaignName, visit.getMotechProjectedDate());
        }
    }

    @Override
    public void reenrollSubjectWithNewDate(String subjectId, String campaignName, LocalDate date) {
        unenrollSubject(subjectId, campaignName);
        enrollUnenrolled(subjectId, campaignName, date);
    }

    @Override
    public void withdrawalOrEnrollSubject(Subject oldSubject, Subject newSubject) {
        if (newSubject.getPrimerVaccinationDate() != null && oldSubject.getPrimerVaccinationDate() == null) {
            enrollSubject(newSubject);
        } else {
            enrollBoosterRelatedCampaignsIfNeeded(oldSubject, newSubject);
        }
        deleteEnrollmentsIfPrimeOrBoosterVaccinationDateRemoved(oldSubject, newSubject);
        rollbackIfWithdrawalDateRemoved(oldSubject, newSubject);
        if (newSubject.getDateOfDisconStd() != null && oldSubject.getDateOfDisconStd() == null) {
            if (newSubject.getVisits() != null) {
                for (Visit visit: newSubject.getVisits()) {
                    completeCampaignForSubjectWithStatus(visit, EnrollmentStatus.WITHDRAWN_FROM_STUDY);
                }
            }
            LOGGER.info("Participant with id: {} was withdrawn from study", oldSubject.getSubjectId());
        } else if (newSubject.getDateOfDisconVac() != null && oldSubject.getDateOfDisconVac() == null) {
            unenrollIfCampaignInDisconVacList(newSubject);
            LOGGER.info("Participant with id: {} was unenrolled from booster", oldSubject.getSubjectId());
        }
        changeDuplicatedEnrollmentsWhenDateOfBirthChanged(newSubject, oldSubject);
    }

    @Override
    public void completeCampaign(String subjectId, String campaignName) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subjectId);
        if (subjectEnrollments != null) {
            Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);
            if (enrollment != null) {
                completeCampaignForDuplicatedEnrollments(enrollment);
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
                enrollment.setDuplicatedEnrollments(null);
                updateSubjectEnrollments(subjectEnrollments);
            }
        }
    }

    @Override
    public boolean checkIfEnrolledAndUpdateEnrollment(Visit visit) {
        if (VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) || VisitType.SCREENING.equals(visit.getType())) {
            return false;
        }

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(visit.getSubject().getSubjectId());
        String campaignName = visit.getType().getMotechValue();

        if (null == subjectEnrollments) {
            return false;
        }

        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);

        if (enrollment == null) {
            return false;
        }

        if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
            return true;
        }

        if (!VisitType.PRIME_VACCINATION_DAY.equals(visit.getType()) && !visit.getMotechProjectedDate().equals(enrollment.getReferenceDate())) {
            try {
                updateReferenceDateIfUnenrolled(enrollment, visit);
            } catch (EbodacEnrollmentException e) {
                LOGGER.debug(e.getMessage(), e);
            }
        }

        return false;
    }

    @Override
    public boolean isEnrolled(String subjectId) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subjectId);
        return subjectEnrollments != null && EnrollmentStatus.ENROLLED.equals(subjectEnrollments.getStatus());
    }

    @Override
    public void createEnrollmentRecordsForSubject(Subject subject) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        if (subjectEnrollments == null) {
            subjectEnrollments = new SubjectEnrollments(subject);
        }

        for (Visit visit : subject.getVisits()) {
            if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())) {
                String campaignName = visit.getType().getMotechValue();
                Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);

                if (enrollment == null) {
                    LocalDate referenceDate = visit.getMotechProjectedDate();
                    if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
                        referenceDate = visit.getDate();
                    }

                    if (referenceDate != null) {
                        try {
                            Long stageId = getActualStageId(subject.getStageId());
                            enrollment = new Enrollment(subject.getSubjectId(), campaignName, referenceDate, stageId, EnrollmentStatus.INITIAL);
                            subjectEnrollments.addEnrolment(enrollment);
                        } catch (IllegalArgumentException e) {
                            LOGGER.debug(e.getMessage(), e);
                        }
                    }
                }
            }
        }

        updateSubjectEnrollments(subjectEnrollments);
    }

    @Override
    public void updateEnrollmentsWhenSubjectDataChanged(Subject newSubject, Subject oldSubject, boolean subjectImported) {
        if (isEnrolled(newSubject.getSubjectId())) {
            if (StringUtils.isBlank(newSubject.getPhoneNumber()) || newSubject.getLanguage() == null) {
                unenrollSubject(newSubject.getSubjectId());
            } else {
                if (!newSubject.getPhoneNumber().equals(oldSubject.getPhoneNumber())) {
                    changeDuplicatedEnrollmentsWhenSubjectDataChanged(newSubject);
                }
            }
        } else {
            if (checkIfSubjectRequiredDataWasAdded(newSubject, oldSubject)) {
                if (subjectImported) {
                    createEnrollmentRecordsForSubject(newSubject);
                } else {
                    createEnrollmentRecordsForSubject(oldSubject);
                }
            }
        }
    }

    @Override
    public void unenrollAndRemoveEnrollment(Visit visit) {
        if (!VisitType.PRIME_VACCINATION_DAY.equals(visit.getType()) && !VisitType.UNSCHEDULED_VISIT.equals(visit.getType())
                && !VisitType.SCREENING.equals(visit.getType())) {
            unenrollAndRemoveEnrollment(visit.getSubject().getSubjectId(), visit.getType().getMotechValue());
        }
    }

    @Override
    public void rollbackOrRemoveEnrollment(Visit visit) {
        if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
            unenrollAndRemoveEnrollment(visit.getSubject().getSubjectId(), visit.getType().getMotechValue());
            unenrollAndRemoveEnrollment(visit.getSubject().getSubjectId(), EbodacConstants.BOOSTER_RELATED_MESSAGES);
        } else if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())) {
            SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(visit.getSubject().getSubjectId());
            if (subjectEnrollments != null) {
                rollbackEnrollmentStatusOrEnrollNew(subjectEnrollments, visit.getSubject(), visit.getType().getMotechValue(),
                        visit.getMotechProjectedDate(), EnrollmentStatus.COMPLETED);
            }
        }
    }

    private void unenrollAndRemoveEnrollment(String subjectId, String campaignName) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subjectId);

        if (subjectEnrollments != null) {
            Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);

            if (enrollment != null) {
                unnerollAndDeleteEnrollment(enrollment, subjectEnrollments, null);
            }

            if (subjectEnrollments.getEnrollments().isEmpty()) {
                subjectEnrollmentsDataService.delete(subjectEnrollments);
            }
        }
    }

    private void completeCampaignForDuplicatedEnrollments(Enrollment parentEnrollment) {
        for (Enrollment e : parentEnrollment.getDuplicatedEnrollments()) {
            SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(e.getExternalId());
            if (subjectEnrollments != null) {
                Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(e.getCampaignName());
                if (enrollment != null && EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())
                        && e.getParentEnrollment().equals(enrollment.getParentEnrollment())) {
                    enrollment.setStatus(EnrollmentStatus.COMPLETED);
                    updateSubjectEnrollments(subjectEnrollments);
                }
            }
        }
    }

    private void deleteEnrollmentsIfPrimeOrBoosterVaccinationDateRemoved(Subject oldSubject, Subject newSubject) {
        if (newSubject.getPrimerVaccinationDate() == null && oldSubject.getPrimerVaccinationDate() != null) {
            deleteEnrollmentsIfPrimeOrBoosterVaccinationDateRemoved(oldSubject.getSubjectId(), true);
        } else if (newSubject.getBoosterVaccinationDate() == null && oldSubject.getBoosterVaccinationDate() != null) {
            deleteEnrollmentsIfPrimeOrBoosterVaccinationDateRemoved(oldSubject.getSubjectId(), false);
        }
    }

    private void deleteEnrollmentsIfPrimeOrBoosterVaccinationDateRemoved(String subjectId, boolean prime) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subjectId);

        if (subjectEnrollments != null) {
            Iterator<Enrollment> it = subjectEnrollments.getEnrollments().iterator();
            while (it.hasNext()) {
                Enrollment enrollment = it.next();
                if (prime || checkIfCampaignInBoosterRelatedMessagesList(enrollment)) {
                    unnerollAndDeleteEnrollment(enrollment, subjectEnrollments, it);
                }
            }

            if (subjectEnrollments.getEnrollments().isEmpty()) {
                subjectEnrollmentsDataService.delete(subjectEnrollments);
            }
        }
    }

    private void unnerollAndDeleteEnrollment(Enrollment enrollment, SubjectEnrollments subjectEnrollments, Iterator<Enrollment> it) {
        try {
            if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
                changeParentForDuplicatedEnrollments(enrollment, false);
                unscheduleJobsForEnrollment(enrollment);
            }

            if (it != null) {
                it.remove();
            } else {
                subjectEnrollments.removeEnrolment(enrollment);
            }
            updateSubjectEnrollments(subjectEnrollments);
            enrollmentDataService.delete(enrollment);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
        }
    }

    private void updateReferenceDateIfUnenrolled(Enrollment enrollment, Visit visit) {
        enrollment.setReferenceDate(visit.getMotechProjectedDate());
        enrollment.setParentEnrollment(null);
        enrollment.setDuplicatedEnrollments(null);

        enrollmentDataService.update(enrollment);
    }

    private void enrollSubject(Visit visit) {
        if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
            enrollNew(visit.getSubject(), visit.getType().getMotechValue(), visit.getDate());
            enrollNew(visit.getSubject(), EbodacConstants.BOOSTER_RELATED_MESSAGES, visit.getDate());
        } else if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())) {
            enrollNew(visit.getSubject(), visit.getType().getMotechValue(), visit.getMotechProjectedDate());
        }
    }

    private void enrollOrReenrollSubject(Visit visit) {
        if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())) {
            SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(visit.getSubject().getSubjectId());

            if (subjectEnrollments == null) {
                enrollNew(visit.getSubject(), visit.getType().getMotechValue(), visit.getMotechProjectedDate());
            } else {
                Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(visit.getType().getMotechValue());

                if (enrollment == null) {
                    enrollNew(visit.getSubject(), visit.getType().getMotechValue(), visit.getMotechProjectedDate());
                } else if (!enrollment.getReferenceDate().equals(visit.getMotechProjectedDate())) {
                    try {
                        if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
                            reenrollSubjectWithNewDate(visit.getSubject().getSubjectId(), enrollment.getCampaignName(), visit.getMotechProjectedDate());
                        } else {
                            updateReferenceDateIfUnenrolled(enrollment, visit);
                        }
                    } catch (EbodacEnrollmentException e) {
                        LOGGER.debug(e.getMessage(), e);
                    }
                }
            }
        }
    }

    private void enrollNew(Subject subject, String campaignName, LocalDate referenceDate) {
        try {
            enrollNew(subject, campaignName, referenceDate, null);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
        }
    }

    private void enrollNew(Subject subject, String campaignName, LocalDate referenceDate, Time deliverTime) {
        if (subject == null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant to Campaign with name: %s, because participant is null",
                    "ebodac.enroll.error.subjectNull", campaignName);
        }

        if (referenceDate == null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because reference date is empty",
                    "ebodac.enroll.error.emptyReferenceDate", subject.getSubjectId(), campaignName);
        }

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        if (subjectEnrollments == null) {
            subjectEnrollments = new SubjectEnrollments(subject);
        }

        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);

        if (enrollment != null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because enrollment already exists",
                    "ebodac.enroll.error.enrolmentAlreadyExist", subject.getSubjectId(), campaignName);
        }

        try {
            Long stageId = getActualStageId(subject.getStageId());
            enrollment = new Enrollment(subject.getSubjectId(), campaignName, referenceDate, stageId, deliverTime);
        } catch (IllegalArgumentException e) {
            throw new EbodacEnrollmentException("Participant StageId cannot be empty", "ebodac.enrollment.error.emptyStageId");
        }

        subjectEnrollments.addEnrolment(enrollment);
        checkSubjectRequiredDataAndDisconVacDateAndBoosterRelatedMessages(subject, enrollment);

        if (!checkDuplicatedEnrollments(subject, enrollment)) {
            scheduleJobsForEnrollment(enrollment, false);
        }

        updateSubjectEnrollments(subjectEnrollments);
    }

    private void enrollUnenrolled(String subjectId, String campaignName, LocalDate referenceDate) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subjectId);
        if (subjectEnrollments == null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant, because not found unenrolled Participant with id: %s in campaign with name: %s",
                    "ebodac.enroll.error.noUnenrolledSubjectInCampaign", subjectId, campaignName);
        }

        Subject subject = subjectEnrollments.getSubject();

        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);

        checkIfUnenrolled(enrollment, subjectId, campaignName);

        checkSubjectRequiredDataAndDisconVacDateAndBoosterRelatedMessages(subject, enrollment);

        if (referenceDate != null) {
            if (VisitType.PRIME_VACCINATION_DAY.getMotechValue().equals(campaignName) && !referenceDate.equals(enrollment.getReferenceDate())) {
                throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because reference date cannot be changed for Prime Vaccination Day Campaign",
                        "ebodac.enroll.error.primeVaccinationDateChanged", subjectId, enrollment.getCampaignName());
            }
            enrollment.setReferenceDate(referenceDate);
            enrollment.setParentEnrollment(null);
        } else if (enrollment.getReferenceDate() == null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because reference date is empty",
                    "ebodac.enroll.error.emptyReferenceDate", subjectId, enrollment.getCampaignName());
        }

        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        if (!checkDuplicatedEnrollments(subject, enrollment)) {
            scheduleJobsForEnrollment(enrollment, false);
        }

        updateSubjectEnrollments(subjectEnrollments);
    }

    private void checkIfUnenrolled(Enrollment enrollment, String subjectId, String campaignName) {
        if (enrollment == null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant, because not found unenrolled Participant with id: %s in campaign with name: %s",
                    "ebodac.enroll.error.noUnenrolledSubjectInCampaign", subjectId, campaignName);
        }
        if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because participant is already enrolled in this campaign",
                    "ebodac.enroll.error.subjectAlreadyEnrolled", subjectId, enrollment.getCampaignName());
        }
        if (EnrollmentStatus.COMPLETED.equals(enrollment.getStatus()) || EnrollmentStatus.WITHDRAWN_FROM_STUDY.equals(enrollment.getStatus())
                || EnrollmentStatus.UNENROLLED_FROM_BOOSTER.equals(enrollment.getStatus())) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because this campaign is completed",
                    "ebodac.enroll.error.campaignCompleted", subjectId, enrollment.getCampaignName());
        }
    }

    private void completeCampaignForSubjectWithStatus(Visit visit, EnrollmentStatus status) {
        if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
            completeCampaignForSubjectWithStatus(visit.getSubject(), visit.getType().getMotechValue(), status);
            completeCampaignForSubjectWithStatus(visit.getSubject(), EbodacConstants.BOOSTER_RELATED_MESSAGES, status);
        } else if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())) {
            completeCampaignForSubjectWithStatus(visit.getSubject(), visit.getType().getMotechValue(), status);
        }
    }

    private void completeCampaignForSubjectWithStatus(Subject subject, String campaignName, EnrollmentStatus status) {
        try {
            unscheduleJobsAndSetStatusForEnrollment(subject.getSubjectId(), campaignName, status);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
        }
    }

    private boolean unscheduleJobsAndSetStatusForEnrollment(String subjectId, String campaignName, EnrollmentStatus status) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subjectId);
        if (subjectEnrollments == null) {
            return false;
        }
        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);

        if (enrollment == null) {
            return false;
        } else if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
            unscheduleJobsForEnrollmentAndChangeParent(enrollment);

            enrollment.setStatus(status);
            updateSubjectEnrollments(subjectEnrollments);

            return true;
        } else if ((EnrollmentStatus.UNENROLLED.equals(enrollment.getStatus()) || EnrollmentStatus.INITIAL.equals(enrollment.getStatus())) && !status.equals(enrollment.getStatus())) {
            enrollment.setStatus(status);
            updateSubjectEnrollments(subjectEnrollments);
        }

        return false;
    }

    private void scheduleJobsForEnrollment(Enrollment enrollment, boolean completeIfLastMessageInThePast) {
        try {
            messageCampaignService.scheduleJobsForEnrollment(enrollment.toCampaignEnrollment());
        } catch (CampaignNotFoundException e) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s because Campaign with name: %s doesn't exist",
                    e, "ebodac.enroll.error.campaignNotExist", enrollment.getExternalId(), enrollment.getCampaignName());
        } catch (IllegalArgumentException e) {
            if (completeIfLastMessageInThePast) {
                LOGGER.debug("Cannot enroll Participant with id: {} for Campaign with name: {}, because last message date is in the past. Changing enrollment status to Completed",
                        enrollment.getExternalId(), enrollment.getCampaignName(), e);
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
            } else {
                throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because last message date is in the past",
                        e, "ebodac.enroll.error.lastMessageInPast", enrollment.getExternalId(), enrollment.getCampaignName());
            }
        } catch (Exception e) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because of unknown exception",
                    e, "ebodac.enroll.error.unknownException", enrollment.getExternalId(), enrollment.getCampaignName());
        }
    }

    private void unscheduleJobsForEnrollmentAndChangeParent(Enrollment enrollment) {
        changeParentForDuplicatedEnrollments(enrollment, true);
        unscheduleJobsForEnrollment(enrollment);
    }

    private void unscheduleJobsForEnrollment(Enrollment enrollment) {
        try {
            messageCampaignService.unscheduleJobsForEnrollment(enrollment.toCampaignEnrollment());
        } catch (CampaignNotFoundException e) {
            throw new EbodacEnrollmentException("Cannot unenroll Participant with id: %s because campaign with name: %s doesn't exist",
                    e, "ebodac.unenroll.error.campaignNotExist", enrollment.getExternalId(), enrollment.getCampaignName());
        } catch (Exception e) {
            throw new EbodacEnrollmentException("Cannot unenroll Participant with id: %s from campaign with name: %s, because of unknown exception",
                    e, "ebodac.unenroll.error.unknownException", enrollment.getExternalId(), enrollment.getCampaignName());
        }
    }

    private void updateSubjectEnrollments(SubjectEnrollments subjectEnrollments) {
        EnrollmentStatus status = EnrollmentStatus.COMPLETED;

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
                status = EnrollmentStatus.ENROLLED;
                break;
            }
            if (EnrollmentStatus.UNENROLLED.equals(enrollment.getStatus())) {
                status = EnrollmentStatus.UNENROLLED;
            } else if (!EnrollmentStatus.UNENROLLED.equals(status) && EnrollmentStatus.INITIAL.equals(enrollment.getStatus())) {
                status = EnrollmentStatus.INITIAL;
            } else if (!EnrollmentStatus.UNENROLLED.equals(status) && !EnrollmentStatus.INITIAL.equals(status)
                    && EnrollmentStatus.WITHDRAWN_FROM_STUDY.equals(enrollment.getStatus())) {
                status = EnrollmentStatus.WITHDRAWN_FROM_STUDY;
            }
        }

        subjectEnrollments.setStatus(status);
        subjectEnrollmentsDataService.update(subjectEnrollments);
    }

    private void checkSubjectRequiredDataAndDisconVacDateAndBoosterRelatedMessages(Subject subject, Enrollment enrollment) {
        if (checkSubjectRequiredDataAndDisconVacDate(subject) && checkIfCampaignInDisconVacList(enrollment)) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because participant resigned form booster vaccination",
                    "ebodac.enroll.error.subjectResignFromBooster", subject.getSubjectId(), enrollment.getCampaignName());
        }
        if (subject.getBoosterVaccinationDate() == null && checkIfCampaignInBoosterRelatedMessagesList(enrollment)) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because participant Booster Vaccination Date is empty",
                    "ebodac.enroll.error.emptyBoosterVaccinationDate", subject.getSubjectId(), enrollment.getCampaignName());
        }
    }

    private boolean checkSubjectRequiredDataAndDisconVacDate(Subject subject) {
        if (subject.getDateOfDisconStd() != null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s, because participant is withdrawn from study",
                    "ebodac.enroll.error.subjectWithdrawn", subject.getSubjectId());
        }
        if (subject.getLanguage() == null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s, because participant language is empty",
                    "ebodac.enroll.error.emptyLanguage", subject.getSubjectId());
        }
        if (StringUtils.isBlank(subject.getPhoneNumber())) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s, because participant phone number is empty",
                    "ebodac.enroll.error.emptyPhoneNumber", subject.getSubjectId());
        }
        if (subject.getPrimerVaccinationDate() == null) {
            throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s, because participant Prime Vaccination Date is empty",
                    "ebodac.enroll.error.emptyPrimeVaccinationDate", subject.getSubjectId());
        }

        return subject.getDateOfDisconVac() != null;
    }

    private boolean checkIfCampaignInDisconVacList(Enrollment enrollment) {
        return configService.getConfig().getDisconVacCampaignsList().contains(enrollment.getCampaignNameWithStageId());
    }

    private void changeParentForDuplicatedEnrollments(Enrollment oldParent, boolean parentUnenrolled) {
        if (oldParent.hasDuplicatedEnrollments()) {
            Set<Enrollment> duplicatedEnrollments = oldParent.getDuplicatedEnrollments();
            for (Enrollment e : duplicatedEnrollments) {
                if (EnrollmentStatus.ENROLLED.equals(e.getStatus())) {
                    try {
                        scheduleJobsForEnrollment(e, false);
                    } catch (EbodacEnrollmentException ex) {
                        throw new EbodacEnrollmentException("Cannot unenroll Participant with id: %s from campaign with name: %s, because error occurred during enrolling new parent for duplicated enrolments",
                                ex, "ebodac.unenroll.error.cannotEnrollNewParent", oldParent.getExternalId(), oldParent.getCampaignName());
                    }

                    e.setParentEnrollment(null);
                    duplicatedEnrollments.remove(e);

                    oldParent.setDuplicatedEnrollments(null);
                    e.setDuplicatedEnrollments(duplicatedEnrollments);

                    if (parentUnenrolled) {
                        oldParent.setParentEnrollment(e);
                        duplicatedEnrollments.add(oldParent);
                    }

                    enrollmentDataService.update(oldParent);
                    enrollmentDataService.update(e);
                    break;
                }
            }
            oldParent.setDuplicatedEnrollments(null);
        }
    }

    private boolean checkDuplicatedEnrollments(Subject subject, Enrollment enrollment) {
        enrollment.setDuplicatedEnrollments(null);

        if (enrollment.getParentEnrollment() != null && EnrollmentStatus.ENROLLED.equals(enrollment.getParentEnrollment().getStatus())) {
            return true;
        }

        enrollment.setParentEnrollment(null);

        String phoneNumber = subject.getPhoneNumber();
        LocalDate referenceDate = enrollment.getReferenceDate();
        String campaignName = enrollment.getCampaignName();
        Long stageId = enrollment.getStageId();

        Range<LocalDate> dateOfBirthRange = SubjectAgeRangeHelper.calculateDateOfBirthRange(subject.getDateOfBirth(),
                enrollment.getReferenceDate(), enrollment.getStageId(), configService.getConfig().getSubjectAgeRangeList());

        List<Subject> subjects = subjectDataService.findByPhoneNumberAndDateOfBirthRange(phoneNumber, dateOfBirthRange);

        Set<String> subjectIds = new HashSet<>();

        for (Subject s : subjects) {
            if (!s.getSubjectId().equals(subject.getSubjectId())) {
                subjectIds.add(s.getSubjectId());
            }
        }

        if (subjectIds.isEmpty()) {
            return false;
        }

        campaignName = changeCampaignNameForDuplicatedEnrollmentsPattern(campaignName);

        List<Enrollment> enrollments = enrollmentDataService.findByStatusReferenceDateStageIdCampaignNameAndSubjectIds(
                EnrollmentStatus.ENROLLED, referenceDate, stageId, campaignName, subjectIds);

        return findAndSetNewParent(enrollments, enrollment, subject.getSubjectId(), campaignName);
    }

    private void changeDuplicatedEnrollmentsWhenDateOfBirthChanged(Subject newSubject, Subject oldSubject) {
        if (newSubject.getDateOfBirth() != null && !newSubject.getDateOfBirth().equals(oldSubject.getDateOfBirth())) {
            changeDuplicatedEnrollmentsWhenSubjectDataChanged(newSubject);
        }
    }

    private void changeDuplicatedEnrollmentsWhenSubjectDataChanged(Subject subject) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());
        if (subjectEnrollments == null) {
            return;
        }

        try {
            for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
                Enrollment parentEnrollment = enrollment.getParentEnrollment();

                if (parentEnrollment != null) {
                    parentEnrollment.getDuplicatedEnrollments().remove(enrollment);
                    enrollment.setParentEnrollment(null);

                    if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus()) && !checkDuplicatedEnrollments(subject, enrollment)) {
                        scheduleJobsForEnrollment(enrollment, false);
                    }

                    enrollmentDataService.update(parentEnrollment);
                    enrollmentDataService.update(enrollment);
                } else if (enrollment.hasDuplicatedEnrollments()) {
                    if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
                        changeParentForDuplicatedEnrollments(enrollment, false);
                        if (checkDuplicatedEnrollments(subject, enrollment)) {
                            unscheduleJobsForEnrollment(enrollment);
                        }
                    } else {
                        enrollment.setDuplicatedEnrollments(null);
                    }
                    enrollmentDataService.update(enrollment);
                }
            }
        } catch (EbodacEnrollmentException e) {
            throw new EbodacEnrollmentException("Cannot change Participant phone number, because error occurred during changing parent for duplicated enrolments",
                    e, "ebodac.updateSubject.cannotChangeParent");
        }
    }

    private String changeCampaignNameForDuplicatedEnrollmentsPattern(String campaignName) {
        if (Pattern.compile(LONG_TERM_FOLLOW_UP_CAMPAIGN).matcher(campaignName).matches()) {
            return LONG_TERM_FOLLOW_UP_CAMPAIGN;
        } else if (Pattern.compile(FOLLOW_UP_CAMPAIGN).matcher(campaignName).matches()) {
            return FOLLOW_UP_CAMPAIGN;
        }

        return campaignName;
    }

    private boolean findAndSetNewParent(List<Enrollment> enrollments, Enrollment enrollment, String subjectId, String campaignName) {
        if (!enrollments.isEmpty()) {
            Enrollment parentEnrolment = null;
            if (enrollments.size() > 1) {
                for (Enrollment e: enrollments) {
                    if (e.getParentEnrollment() == null && e.hasDuplicatedEnrollments()) {
                        parentEnrolment = e;
                        break;
                    }
                }
            } else {
                parentEnrolment = enrollments.get(0);
            }

            if (parentEnrolment == null) {
                throw new EbodacEnrollmentException("Cannot enroll Participant with id: %s to Campaign with name: %s, because cannot find parent for duplicated enrollments",
                        "ebodac.enroll.error.cannotFindParent", subjectId, campaignName);
            }

            enrollment.setParentEnrollment(parentEnrolment);
            parentEnrolment.addDuplicatedEnrollment(enrollment);

            enrollmentDataService.update(parentEnrolment);
            return true;
        }

        return false;
    }

    private void rollbackIfWithdrawalDateRemoved(Subject oldSubject, Subject newSubject) {
        if (newSubject.getDateOfDisconStd() == null && oldSubject.getDateOfDisconStd() != null) {
            LOGGER.warn("Date of discontinuation Study was removed for Participant with id: {}", newSubject.getSubjectId());
            rollbackEnrollmentStatusOrEnrollNew(newSubject, false);
        }
        if (newSubject.getDateOfDisconVac() == null && oldSubject.getDateOfDisconVac() != null) {
            LOGGER.warn("Date of discontinuation Vaccination was removed for Participant with id: {}", newSubject.getSubjectId());
            rollbackEnrollmentStatusOrEnrollNew(newSubject, true);
        }
    }

    private void rollbackEnrollmentStatusOrEnrollNew(Subject subject, boolean boosterDiscon) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        if (subjectEnrollments == null) {
            enrollSubject(subject);
        } else if (subject.getVisits() != null) {
            EnrollmentStatus statusToRollback = EnrollmentStatus.WITHDRAWN_FROM_STUDY;
            if (boosterDiscon) {
                statusToRollback = EnrollmentStatus.UNENROLLED_FROM_BOOSTER;
            }
            for (Visit visit : subject.getVisits()) {
                if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
                    rollbackEnrollmentStatusOrEnrollNew(subjectEnrollments, subject, visit.getType().getMotechValue(), visit.getDate(), statusToRollback);
                    rollbackEnrollmentStatusOrEnrollNew(subjectEnrollments, subject, EbodacConstants.BOOSTER_RELATED_MESSAGES, visit.getDate(), statusToRollback);
                } else if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())) {
                    rollbackEnrollmentStatusOrEnrollNew(subjectEnrollments, subject, visit.getType().getMotechValue(), visit.getMotechProjectedDate(), statusToRollback);
                }
            }
            updateSubjectEnrollments(subjectEnrollments);
        }
    }

    private void rollbackEnrollmentStatusOrEnrollNew(SubjectEnrollments subjectEnrollments, Subject subject, String campaignName,
                                                     LocalDate referenceDate, EnrollmentStatus statusToRollback) {
        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);

        if (enrollment == null) {
            enrollNew(subject, campaignName, referenceDate);
        } else {
            rollbackEnrollmentStatus(subject, enrollment, statusToRollback);
        }
    }

    private void rollbackEnrollmentStatus(Subject subject, Enrollment enrollment, EnrollmentStatus statusToRollback) {
        if (statusToRollback.equals(enrollment.getStatus())) {
            EnrollmentStatus status = enrollment.getPreviousStatus();

            if (status == null) {
                status = EnrollmentStatus.ENROLLED;
            }

            enrollment.setStatus(status);

            try {
                if (EnrollmentStatus.ENROLLED.equals(status)) {
                    boolean disconVac = checkSubjectRequiredDataAndDisconVacDate(subject);

                    if (disconVac && checkIfCampaignInDisconVacList(enrollment)) {
                        enrollment.setStatus(EnrollmentStatus.UNENROLLED_FROM_BOOSTER);
                    } else if (!checkDuplicatedEnrollments(subject, enrollment)) {
                        scheduleJobsForEnrollment(enrollment, true);
                    }
                }

                enrollmentDataService.update(enrollment);
            } catch (EbodacEnrollmentException e) {
                LOGGER.debug("Cannot rollback status for Enrollment with id: {} and Campaign name: {}",
                        enrollment.getExternalId(), enrollment.getCampaignName(), e);
            }
        }
    }

    private void checkSubjectEnrollmentsStatus(SubjectEnrollments subjectEnrollments, String subjectId) {
        if (subjectEnrollments == null || !(EnrollmentStatus.UNENROLLED.equals(subjectEnrollments.getStatus()) || EnrollmentStatus.INITIAL.equals(subjectEnrollments.getStatus()))) {
            throw new EbodacEnrollmentException("Cannot enroll Participant, because no unenrolled Participant exist with id: %s",
                    "ebodac.enroll.error.noUnenrolledSubject", subjectId);
        }
    }

    private boolean checkIfSubjectRequiredDataWasAdded(Subject newSubject, Subject oldSubject) {
        if (newSubject.getVisits() != null && newSubject.getPrimerVaccinationDate() != null &&
                StringUtils.isNotBlank(newSubject.getPhoneNumber()) && newSubject.getLanguage() != null) {
            if (oldSubject.getLanguage() == null || StringUtils.isBlank(oldSubject.getPhoneNumber())) {
                return true;
            }
        }

        return false;
    }

    private void unenrollIfCampaignInDisconVacList(Subject subject) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());
        if (subjectEnrollments != null) {
            for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
                if (checkIfCampaignInDisconVacList(enrollment)) {
                    if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
                        try {
                            unscheduleJobsForEnrollmentAndChangeParent(enrollment);
                            enrollment.setStatus(EnrollmentStatus.UNENROLLED_FROM_BOOSTER);
                        } catch (EbodacEnrollmentException e) {
                            LOGGER.debug(e.getMessage(), e);
                        }
                    } else if (EnrollmentStatus.UNENROLLED.equals(enrollment.getStatus()) || EnrollmentStatus.INITIAL.equals(enrollment.getStatus())) {
                        enrollment.setStatus(EnrollmentStatus.UNENROLLED_FROM_BOOSTER);
                    }
                }
            }
            updateSubjectEnrollments(subjectEnrollments);
        }
    }

    private Long getActualStageId(Long stageId) {
        Long actualStageId = stageId;

        if (actualStageId == null) {
            actualStageId = configService.getConfig().getActiveStageId();
        }

        return actualStageId;
    }

    private void enrollBoosterRelatedCampaignsIfNeeded(Subject oldSubject, Subject newSubject) {
        List<Visit> visits = newSubject.getVisits();

        if (newSubject.getBoosterVaccinationDate() != null && oldSubject.getBoosterVaccinationDate() == null && visits != null) {
            for (Visit visit : visits) {
                try {
                    String campaignName = visit.getType().getMotechValue();
                    if (checkIfCampaignInBoosterRelatedMessagesList(campaignName, newSubject.getStageId())) {
                        enrollNew(newSubject, campaignName, visit.getMotechProjectedDate(), null);
                    }
                } catch (EbodacEnrollmentException e) {
                    LOGGER.debug(e.getMessage(), e);
                }
            }
        }
    }

    private boolean checkIfCampaignInBoosterRelatedMessagesList(Enrollment enrollment) {
        return configService.getConfig().getBoosterRelatedMessages().contains(enrollment.getCampaignNameWithStageId());
    }

    private boolean checkIfCampaignInBoosterRelatedMessagesList(String campaignName, Long stageId) {
        Long actualStageId = getActualStageId(stageId);
        String campaignNameWithStageId = campaignName;

        if (actualStageId == null) {
            throw new EbodacEnrollmentException("Participant StageId cannot be empty", "ebodac.enrollment.error.emptyStageId");
        }

        if (actualStageId > 1) {
            campaignNameWithStageId = campaignName + EbodacConstants.STAGE + actualStageId;
        }

        return configService.getConfig().getBoosterRelatedMessages().contains(campaignNameWithStageId);
    }

    @Autowired
    public void setSubjectEnrollmentsDataService(SubjectEnrollmentsDataService subjectEnrollmentsDataService) {
        this.subjectEnrollmentsDataService = subjectEnrollmentsDataService;
    }

    @Autowired
    public void setEnrollmentDataService(EnrollmentDataService enrollmentDataService) {
        this.enrollmentDataService = enrollmentDataService;
    }

    @Autowired
    public void setSubjectDataService(SubjectDataService subjectDataService) {
        this.subjectDataService = subjectDataService;
    }

    @Autowired
    public void setMessageCampaignService(MessageCampaignService messageCampaignService) {
        this.messageCampaignService = messageCampaignService;
    }

    @Autowired
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
