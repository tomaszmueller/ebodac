package org.motechproject.ebodac.domain;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.ebodac.util.CustomDateDeserializer;
import org.motechproject.ebodac.util.CustomDateSerializer;
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

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }

    public Time getDeliverTime() {
        return deliverTime;
    }

    public void setDeliverTime(Time deliverTime) {
        this.deliverTime = deliverTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Enrollment)) {
            return false;
        }

        Enrollment that = (Enrollment) o;

        return externalId.equals(that.externalId) && campaignName.equals(that.campaignName);

    }

    @Override
    public int hashCode() {
        int result = externalId.hashCode();
        result = 31 * result + campaignName.hashCode();
        return result;
    }

    public CampaignEnrollment toCampaignEnrollment() {
        CampaignEnrollment enrollment = new CampaignEnrollment(externalId, campaignName);
        enrollment.setDeliverTime(deliverTime);
        enrollment.setReferenceDate(referenceDate);

        return enrollment;
    }
}
