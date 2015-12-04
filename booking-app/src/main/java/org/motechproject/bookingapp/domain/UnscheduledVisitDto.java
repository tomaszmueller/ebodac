package org.motechproject.bookingapp.domain;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.bookingapp.util.CustomDateDeserializer;
import org.motechproject.bookingapp.util.CustomDateSerializer;
import org.motechproject.bookingapp.util.CustomTimeSerializer;
import org.motechproject.commons.date.model.Time;

public class UnscheduledVisitDto {

    private String id;
    private String participantId;
    private Long siteId;
    private Long clinicId;
    private String siteName;
    private String clinicName;
    private LocalDate date;
    private Time startTime;
    private Time endTime;
    private String purpose;

    public UnscheduledVisitDto () {} //NO CHECKSTYLE WhitespaceAround

    public UnscheduledVisitDto(UnscheduledVisit unscheduledVisit) {
        setId(unscheduledVisit.getId().toString());
        setParticipantId(unscheduledVisit.getSubject().getSubjectId());
        setSiteId(unscheduledVisit.getClinic().getSite().getId());
        setClinicId(unscheduledVisit.getClinic().getId());
        setSiteName(unscheduledVisit.getClinic().getSite().getSiteId());
        setClinicName(unscheduledVisit.getClinic().getLocation());
        setDate(unscheduledVisit.getDate());
        setStartTime(unscheduledVisit.getStartTime());
        setEndTime(unscheduledVisit.getEndTime());
        setPurpose(unscheduledVisit.getPurpose());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getDate() {
        return date;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @JsonSerialize(using = CustomTimeSerializer.class)
    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    @JsonSerialize(using = CustomTimeSerializer.class)
    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

}
