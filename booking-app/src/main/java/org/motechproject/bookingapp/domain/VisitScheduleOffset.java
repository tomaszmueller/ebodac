package org.motechproject.bookingapp.domain;

import org.motechproject.ebodac.domain.enums.VisitType;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.NonEditable;

import javax.jdo.annotations.Unique;

@Entity
@Unique(name = "visitTypeAndStageId", members = {"visitType", "stageId" })
public class VisitScheduleOffset {

    @Field(required = true)
    private VisitType visitType;

    @Field(required = true)
    private Long stageId;

    @Field(required = true)
    private Integer timeOffset;

    @Field(required = true)
    private Integer earliestDateOffset;

    @Field(required = true)
    private Integer latestDateOffset;

    @NonEditable(display = false)
    @Field
    private String owner;

    public VisitType getVisitType() {
        return visitType;
    }

    public void setVisitType(VisitType visitType) {
        this.visitType = visitType;
    }

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
    }

    public Integer getTimeOffset() {
        return timeOffset;
    }

    public void setTimeOffset(Integer timeOffset) {
        this.timeOffset = timeOffset;
    }

    public Integer getEarliestDateOffset() {
        return earliestDateOffset;
    }

    public void setEarliestDateOffset(Integer earliestDateOffset) {
        this.earliestDateOffset = earliestDateOffset;
    }

    public Integer getLatestDateOffset() {
        return latestDateOffset;
    }

    public void setLatestDateOffset(Integer latestDateOffset) {
        this.latestDateOffset = latestDateOffset;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
