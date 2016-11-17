package org.motechproject.bookingapp.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.util.CustomDateDeserializer;
import org.motechproject.bookingapp.util.CustomDateSerializer;
import org.motechproject.bookingapp.util.CustomTimeSerializer;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.model.Time;
import org.motechproject.ebodac.domain.enums.VisitType;
import org.motechproject.ebodac.util.json.serializer.CustomVisitTypeDeserializer;
import org.motechproject.ebodac.util.json.serializer.CustomVisitTypeSerializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VisitRescheduleDto {

    private String location;

    private String participantId;

    private String participantName;

    private Long stageId;

    private VisitType visitType;

    private LocalDate actualDate;

    private LocalDate plannedDate;

    private Time startTime;

    private Long visitId;

    private Long visitBookingDetailsId;

    private LocalDate earliestDate;

    private LocalDate latestDate;

    private LocalDate earliestWindowDate;

    private Boolean ignoreDateLimitation;

    private Boolean boosterRelated;

    private Boolean notVaccinated;

    public VisitRescheduleDto() {
    }

    public VisitRescheduleDto(VisitBookingDetails details) {
        setParticipantId(details.getSubject().getSubjectId());
        setParticipantName(details.getSubject().getName());
        setStageId(details.getSubject().getStageId());
        setVisitType(details.getVisit().getType());
        setActualDate(details.getVisit().getDate());
        setPlannedDate(details.getVisit().getMotechProjectedDate());
        setStartTime(details.getStartTime());
        setVisitId(details.getVisit().getId());
        setVisitBookingDetailsId(details.getId());
        if (details.getIgnoreDateLimitation() != null) {
            setIgnoreDateLimitation(details.getIgnoreDateLimitation());
        } else {
            setIgnoreDateLimitation(false);
        }
        if (details.getClinic() != null) {
            setLocation(details.getSubject().getSiteName());
        }
    }

    public VisitRescheduleDto(VisitBookingDetails details, Range<LocalDate> dateRange,
                              Boolean boosterRelated, Boolean notVaccinated) {
        this(details);
        this.boosterRelated = boosterRelated;
        this.notVaccinated = notVaccinated;
        calculateEarliestAndLatestDate(dateRange);
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

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
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

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getEarliestWindowDate() {
        return earliestWindowDate;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setEarliestWindowDate(LocalDate earliestWindowDate) {
        this.earliestWindowDate = earliestWindowDate;
    }

    public Boolean getIgnoreDateLimitation() {
        return ignoreDateLimitation;
    }

    public void setIgnoreDateLimitation(Boolean ignoreDateLimitation) {
        this.ignoreDateLimitation = ignoreDateLimitation;
    }

    public Boolean getBoosterRelated() {
        return boosterRelated;
    }

    public void setBoosterRelated(Boolean boosterRelated) {
        this.boosterRelated = boosterRelated;
    }

    public Boolean getNotVaccinated() {
        return notVaccinated;
    }

    public void setNotVaccinated(Boolean notVaccinated) {
        this.notVaccinated = notVaccinated;
    }

    private void calculateEarliestAndLatestDate(Range<LocalDate> dateRange) {
        if (dateRange != null) {
            LocalDate maxDate = dateRange.getMax();
            LocalDate minDate = dateRange.getMin();
            earliestWindowDate = minDate;

            if (minDate.isBefore(LocalDate.now())) {
                minDate = LocalDate.now();
            }
            earliestDate = minDate;

            if (!maxDate.isBefore(LocalDate.now())) {
                latestDate = maxDate;
            }
        }
    }
}
