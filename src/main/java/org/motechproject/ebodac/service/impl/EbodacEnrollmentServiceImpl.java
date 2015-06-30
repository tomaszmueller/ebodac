package org.motechproject.ebodac.service.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.messagecampaign.dao.CampaignEnrollmentDataService;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.messagecampaign.service.MessageCampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ebodacEnrollmentService")
public class EbodacEnrollmentServiceImpl implements EbodacEnrollmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EbodacEnrollmentServiceImpl.class);

    private CampaignEnrollmentDataService campaignEnrollmentDataService;
    private MessageCampaignService messageCampaignService;

    @Override
    public void enrollSubject(Subject subject, String campaignName, DateTime referenceDate, Boolean updateInactiveEnrollment) {
        String subjectID = subject.getSubjectId();

        if (subjectID == null || referenceDate == null || subject.getLanguage() == null
                || StringUtils.isBlank(subject.getPhoneNumber())) {
            LOGGER.debug("Cannot enroll Subject with id: {} because of insufficient subject data", subjectID);
        } else if (StringUtils.isBlank(campaignName)) {
            LOGGER.error("Cannot enroll Subject with id: {} because campaign name is null or empty", subjectID);
        } else {
            CampaignEnrollment existingEnrollment = campaignEnrollmentDataService.findByExternalIdAndCampaignName(subjectID, campaignName);

            if (existingEnrollment != null) {
                CampaignEnrollment enrollment = updateEnrollment(existingEnrollment, referenceDate, updateInactiveEnrollment);

                if (enrollment != null) {
                    messageCampaignService.unscheduleJobsForEnrollment(enrollment);
                    messageCampaignService.scheduleJobsForEnrollment(enrollment);
                }
            } else {
                CampaignEnrollment enrollment = registerEnrollment(subjectID, campaignName, referenceDate);

                if (enrollment != null) {
                    messageCampaignService.scheduleJobsForEnrollment(enrollment);
                }
            }
        }
    }

    @Override
    public void unenrollSubject(Subject subject, String campaignName) {
        unenrollSubject(subject.getSubjectId(), campaignName);
    }

    @Override
    public void unenrollSubject(String subjectId, String campaignName) {
        CampaignEnrollment enrollment = campaignEnrollmentDataService.findByExternalIdAndCampaignName(subjectId, campaignName);

        if (enrollment != null) {
            messageCampaignService.unscheduleJobsForEnrollment(enrollment);

            enrollment.setStatus(CampaignEnrollmentStatus.INACTIVE);
            campaignEnrollmentDataService.update(enrollment);
        } else {
            LOGGER.warn("No Subject with subjectId {} registered in campaign {}", subjectId, campaignName);
        }
    }

    private CampaignEnrollment updateEnrollment(CampaignEnrollment existingEnrollment, DateTime referenceDate, Boolean updateInactiveEnrollment) {

        if (CampaignEnrollmentStatus.ACTIVE.equals(existingEnrollment.getStatus()) && !referenceDate.toLocalDate().isEqual(existingEnrollment.getReferenceDate())) {
            existingEnrollment.setReferenceDate(referenceDate.toLocalDate());
            return campaignEnrollmentDataService.update(existingEnrollment);
        } else if (CampaignEnrollmentStatus.INACTIVE.equals(existingEnrollment.getStatus()) && updateInactiveEnrollment) {
            existingEnrollment.setReferenceDate(referenceDate.toLocalDate());
            existingEnrollment.setStatus(CampaignEnrollmentStatus.ACTIVE);
            return campaignEnrollmentDataService.update(existingEnrollment);
        }

        return null;
    }

    private CampaignEnrollment registerEnrollment(String externalId, String campaignName, DateTime referenceDate) {
        CampaignEnrollment enrollment = new CampaignEnrollment(externalId, campaignName);
        enrollment.setReferenceDate(referenceDate.toLocalDate());

        return campaignEnrollmentDataService.create(enrollment);
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
