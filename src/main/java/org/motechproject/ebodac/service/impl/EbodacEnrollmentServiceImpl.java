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
import org.motechproject.ebodac.exception.EbodacUnenrollmentException;
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
    public void enrollSubject(Subject subject) {
        List<Visit> visits = subject.getVisits();

        if (visits != null) {
            for (Visit visit : visits) {
                enrollSubject(visit);
            }
        }
    }

    @Override
    public void enrollOrCompleteCampaignForSubject(Visit visit) {
        if (visit.getDate() != null) {
            completeCampaignForSubject(visit);
        } else {
            enrollSubject(visit);
        }
    }

    private void enrollSubject(Visit visit) {
        try {
            if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
                enrollSubject(visit.getSubject(), visit.getType().getValue(), visit.getDateProjected(), false);
                enrollSubject(visit.getSubject(), EbodacConstants.MIDPOINT_MESSAGE, visit.getDateProjected(), false);
            } else if (VisitType.BOOST_VACCINATION_DAY.equals(visit.getType())) {
                String dayOfWeek = visit.getDateProjected().dayOfWeek().getAsText(Locale.ENGLISH);
                String campaignName = visit.getType().getValue() + " " + dayOfWeek;

                enrollSubject(visit.getSubject(), campaignName, visit.getDateProjected(), false);
            } else if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())) {
                enrollSubject(visit.getSubject(), visit.getType().getValue(), visit.getDateProjected(), false);
            }
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug("Cannot enroll subject", e);
        }
    }

    private void completeCampaignForSubject(Visit visit) {
        try {
            if (VisitType.BOOST_VACCINATION_DAY.equals(visit.getType())) {
                completeAllBoostVaccinationDayCampaigns(visit.getSubject());
            } else if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())
                    && !VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
                completeCampaignForSubject(visit.getSubject(), visit.getType().getValue());
            }
        } catch (EbodacUnenrollmentException e) {
            LOGGER.debug("Cannot unenroll subject", e);
        }
    }

    private void completeAllBoostVaccinationDayCampaigns(Subject subject) {
        String campaignName = VisitType.BOOST_VACCINATION_DAY.getValue();

        for (String day : EbodacConstants.DAYS_OF_WEEK) {
            completeCampaignForSubject(subject, campaignName + " " + day);
        }
    }

    private void enrollSubject(Subject subject, String campaignName, DateTime referenceDate, Time deliverTime, Boolean updateInactiveEnrollment) {
        String subjectID = subject.getSubjectId();

        if (StringUtils.isBlank(campaignName)) {
            LOGGER.error("Cannot enroll Subject with id: {} because campaign name is null or empty", subjectID);
        } else if (subjectID == null || referenceDate == null || subject.getLanguage() == null
                || StringUtils.isBlank(subject.getPhoneNumber())) {
            LOGGER.debug("Cannot enroll Subject with id: {} for campaign with name: {}, because of insufficient subject data", subjectID, campaignName);
        } else {
            CampaignEnrollment existingEnrollment = campaignEnrollmentDataService.findByExternalIdAndCampaignName(subjectID, campaignName);

            try {
                if (existingEnrollment != null) {
                    CampaignEnrollment enrollment = updateEnrollment(existingEnrollment, referenceDate, deliverTime, updateInactiveEnrollment);

                    if (enrollment != null) {
                        messageCampaignService.unscheduleJobsForEnrollment(enrollment);
                        messageCampaignService.scheduleJobsForEnrollment(enrollment);

                        campaignEnrollmentDataService.update(enrollment);
                    }
                } else {
                    CampaignEnrollment enrollment = createEnrollment(subjectID, campaignName, referenceDate, deliverTime);

                    if (enrollment != null) {
                        messageCampaignService.scheduleJobsForEnrollment(enrollment);

                        campaignEnrollmentDataService.create(enrollment);
                    }
                }
            } catch (CampaignNotFoundException e) {
                throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s because campaign with name: %s doesn't exist",
                        subjectID, campaignName), e);
            } catch (IllegalArgumentException e) {
                throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for campaign with name: %s, because last message date is in the past",
                        subjectID, campaignName), e);
            } catch (Exception e) {
                throw new EbodacEnrollmentException(String.format("Cannot enroll Subject with id: %s for campaign with name: %s",
                        subjectID, campaignName), e);
            }
        }
    }

    private void completeCampaignForSubject(Subject subject, String campaignName) {
        unenrollAndSetStatus(subject.getSubjectId(), campaignName, CampaignEnrollmentStatus.COMPLETED);
    }

    @Override
    public void enrollSubject(Subject subject, String campaignName, DateTime referenceDate, Boolean updateInactiveEnrollment) {
        enrollSubject(subject, campaignName, referenceDate, null, updateInactiveEnrollment);
    }

    @Override
    public void unenrollSubject(Subject subject, String campaignName) {
        unenrollSubject(subject.getSubjectId(), campaignName);
    }

    @Override
    public void unenrollSubject(String subjectId, String campaignName) {
        unenrollAndSetStatus(subjectId, campaignName, CampaignEnrollmentStatus.INACTIVE);
    }

    private void unenrollAndSetStatus(String subjectId, String campaignName, CampaignEnrollmentStatus status) {
        CampaignEnrollment enrollment = campaignEnrollmentDataService.findByExternalIdAndCampaignName(subjectId, campaignName);

        if (enrollment == null) {
            LOGGER.debug("No Subject with subjectId {} registered in campaign {}", subjectId, campaignName);
        } else if (CampaignEnrollmentStatus.ACTIVE.equals(enrollment.getStatus())) {
            try {
                messageCampaignService.unscheduleJobsForEnrollment(enrollment);

                enrollment.setStatus(status);
                campaignEnrollmentDataService.update(enrollment);
            } catch (CampaignNotFoundException e) {
                throw new EbodacUnenrollmentException(String.format("Cannot unenroll Subject with id: %s because campaign with name: %s doesn't exist",
                        subjectId, campaignName), e);
            } catch (Exception e) {
                throw new EbodacUnenrollmentException(String.format("Cannot unenroll Subject with id: %s from campaign with name: %s",
                        subjectId, campaignName), e);
            }
        } else if (CampaignEnrollmentStatus.INACTIVE.equals(enrollment.getStatus()) && !status.equals(enrollment.getStatus())) {
            enrollment.setStatus(status);
            campaignEnrollmentDataService.update(enrollment);
        }
    }

    @Override
    public void enrollScreening(Subject subject) {
        try {
            enrollSubject(subject, VisitType.SCREENING.getValue(), DateUtil.now(), new Time(DateUtil.now().toLocalTime()), false);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug("Cannot enroll subject", e);
        }
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
