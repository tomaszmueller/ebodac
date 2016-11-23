package org.motechproject.ebodac.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.enums.EnrollmentStatus;
import org.motechproject.ebodac.domain.enums.VisitType;
import org.motechproject.ebodac.util.json.serializer.CustomDateDeserializer;
import org.motechproject.ebodac.util.json.serializer.CustomDateSerializer;
import org.motechproject.ebodac.util.json.serializer.CustomEnrollmentStatusSerializer;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.mds.annotations.NonEditable;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollment;

import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Unique;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Entity(nonEditable = true, maxFetchDepth = 1)
@Unique(name = "externalIdAndCampaignName", members = {"externalId", "campaignName" })
public class Enrollment {

    @Field(required = true)
    private String externalId;

    @Field(required = true)
    private String campaignName;

    @Field(required = true)
    private EnrollmentStatus status;

    @Field
    private EnrollmentStatus previousStatus;

    @Field(required = true)
    private LocalDate referenceDate;

    @Field
    private Long stageId;

    @Field
    private Time deliverTime;

    @NonEditable
    @Field
    private Enrollment parentEnrollment;

    @NonEditable
    @Field
    @Persistent(mappedBy = "parentEnrollment")
    private Set<Enrollment> duplicatedEnrollments = new HashSet<>();

    @NonEditable(display = false)
    @Field
    private String owner;

    private Enrollment() {
    }

    public Enrollment(String externalId, String campaignName, LocalDate referenceDate, Long stageId) {
        this.externalId = externalId;
        this.campaignName = campaignName;
        this.referenceDate = referenceDate;

        if (stageId == null) {
            throw new IllegalArgumentException("Participant StageId cannot be empty");
        }
        this.stageId = stageId;

        this.status = EnrollmentStatus.ENROLLED;
    }

    public Enrollment(String externalId, String campaignName, LocalDate referenceDate, Long stageId, Time deliverTime) {
        this(externalId, campaignName, referenceDate, stageId);
        this.deliverTime = deliverTime;
    }

    public Enrollment(String externalId, String campaignName, LocalDate referenceDate, Long stageId, EnrollmentStatus status) {
        this(externalId, campaignName, referenceDate, stageId);
        this.status = status;
    }

    public String getExternalId() {
        return externalId;
    }

    @Ignore
    @JsonProperty
    public String getCampaignNameWithBoostVacDayAndStageId() {
        return addBoostVacDayAndStageIdToCampaignName();
    }

    @Ignore
    @JsonIgnore
    public String getCampaignNameWithStageId() {
        return addStageIdToCampaignName();
    }

    public String getCampaignName() {
        return campaignName;
    }

    @JsonSerialize(using = CustomEnrollmentStatusSerializer.class)
    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        if (status != null && !status.equals(this.status)) {
            previousStatus = this.status;
        }
        this.status = status;
    }

    @JsonSerialize(using = CustomEnrollmentStatusSerializer.class)
    public EnrollmentStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(EnrollmentStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }

    public Long getStageId() {
        return stageId;
    }

    public Time getDeliverTime() {
        return deliverTime;
    }

    public void setDeliverTime(Time deliverTime) {
        this.deliverTime = deliverTime;
    }

    @JsonIgnore
    public Enrollment getParentEnrollment() {
        return parentEnrollment;
    }

    public void setParentEnrollment(Enrollment parentEnrollment) {
        this.parentEnrollment = parentEnrollment;
    }

    @JsonIgnore
    public Set<Enrollment> getDuplicatedEnrollments() {
        if (duplicatedEnrollments == null) {
            duplicatedEnrollments = new HashSet<>();
        }
        return duplicatedEnrollments;
    }

    public void setDuplicatedEnrollments(Set<Enrollment> duplicatedEnrollments) {
        this.duplicatedEnrollments = duplicatedEnrollments;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    @Override
    public String toString() {
        return externalId + " - " + campaignName + " - " + status;
    }

    public CampaignEnrollment toCampaignEnrollment() {
        String campaignNameWithBoostVacDayAndStageId = addBoostVacDayAndStageIdToCampaignName();
        CampaignEnrollment enrollment = new CampaignEnrollment(externalId, campaignNameWithBoostVacDayAndStageId);
        enrollment.setDeliverTime(deliverTime);
        enrollment.setReferenceDate(referenceDate);

        return enrollment;
    }

    @Ignore
    public void addDuplicatedEnrollment(Enrollment enrollment) {
        getDuplicatedEnrollments().add(enrollment);
    }

    @Ignore
    public boolean hasDuplicatedEnrollments() {
        return !getDuplicatedEnrollments().isEmpty();
    }

    private String addBoostVacDayAndStageIdToCampaignName() {
        String campaignNameWithBoostVacDayAndStageId = campaignName;

        if (campaignNameWithBoostVacDayAndStageId.startsWith(VisitType.BOOST_VACCINATION_DAY.getMotechValue())) {
            String dayOfWeek = referenceDate.dayOfWeek().getAsText(Locale.ENGLISH);
            campaignNameWithBoostVacDayAndStageId = VisitType.BOOST_VACCINATION_DAY.getMotechValue() + " " + dayOfWeek;
        }

        if (stageId > 1) {
            return campaignNameWithBoostVacDayAndStageId + EbodacConstants.STAGE + stageId;
        }

        return campaignNameWithBoostVacDayAndStageId;
    }

    private String addStageIdToCampaignName() {
        if (stageId > 1) {
            return campaignName + EbodacConstants.STAGE + stageId;
        }

        return campaignName;
    }
}
