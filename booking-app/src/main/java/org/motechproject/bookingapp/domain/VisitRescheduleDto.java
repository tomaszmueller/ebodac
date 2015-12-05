package org.motechproject.bookingapp.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.bookingapp.util.CustomDateDeserializer;
import org.motechproject.bookingapp.util.CustomDateSerializer;
import org.motechproject.bookingapp.util.CustomTimeSerializer;
import org.motechproject.commons.date.model.Time;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.util.CustomVisitTypeDeserializer;
import org.motechproject.ebodac.util.CustomVisitTypeSerializer;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VisitRescheduleDto {

    private String location;

    private String participantId;

    private String participantName;

    private VisitType visitType;

    private LocalDate actualDate;

    private LocalDate plannedDate;

    private Time startTime;

    private Time endTime;

    private Long siteId;

    private Long clinicId;

    private Long visitId;

    private Long visitBookingDetailsId;

    private LocalDate earliestDate;

    private LocalDate latestDate;

    public VisitRescheduleDto() {
    }

    public VisitRescheduleDto(VisitBookingDetails details) {
        setParticipantId(details.getSubject().getSubjectId());
        setParticipantName(details.getSubject().getName());
        setVisitType(details.getVisit().getType());
        setActualDate(details.getVisit().getDate());
        setPlannedDate(details.getVisit().getMotechProjectedDate());
        setStartTime(details.getStartTime());
        setEndTime(details.getEndTime());
        setVisitId(details.getVisit().getId());
        setVisitBookingDetailsId(details.getId());
        if (details.getClinic() != null) {
            setClinicId(details.getClinic().getId());
            setSiteId(details.getClinic().getSite().getId());
            setLocation(details.getClinic().getSite().getSiteId() + " - " + details.getClinic().getLocation());
        }
    }

    public VisitRescheduleDto(VisitBookingDetails details, Map<VisitType, VisitScheduleOffset> offsetMap) {
        this(details);
        calculateEarliestAndLatestDate(offsetMap.get(details.getVisit().getType()), details.getSubject().getPrimerVaccinationDate());
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    @JsonSerialize(using = CustomVisitTypeSerializer.class)
    public VisitType getVisitType() {
        return visitType;
    }

    @JsonDeserialize(using = CustomVisitTypeDeserializer.class)
    public void setVisitType(VisitType visitType) {
        this.visitType = visitType;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getActualDate() {
        return actualDate;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setActualDate(LocalDate actualDate) {
        this.actualDate = actualDate;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getPlannedDate() {
        return plannedDate;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setPlannedDate(LocalDate plannedDate) {
        this.plannedDate = plannedDate;
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

    public Long getVisitId() {
        return visitId;
    }

    public void setVisitId(Long visitId) {
        this.visitId = visitId;
    }

    public Long getVisitBookingDetailsId() {
        return visitBookingDetailsId;
    }

    public void setVisitBookingDetailsId(Long visitBookingDetailsId) {
        this.visitBookingDetailsId = visitBookingDetailsId;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getEarliestDate() {
        return earliestDate;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setEarliestDate(LocalDate earliestDate) {
        this.earliestDate = earliestDate;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getLatestDate() {
        return latestDate;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setLatestDate(LocalDate latestDate) {
        this.latestDate = latestDate;
    }

    private void calculateEarliestAndLatestDate(VisitScheduleOffset offset, LocalDate primerVaccinationDate) {
        if (primerVaccinationDate != null && offset != null) {
            earliestDate = primerVaccinationDate.plusDays(offset.getEarliestDateOffset());
            latestDate = primerVaccinationDate.plusDays(offset.getLatestDateOffset());
        }
    }
}
