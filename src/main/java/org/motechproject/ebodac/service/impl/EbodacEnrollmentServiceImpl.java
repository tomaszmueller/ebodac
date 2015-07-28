package org.motechproject.ebodac.service.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.exception.EbodacEnrollmentException;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.messagecampaign.dao.CampaignEnrollmentDataService;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
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

    private CampaignEnrollmentDataService campaignEnrollmentDataService;
    private MessageCampaignService messageCampaignService;

    @Override
    public void enrollScreening(Subject subject) {
        try {
            enrollSubject(subject, VisitType.SCREENING.getValue(), DateUtil.now(), new Time(DateUtil.now().toLocalTime()), false, false, true);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
        }
    }

    @Override
    public void enrollSubject(Subject subject) {
        List<Visit> visits = subject.getVisits();

        if (visits != null) {
            for (Visit visit : visits) {
                if (visit.getDate() == null && visit.getMotechProjectedDate() == null) {
                    enrollSubject(visit);
                }
            }
        }
    }

    @Override
    public void enrollOrCompleteCampaignForSubject(Visit visit) {
        if (visit.getDate() != null) {
            completeCampaignForSubject(visit);
        } else if (visit.getMotechProjectedDate() == null) {
            enrollSubject(visit);
        }
    }

    @Override
    public void unenrollSubject(Subject subject, String campaignName) {
        unenrollSubject(subject.getSubjectId(), campaignName);
    }

    @Override
    public void unenrollSubject(String subjectId, String campaignName) {
        if (!unenrollAndSetStatus(subjectId, campaignName, CampaignEnrollmentStatus.INACTIVE)) {
            throw new EbodacEnrollmentException(String.format("Cannot unenroll Subject, because no Subject with id: %s registered in Campaign with name: %s",
                    subjectId, campaignName));
        }
    }

    @Override
    public void reenrollSubject(Visit visit) {
        if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
            reenrollSubject(visit.getSubject(), visit.getType().getValue(), visit.getMotechProjectedDate(), false);
            reenrollSubject(visit.getSubject(), EbodacConstants.MIDPOINT_MESSAGE, visit.getMotechProjectedDate(), false);
        } else if (VisitType.BOOST_VACCINATION_DAY.equals(visit.getType())) {
            String dayOfWeek = visit.getMotechProjectedDate().dayOfWeek().getAsText(Locale.ENGLISH);
            String campaignName = visit.getType().getValue() + " " + dayOfWeek;

            deleteEnrolmentsForAllBoostVaccinationDayCampaigns(visit.getSubject(), false);

            enrollSubject(visit.getSubject(), campaignName, visit.getMotechProjectedDate(), false);
        } else if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())) {
            reenrollSubject(visit.getSubject(), visit.getType().getValue(), visit.getMotechProjectedDate(), false);
        }
    }

    private void enrollSubject(Visit visit) {
        try {
            if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
                enrollSubject(visit.getSubject(), visit.getType().getValue(), visit.getDateProjected(), false);
                enrollSubject(visit.getSubject(), EbodacConstants.MIDPOINT_MESSAGE, visit.getDateProjected(), false);

                visit.setMotechProjectedDate(visit.getDateProjected());
            } else if (VisitType.BOOST_VACCINATION_DAY.equals(visit.getType())) {
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

    private void enrollSubject(Subject subject, String campaignName, DateTime referenceDate, boolean updateInactiveEnrollment) {
        enrollSubject(subject, campaignName, referenceDate, null, updateInactiveEnrollment, false, false);
    }

    private void enrollSubject(Subject subject, String campaignName, DateTime referenceDate, Time deliverTime, boolean updateInactiveEnrollment, boolean updateOnly, boolean createOly) {
        String subjectID = subject.getSubjectId();

        if (StringUtils.isBlank(campaignName)) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s because Campaign name is null or empty", subjectID));
        } else if (subjectID == null) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject for Campaign with name: %s, because subject id is null",
                    campaignName));
        } else if (referenceDate == null) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because reference date is nul",
                    subjectID, campaignName));
        } else if (subject.getLanguage() == null) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because subject language is null",
                    subjectID, campaignName));
        } else if (StringUtils.isBlank(subject.getPhoneNumber())) {
            throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s, because subject phone number is null",
                    subjectID, campaignName));
        } else {
            CampaignEnrollment existingEnrollment = campaignEnrollmentDataService.findByExternalIdAndCampaignName(subjectID, campaignName);

            try {
                if (existingEnrollment != null) {
                    if (createOly) {
                        throw new EbodacEnrollmentException(String.format("Cannot enroll Subject, because Subject with id %s already registered in Campaign with name: %s",
                                subjectID, campaignName));
                    }

                    if (referenceDate.isBeforeNow()) {
                        throw new EbodacEnrollmentException(String.format("Cannot re-enroll Subject with id: %s for Campaign with name: %s, because reference date is in the past",
                                subjectID, campaignName));
                    }

                    CampaignEnrollment enrollment = updateEnrollment(existingEnrollment, referenceDate, deliverTime, updateInactiveEnrollment);

                    if (enrollment != null) {
                        messageCampaignService.unscheduleJobsForEnrollment(enrollment);
                        messageCampaignService.scheduleJobsForEnrollment(enrollment);

                        campaignEnrollmentDataService.update(enrollment);
                    }
                } else {
                    if (updateOnly) {
                        throw new EbodacEnrollmentException(String.format("Cannot re-enroll Subject, because no Subject with id %s registered in Campaign with name: %s",
                                subjectID, campaignName));
                    }
                    CampaignEnrollment enrollment = createEnrollment(subjectID, campaignName, referenceDate, deliverTime);

                    if (enrollment != null) {
                        messageCampaignService.scheduleJobsForEnrollment(enrollment);

                        campaignEnrollmentDataService.create(enrollment);
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
                throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for Campaign with name: %s",
                        subjectID, campaignName), e);
            }
        }
    }

    private void reenrollSubject(Subject subject, String campaignName, DateTime referenceDate, boolean updateInactiveEnrollment) {
        enrollSubject(subject, campaignName, referenceDate, null, updateInactiveEnrollment, true, false);
    }

    private void completeCampaignForSubject(Visit visit) {
        try {
            if (VisitType.BOOST_VACCINATION_DAY.equals(visit.getType())) {
                completeAllBoostVaccinationDayCampaigns(visit.getSubject());
            } else if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())
                    && !VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
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
        return unenrollAndSetStatus(subject.getSubjectId(), campaignName, CampaignEnrollmentStatus.COMPLETED);
    }

    private boolean unenrollAndSetStatus(String subjectId, String campaignName, CampaignEnrollmentStatus status) {
        CampaignEnrollment enrollment = campaignEnrollmentDataService.findByExternalIdAndCampaignName(subjectId, campaignName);

        if (enrollment == null) {
            return false;
        } else if (CampaignEnrollmentStatus.ACTIVE.equals(enrollment.getStatus())) {
            try {
                messageCampaignService.unscheduleJobsForEnrollment(enrollment);

                enrollment.setStatus(status);
                campaignEnrollmentDataService.update(enrollment);

                return true;
            } catch (CampaignNotFoundException e) {
                throw new EbodacEnrollmentException(String.format("Cannot unenroll Subject with id: %s because Campaign with name: %s doesn't exist",
                        subjectId, campaignName), e);
            } catch (Exception e) {
                throw new EbodacEnrollmentException(String.format("Cannot unenroll Subject with id: %s from Campaign with name: %s",
                        subjectId, campaignName), e);
            }
        } else if (CampaignEnrollmentStatus.INACTIVE.equals(enrollment.getStatus()) && !status.equals(enrollment.getStatus())) {
            enrollment.setStatus(status);
            campaignEnrollmentDataService.update(enrollment);
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
        CampaignEnrollment enrollment = campaignEnrollmentDataService.findByExternalIdAndCampaignName(subjectId, campaignName);

        if (enrollment == null) {
            return false;
        } else if (CampaignEnrollmentStatus.ACTIVE.equals(enrollment.getStatus())) {
            try {
                messageCampaignService.unscheduleJobsForEnrollment(enrollment);

                campaignEnrollmentDataService.delete(enrollment);

                return true;
            } catch (CampaignNotFoundException e) {
                throw new EbodacEnrollmentException(String.format("Cannot unenroll Subject with id: %s because campaign with name: %s doesn't exist",
                        subjectId, campaignName), e);
            } catch (Exception e) {
                throw new EbodacEnrollmentException(String.format("Cannot unenroll Subject with id: %s from campaign with name: %s",
                        subjectId, campaignName), e);
            }
        } else if (deleteInactiveEnrollment && CampaignEnrollmentStatus.INACTIVE.equals(enrollment.getStatus())) {
            campaignEnrollmentDataService.delete(enrollment);
            return true;
        }

        return false;
    }

    private CampaignEnrollment updateEnrollment(CampaignEnrollment existingEnrollment, DateTime referenceDate, Time deliverTime, Boolean updateInactiveEnrollment) {

        if (CampaignEnrollmentStatus.ACTIVE.equals(existingEnrollment.getStatus()) && !referenceDate.toLocalDate().isEqual(existingEnrollment.getReferenceDate())) {
            existingEnrollment.setReferenceDate(referenceDate.toLocalDate());
            existingEnrollment.setDeliverTime(deliverTime);

            return existingEnrollment;
        } else if (CampaignEnrollmentStatus.INACTIVE.equals(existingEnrollment.getStatus()) && updateInactiveEnrollment) {
            existingEnrollment.setReferenceDate(referenceDate.toLocalDate());
            existingEnrollment.setStatus(CampaignEnrollmentStatus.ACTIVE);
            existingEnrollment.setDeliverTime(deliverTime);

            return existingEnrollment;
        }

        return null;
    }

    private CampaignEnrollment createEnrollment(String externalId, String campaignName, DateTime referenceDate, Time deliverTime) {
        CampaignEnrollment enrollment = new CampaignEnrollment(externalId, campaignName);
        enrollment.setReferenceDate(referenceDate.toLocalDate());
        enrollment.setDeliverTime(deliverTime);

        return enrollment;
    }

    @Autowired
    public void setCampaignEnrollmentDataService(CampaignEnrollmentDataService campaignEnrollmentDataService) {
        this.campaignEnrollmentDataService = campaignEnrollmentDataService;
    }

    @Autowired
    public void setMessageCampaignService(MessageCampaignService messageCampaignService) {
        this.messageCampaignService = messageCampaignService;
    }
}
