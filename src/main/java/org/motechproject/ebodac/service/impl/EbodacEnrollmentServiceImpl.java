package org.motechproject.ebodac.service.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Enrollment;
import org.motechproject.ebodac.domain.EnrollmentStatus;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.SubjectEnrollments;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.exception.EbodacEnrollmentException;
import org.motechproject.ebodac.repository.EnrollmentDataService;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.messagecampaign.exception.CampaignNotFoundException;
import org.motechproject.messagecampaign.service.MessageCampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service("ebodacEnrollmentService")
public class EbodacEnrollmentServiceImpl implements EbodacEnrollmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EbodacEnrollmentServiceImpl.class);

    private SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    private EnrollmentDataService enrollmentDataService;

    private MessageCampaignService messageCampaignService;

    private ConfigService configService;

    @Override
    public void enrollScreening(Subject subject) {
        try {
            enrollSubject(subject, VisitType.SCREENING.getValue(), DateUtil.now().toLocalDate(), new Time(DateUtil.now().toLocalTime()), false, false, true);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
        }
    }

    @Override
    public void enrollSubject(Subject subject) {
        List<Visit> visits = subject.getVisits();

        if (visits != null) {
            for (Visit visit : visits) {
                if (visit.getMotechProjectedDate() == null && (visit.getDate() == null || VisitType.PRIME_VACCINATION_DAY.equals(visit.getType()))) {
                    enrollSubject(visit);
                }
            }
        }
    }

    @Override
    public void enrollSubject(String subjectId) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subjectId);
        if (subjectEnrollments == null || !EnrollmentStatus.UNENROLLED.equals(subjectEnrollments.getStatus())) {
            throw new EbodacEnrollmentException(String.format("Cannot unenroll Subject, because Subject with id: %s is not enrolled in any campaign",
                    subjectId));
        }

        Subject subject = subjectEnrollments.getSubject();

        if (subject.getDateOfDisconStd() != null) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s, because subject is withdrawn from study",
                    subject.getSubjectId()));
        } else if (subject.getLanguage() == null) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s, because subject language is empty",
                    subject.getSubjectId()));
        } else if (StringUtils.isBlank(subject.getPhoneNumber())) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s, because subject phone number is empty",
                    subject.getSubjectId()));
        }

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            if (!EnrollmentStatus.UNENROLLED.equals(enrollment.getStatus())) {
                continue;
            }
            if (enrollment.getReferenceDate() == null) {
                throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because reference date is empty",
                        subject.getSubjectId(), enrollment.getCampaignName()));
            } else if (subject.getDateOfDisconVac() != null) {
                String campaign = enrollment.getCampaignName();
                if (enrollment.getCampaignName().startsWith(VisitType.BOOST_VACCINATION_DAY.getValue())) {
                    campaign = VisitType.BOOST_VACCINATION_DAY.getValue();
                }
                if (configService.getConfig().getDisconVacCampaignsList().contains(campaign)) {
                    enrollment.setStatus(EnrollmentStatus.COMPLETED);
                    continue;
                }
            }

            try {
                messageCampaignService.scheduleJobsForEnrollment(enrollment.toCampaignEnrollment());
                enrollment.setStatus(EnrollmentStatus.ENROLLED);
                enrollmentDataService.update(enrollment);
            } catch (CampaignNotFoundException e) {
                throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s because Campaign with name: %s doesn't exist",
                        subject.getSubjectId(), enrollment.getCampaignName()), e);
            } catch (IllegalArgumentException e) {
                LOGGER.debug("Cannot enroll Subject with id: {} for Campaign with name: {}, because last message date is in the past. Changing enrollment status to Completed",
                        subject.getSubjectId(), enrollment.getCampaignName(), e);
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
            } catch (Exception e) {
                throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because of unknown exception",
                        subject.getSubjectId(), enrollment.getCampaignName()), e);
            }
        }
        updateSubjectEnrollments(subjectEnrollments);
    }

    @Override
    public void enrollSubjectToCampaign(String subjectId, String campaignName) {
        enrollSubjectToCampaign(subjectId, campaignName, null);
    }

    @Override
    public void enrollSubjectToCampaignWithNewDate(String subjectId, String campaignName, LocalDate date) {
        enrollSubjectToCampaign(subjectId, campaignName, date);
    }

    @Override
    public void enrollOrCompleteCampaignForSubject(Visit visit) {
        if (visit.getDate() != null && !VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
            completeCampaignForSubject(visit, false);
        } else if (visit.getMotechProjectedDate() == null) {
            enrollSubject(visit);
        }
    }

    @Override
    public void unenrollSubject(String subjectId) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subjectId);
        if (subjectEnrollments == null || !EnrollmentStatus.ENROLLED.equals(subjectEnrollments.getStatus())) {
            throw new EbodacEnrollmentException(String.format("Cannot unenroll Subject, because no Subject with id: %s is not enrolled in any campaign",
                    subjectId));
        }
        for (Enrollment enrollment: subjectEnrollments.getEnrollments()) {
            try {
                if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
                    messageCampaignService.unscheduleJobsForEnrollment(enrollment.toCampaignEnrollment());

                    enrollment.setStatus(EnrollmentStatus.UNENROLLED);
                    enrollmentDataService.update(enrollment);
                }
            } catch (CampaignNotFoundException e) {
                throw new EbodacEnrollmentException(String.format("Cannot unenroll Subject with id: %s because Campaign with name: %s doesn't exist",
                        subjectId, enrollment.getCampaignName()), e);
            } catch (Exception e) {
                throw new EbodacEnrollmentException(String.format("Cannot unenroll Subject with id: %s from Campaign with name: %s, because of unknown exception",
                        subjectId, enrollment.getCampaignName()), e);
            }
        }
        updateSubjectEnrollments(subjectEnrollments);
    }

    @Override
    public void unenrollSubject(String subjectId, String campaignName) {
        if (!unenrollAndSetStatus(subjectId, campaignName, EnrollmentStatus.UNENROLLED)) {
            throw new EbodacEnrollmentException(String.format("Cannot unenroll Subject, because no Subject with id: %s registered in Campaign with name: %s",
                    subjectId, campaignName));
        }
    }

    @Override
    public void reenrollSubject(Visit visit) {
        if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
            reenrollSubject(visit.getSubject(), visit.getType().getValue(), visit.getMotechProjectedDate(), false);
            reenrollSubject(visit.getSubject(), EbodacConstants.BOOSTER_RELATED_MESSAGES, visit.getMotechProjectedDate(), false);
        } else if (VisitType.BOOST_VACCINATION_DAY.equals(visit.getType())) {
            if (visit.getMotechProjectedDate() == null) {
                throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because reference date is empty",
                        visit.getSubject().getSubjectId(), visit.getType().getValue()));
            }
            String dayOfWeek = visit.getMotechProjectedDate().dayOfWeek().getAsText(Locale.ENGLISH);
            String campaignName = visit.getType().getValue() + " " + dayOfWeek;

            deleteEnrolmentsForAllBoostVaccinationDayCampaigns(visit.getSubject(), false);

            enrollSubject(visit.getSubject(), campaignName, visit.getMotechProjectedDate(), false);
        } else if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())) {
            reenrollSubject(visit.getSubject(), visit.getType().getValue(), visit.getMotechProjectedDate(), false);
        }
    }

    @Override
    public void reenrollSubjectWithNewDate(String subjectId, String campaignName, LocalDate date) {
        if (campaignName.startsWith(VisitType.BOOST_VACCINATION_DAY.getValue())) {
            if (!unenrollAndDeleteEnrolment(subjectId, campaignName, false)) {
                throw new EbodacEnrollmentException(String.format("Cannot re-enrol Subject, because no Subject with id %s registered in Campaign with name: %s",
                        subjectId, campaignName));
            }

            String dayOfWeek = date.dayOfWeek().getAsText(Locale.ENGLISH);
            String newName = VisitType.BOOST_VACCINATION_DAY.getValue() + " " + dayOfWeek;

            enrollSubjectToCampaign(subjectId, newName, date);
        } else {
            unenrollSubject(subjectId, campaignName);
            enrollSubjectToCampaign(subjectId, campaignName, date);
        }
    }

    @Override
    public void withdrawalSubject(Subject subject) {
        if (subject.getDateOfDisconStd() != null) {
            if (subject.getVisits() != null) {
                for (Visit visit: subject.getVisits()) {
                    completeCampaignForSubject(visit, true);
                }
            }
        } else if (subject.getDateOfDisconVac() != null) {
            for (String campaignName: configService.getConfig().getDisconVacCampaignsList()) {
                try {
                    if (VisitType.BOOST_VACCINATION_DAY.getValue().equals(campaignName)) {
                        completeAllBoostVaccinationDayCampaigns(subject);
                    } else {
                        completeCampaignForSubject(subject, campaignName);
                    }
                } catch (EbodacEnrollmentException e) {
                    LOGGER.debug(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void completeCampaign(String subjectId, String campaignName) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subjectId);
        if (subjectEnrollments != null) {
            Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);
            if (enrollment != null) {
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
                updateSubjectEnrollments(subjectEnrollments);
            }
        }
    }

    private void enrollSubject(Visit visit) {
        try {
            if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
                enrollSubject(visit.getSubject(), visit.getType().getValue(), visit.getDateProjected(), false);
                enrollSubject(visit.getSubject(), EbodacConstants.BOOSTER_RELATED_MESSAGES, visit.getDateProjected(), false);

                visit.setMotechProjectedDate(visit.getDateProjected());
            } else if (VisitType.BOOST_VACCINATION_DAY.equals(visit.getType())) {
                if (visit.getDateProjected() == null) {
                    throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because reference date is empty",
                            visit.getSubject().getSubjectId(), visit.getType().getValue()));
                }
                String dayOfWeek = visit.getDateProjected().dayOfWeek().getAsText(Locale.ENGLISH);
                String campaignName = visit.getType().getValue() + " " + dayOfWeek;

                enrollSubject(visit.getSubject(), campaignName, visit.getDateProjected(), false);

                visit.setMotechProjectedDate(visit.getDateProjected());
            } else if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())) {
                enrollSubject(visit.getSubject(), visit.getType().getValue(), visit.getDateProjected(), false);

                visit.setMotechProjectedDate(visit.getDateProjected());
            }
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
        }
    }

    private void enrollSubject(Subject subject, String campaignName, LocalDate referenceDate, boolean updateInactiveEnrollment) {
        enrollSubject(subject, campaignName, referenceDate, null, updateInactiveEnrollment, false, false);
    }

    private void enrollSubject(Subject subject, String campaignName, LocalDate referenceDate, Time deliverTime, boolean updateInactiveEnrollment, boolean updateOnly, boolean createOly) {
        String subjectID = subject.getSubjectId();

        if (StringUtils.isBlank(campaignName)) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s because Campaign name is empty", subjectID));
        } else if (subjectID == null) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject for Campaign with name: %s, because subject id is empty",
                    campaignName));
        } else if (referenceDate == null) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because reference date is empty",
                    subjectID, campaignName));
        } else if (subject.getDateOfDisconStd() != null) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because subject is withdrawn from study",
                    subjectID, campaignName));
        } else if (subject.getDateOfDisconVac() != null) {
            String campaign = campaignName;
            if (campaignName.startsWith(VisitType.BOOST_VACCINATION_DAY.getValue())) {
                campaign = VisitType.BOOST_VACCINATION_DAY.getValue();
            }
            if (configService.getConfig().getDisconVacCampaignsList().contains(campaign)) {
                throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because subject resigned form booster vaccination",
                        subjectID, campaignName));
            }
        } else if (subject.getLanguage() == null) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because subject language is empty",
                    subjectID, campaignName));
        } else if (StringUtils.isBlank(subject.getPhoneNumber())) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because subject phone number is empty",
                    subjectID, campaignName));
        } else {
            SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subjectID);
            if (subjectEnrollments == null) {
                subjectEnrollments = new SubjectEnrollments(subject);
            }

            Enrollment existingEnrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);

            try {
                if (existingEnrollment != null) {
                    if (createOly) {
                        throw new EbodacEnrollmentException(String.format("Cannot enroll Subject, because Subject with id %s already registered in Campaign with name: %s",
                                subjectID, campaignName));
                    }

                    if (referenceDate.isBefore(DateUtil.now().toLocalDate())) {
                        throw new EbodacEnrollmentException(String.format("Cannot re-enroll Subject with id: %s for Campaign with name: %s, because reference date is in the past",
                                subjectID, campaignName));
                    }

                    Enrollment enrollment = updateEnrollment(existingEnrollment, referenceDate, deliverTime, updateInactiveEnrollment);

                    if (enrollment != null) {
                        messageCampaignService.unscheduleJobsForEnrollment(enrollment.toCampaignEnrollment());
                        messageCampaignService.scheduleJobsForEnrollment(enrollment.toCampaignEnrollment());

                        updateSubjectEnrollments(subjectEnrollments);
                    }
                } else {
                    if (updateOnly) {
                        throw new EbodacEnrollmentException(String.format("Cannot re-enroll Subject, because no Subject with id %s registered in Campaign with name: %s",
                                subjectID, campaignName));
                    }
                    Enrollment enrollment = createEnrollment(subjectID, campaignName, referenceDate, deliverTime);

                    if (enrollment != null) {
                        messageCampaignService.scheduleJobsForEnrollment(enrollment.toCampaignEnrollment());

                        subjectEnrollments.addEnrolment(enrollment);
                        updateSubjectEnrollments(subjectEnrollments);
                    }
                }
            } catch (CampaignNotFoundException e) {
                throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s because Campaign with name: %s doesn't exist",
                        subjectID, campaignName), e);
            } catch (IllegalArgumentException e) {
                throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because last message date is in the past",
                        subjectID, campaignName), e);
            } catch (EbodacEnrollmentException e) {
                throw e;
            } catch (Exception e) {
                throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because of unknown exception",
                        subjectID, campaignName), e);
            }
        }
    }

    private void enrollSubjectToCampaign(String subjectId, String campaignName, LocalDate referenceDate) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subjectId);
        if (subjectEnrollments == null) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject, because not found unenrolled Subject with id: %s in campaign with name: %s",
                    subjectId, campaignName));
        }

        Subject subject = subjectEnrollments.getSubject();

        if (subject.getDateOfDisconStd() != null) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s, because subject is withdrawn from study",
                    subject.getSubjectId()));
        } else if (subject.getLanguage() == null) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s, because subject language is empty",
                    subject.getSubjectId()));
        } else if (StringUtils.isBlank(subject.getPhoneNumber())) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s, because subject phone number is empty",
                    subject.getSubjectId()));
        }

        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);
        if (enrollment == null && campaignName.startsWith(VisitType.BOOST_VACCINATION_DAY.getValue())) {
            enrollment = new Enrollment(subjectId, campaignName);
            enrollment.setStatus(EnrollmentStatus.UNENROLLED);
            subjectEnrollments.addEnrolment(enrollment);
        }

        if (enrollment == null) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject, because not found unenrolled Subject with id: %s in campaign with name: %s",
                    subject.getSubjectId(), campaignName));
        } else if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because subject is already enrolled in this campaign",
                    subject.getSubjectId(), enrollment.getCampaignName()));
        } else if (EnrollmentStatus.COMPLETED.equals(enrollment.getStatus())) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because this campaign is completed",
                    subject.getSubjectId(), enrollment.getCampaignName()));
        }
        if (subject.getDateOfDisconVac() != null) {
            String campaign = enrollment.getCampaignName();
            if (enrollment.getCampaignName().startsWith(VisitType.BOOST_VACCINATION_DAY.getValue())) {
                campaign = VisitType.BOOST_VACCINATION_DAY.getValue();
            }
            if (configService.getConfig().getDisconVacCampaignsList().contains(campaign)) {
                throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because subject resigned form booster vaccination",
                        subject.getSubjectId(), enrollment.getCampaignName()));
            }
        }

        if (referenceDate != null) {
            enrollment.setReferenceDate(referenceDate);
        } else if (enrollment.getReferenceDate() == null) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because reference date is empty",
                    subject.getSubjectId(), enrollment.getCampaignName()));
        }

        try {
            messageCampaignService.scheduleJobsForEnrollment(enrollment.toCampaignEnrollment());
            enrollment.setStatus(EnrollmentStatus.ENROLLED);
            enrollmentDataService.update(enrollment);
        } catch (CampaignNotFoundException e) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s because Campaign with name: %s doesn't exist",
                    subject.getSubjectId(), enrollment.getCampaignName()), e);
        } catch (IllegalArgumentException e) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because last message date is in the past",
                    subject.getSubjectId(), campaignName), e);
        } catch (Exception e) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because of unknown exception",
                    subject.getSubjectId(), enrollment.getCampaignName()), e);
        }

        updateSubjectEnrollments(subjectEnrollments);
    }

    private void reenrollSubject(Subject subject, String campaignName, LocalDate referenceDate, boolean updateInactiveEnrollment) {
        enrollSubject(subject, campaignName, referenceDate, null, updateInactiveEnrollment, true, false);
    }

    private void completeCampaignForSubject(Visit visit, boolean completeAllCampaigns) {
        try {
            if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
                if (completeAllCampaigns) {
                    completeCampaignForSubject(visit.getSubject(), visit.getType().getValue());
                    completeCampaignForSubject(visit.getSubject(), EbodacConstants.BOOSTER_RELATED_MESSAGES);
                }
            } else if (VisitType.BOOST_VACCINATION_DAY.equals(visit.getType())) {
                completeAllBoostVaccinationDayCampaigns(visit.getSubject());
            } else if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())) {
                if (!completeCampaignForSubject(visit.getSubject(), visit.getType().getValue())) {
                    throw new EbodacEnrollmentException(String.format("Cannot complete Campaign, because no Subject with id: %s registered in Campaign with name: %s",
                            visit.getSubject().getSubjectId(), visit.getType().getValue()));
                }
            }
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
        }
    }

    private void completeAllBoostVaccinationDayCampaigns(Subject subject) {
        String campaignName = VisitType.BOOST_VACCINATION_DAY.getValue();
        boolean enrolled = false;

        for (String day : EbodacConstants.DAYS_OF_WEEK) {
            if (completeCampaignForSubject(subject, campaignName + " " + day)) {
                enrolled = true;
            }
        }

        if (!enrolled) {
            throw new EbodacEnrollmentException(String.format("Cannot complete Campaign, because no Subject with id: %s registered in Campaign with name: %s",
                    subject.getSubjectId(), campaignName));
        }
    }

    private boolean completeCampaignForSubject(Subject subject, String campaignName) {
        return unenrollAndSetStatus(subject.getSubjectId(), campaignName, EnrollmentStatus.COMPLETED);
    }

    private boolean unenrollAndSetStatus(String subjectId, String campaignName, EnrollmentStatus status) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subjectId);
        if (subjectEnrollments == null) {
            return false;
        }
        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);

        if (enrollment == null) {
            return false;
        } else if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
            try {
                messageCampaignService.unscheduleJobsForEnrollment(enrollment.toCampaignEnrollment());

                enrollment.setStatus(status);
                updateSubjectEnrollments(subjectEnrollments);

                return true;
            } catch (CampaignNotFoundException e) {
                throw new EbodacEnrollmentException(String.format("Cannot unenroll Subject with id: %s because Campaign with name: %s doesn't exist",
                        subjectId, campaignName), e);
            } catch (Exception e) {
                throw new EbodacEnrollmentException(String.format("Cannot unenroll Subject with id: %s from Campaign with name: %s, because of unknown exception",
                        subjectId, campaignName), e);
            }
        } else if (EnrollmentStatus.UNENROLLED.equals(enrollment.getStatus()) && !status.equals(enrollment.getStatus())) {
            enrollment.setStatus(status);
            updateSubjectEnrollments(subjectEnrollments);
            return true;
        }

        return false;
    }

    private void deleteEnrolmentsForAllBoostVaccinationDayCampaigns(Subject subject, boolean deleteInactiveEnrollment) {
        String campaignName = VisitType.BOOST_VACCINATION_DAY.getValue();
        boolean enrolled = false;

        for (String day : EbodacConstants.DAYS_OF_WEEK) {
            if (unenrollAndDeleteEnrolment(subject.getSubjectId(), campaignName + " " + day, deleteInactiveEnrollment)) {
                enrolled = true;
            }
        }

        if (!enrolled) {
            throw new EbodacEnrollmentException(String.format("Cannot re-enrol Subject, because no Subject with id %s registered in Campaign with name: %s",
                    subject.getSubjectId(), campaignName));
        }
    }

    private boolean unenrollAndDeleteEnrolment(String subjectId, String campaignName, boolean deleteInactiveEnrollment) {
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subjectId);
        if (subjectEnrollments == null) {
            return false;
        }
        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(campaignName);

        if (enrollment == null) {
            return false;
        } else if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
            try {
                messageCampaignService.unscheduleJobsForEnrollment(enrollment.toCampaignEnrollment());

                subjectEnrollments.removeEnrolment(enrollment);
                updateSubjectEnrollments(subjectEnrollments);

                return true;
            } catch (CampaignNotFoundException e) {
                throw new EbodacEnrollmentException(String.format("Cannot unenroll Subject with id: %s because campaign with name: %s doesn't exist",
                        subjectId, campaignName), e);
            } catch (Exception e) {
                throw new EbodacEnrollmentException(String.format("Cannot unenroll Subject with id: %s from campaign with name: %s, because of unknown exception",
                        subjectId, campaignName), e);
            }
        } else if (deleteInactiveEnrollment && EnrollmentStatus.UNENROLLED.equals(enrollment.getStatus())) {
            subjectEnrollments.removeEnrolment(enrollment);
            updateSubjectEnrollments(subjectEnrollments);
            return true;
        }

        return false;
    }

    private Enrollment updateEnrollment(Enrollment existingEnrollment, LocalDate referenceDate, Time deliverTime, Boolean updateInactiveEnrollment) {

        if (EnrollmentStatus.ENROLLED.equals(existingEnrollment.getStatus()) && !referenceDate.isEqual(existingEnrollment.getReferenceDate())) {
            existingEnrollment.setReferenceDate(referenceDate);
            existingEnrollment.setDeliverTime(deliverTime);

            return existingEnrollment;
        } else if (EnrollmentStatus.UNENROLLED.equals(existingEnrollment.getStatus()) && updateInactiveEnrollment) {
            existingEnrollment.setReferenceDate(referenceDate);
            existingEnrollment.setStatus(EnrollmentStatus.ENROLLED);
            existingEnrollment.setDeliverTime(deliverTime);

            return existingEnrollment;
        }

        return null;
    }

    private Enrollment createEnrollment(String externalId, String campaignName, LocalDate referenceDate, Time deliverTime) {
        Enrollment enrollment = new Enrollment(externalId, campaignName);
        enrollment.setReferenceDate(referenceDate);
        enrollment.setDeliverTime(deliverTime);

        return enrollment;
    }

    private void updateSubjectEnrollments(SubjectEnrollments subjectEnrollments) {
        EnrollmentStatus status = EnrollmentStatus.COMPLETED;

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            if (EnrollmentStatus.ENROLLED.equals(enrollment.getStatus())) {
                status = EnrollmentStatus.ENROLLED;
            } else if (EnrollmentStatus.UNENROLLED.equals(enrollment.getStatus()) && !EnrollmentStatus.ENROLLED.equals(status)) {
                status = EnrollmentStatus.UNENROLLED;
            }
        }

        subjectEnrollments.setStatus(status);
        subjectEnrollmentsDataService.update(subjectEnrollments);
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
    public void setMessageCampaignService(MessageCampaignService messageCampaignService) {
        this.messageCampaignService = messageCampaignService;
    }

    @Autowired
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
