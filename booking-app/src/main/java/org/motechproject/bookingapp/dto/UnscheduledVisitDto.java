package org.motechproject.bookingapp.dto;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.bookingapp.domain.UnscheduledVisit;
import org.motechproject.bookingapp.util.CustomDateDeserializer;
import org.motechproject.bookingapp.util.CustomDateSerializer;
import org.motechproject.bookingapp.util.CustomTimeSerializer;
import org.motechproject.commons.date.model.Time;

public class UnscheduledVisitDto {

    private String id;
    private String participantId;
    private String clinicName;
    private LocalDate date;
    private Time startTime;
    private String purpose;

    public UnscheduledVisitDto () {} //NO CHECKSTYLE WhitespaceAround

    public UnscheduledVisitDto(UnscheduledVisit unscheduledVisit) {
        setId(unscheduledVisit.getId().toString());
        setParticipantId(unscheduledVisit.getSubject().getSubjectId());
        if (unscheduledVisit.getClinic() != null) {
            setClinicName(unscheduledVisit.getSubject().getSiteName());
        }
        setDate(unscheduledVisit.getDate());
        setStartTime(unscheduledVisit.getStartTime());
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

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

}
