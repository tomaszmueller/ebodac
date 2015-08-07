package org.motechproject.ebodac.domain;

import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollment;

import javax.jdo.annotations.Unique;

@Entity
@Unique(name = "externalIdAndCampaignName", members = {"externalId", "campaignName" })
public class Enrollment {

    @Field(required = true)
    private String externalId;

    @Field(required = true)
    private String campaignName;

    private EnrollmentStatus status;

    @Field
    private LocalDate referenceDate;

    @Field
    private Time deliverTime;

    private Enrollment() {
    }

    public Enrollment(String externalId, String campaignName) {
        this.externalId = externalId;
        this.campaignName = campaignName;
        this.status = EnrollmentStatus.ENROLLED;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }

    public Time getDeliverTime() {
        return deliverTime;
    }

    public void setDeliverTime(Time deliverTime) {
        this.deliverTime = deliverTime;
    }

    public CampaignEnrollment toCampaignEnrollment() {
        CampaignEnrollment enrollment = new CampaignEnrollment(externalId, campaignName);
        enrollment.setDeliverTime(deliverTime);
        enrollment.setReferenceDate(referenceDate);

        return enrollment;
    }
}
