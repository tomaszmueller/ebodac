package org.motechproject.bookingapp.dto;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.util.CustomBooleanDeserializer;
import org.motechproject.bookingapp.util.CustomBooleanSerializer;
import org.motechproject.bookingapp.util.CustomDateDeserializer;
import org.motechproject.bookingapp.util.CustomDateSerializer;
import org.motechproject.bookingapp.util.CustomTimeSerializer;
import org.motechproject.commons.date.model.Time;
import org.motechproject.ebodac.domain.enums.Gender;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.enums.VisitType;

public class PrimeVaccinationScheduleDto {

    private Long visitBookingDetailsId;

    private String location;

    private String participantId;

    private String participantName;

    private Gender participantGender;

    private Boolean femaleChildBearingAge;

    private LocalDate actualScreeningDate;

    private LocalDate bookingScreeningActualDate;

    private LocalDate date;

    private Time startTime;

    private Long visitId;

    private Boolean ignoreDateLimitation;

    public PrimeVaccinationScheduleDto() {
    }

    public PrimeVaccinationScheduleDto(VisitBookingDetails details) {

        for (Visit visit : details.getSubject().getVisits()) {
            if (VisitType.SCREENING.equals(visit.getType())) {
                setActualScreeningDate(visit.getDate());
                break;
            }
        }
        if (actualScreeningDate != null) {
            setBookingScreeningActualDate(actualScreeningDate);
        } else {
            for (VisitBookingDetails bookingDetails : details.getSubjectBookingDetails().getVisitBookingDetailsList()) {
                if (VisitType.SCREENING.equals(bookingDetails.getVisit().getType())) {
                    setBookingScreeningActualDate(bookingDetails.getBookingActualDate());
                    break;
                }
            }
        }

        setStartTime(details.getStartTime());
        setParticipantId(details.getSubject().getSubjectId());
        setParticipantName(details.getSubject().getName());
        setDate(details.getBookingPlannedDate());
        if (details.getSubject().getGender() == null || details.getSubject().getGender().equals(Gender.Female)) {
            setFemaleChildBearingAge(details.getSubjectBookingDetails().getFemaleChildBearingAge());
        } else {
            setFemaleChildBearingAge(false);
        }
        setVisitBookingDetailsId(details.getId());
        setVisitId(details.getVisit().getId());
        setParticipantGender(details.getSubject().getGender());
        if (details.getIgnoreDateLimitation() != null) {
            setIgnoreDateLimitation(details.getIgnoreDateLimitation());
        } else {
            setIgnoreDateLimitation(false);
        }
        if (details.getClinic() != null) {
            setLocation(details.getSubject().getSiteName());
        }
    }

    public Long getVisitBookingDetailsId() {
        return visitBookingDetailsId;
    }

    public void setVisitBookingDetailsId(Long visitBookingDetailsId) {
        this.visitBookingDetailsId = visitBookingDetailsId;
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

    public Gender getParticipantGender() {
        return participantGender;
    }

    public void setParticipantGender(Gender participantGender) {
        this.participantGender = participantGender;
    }

    @JsonSerialize(using = CustomBooleanSerializer.class)
    public Boolean getFemaleChildBearingAge() {
        return femaleChildBearingAge;
    }

    @JsonDeserialize(using = CustomBooleanDeserializer.class)
    public void setFemaleChildBearingAge(Boolean femaleChildBearingAge) {
        this.femaleChildBearingAge = femaleChildBearingAge;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getActualScreeningDate() {
        return actualScreeningDate;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setActualScreeningDate(LocalDate actualScreeningDate) {
        this.actualScreeningDate = actualScreeningDate;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getBookingScreeningActualDate() {
        return bookingScreeningActualDate;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setBookingScreeningActualDate(LocalDate bookingScreeningActualDate) {
        this.bookingScreeningActualDate = bookingScreeningActualDate;
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

    public Long getVisitId() {
        return visitId;
    }

    public void setVisitId(Long visitId) {
        this.visitId = visitId;
    }

    public Boolean getIgnoreDateLimitation() {
        return ignoreDateLimitation;
    }

    public void setIgnoreDateLimitation(Boolean ignoreDateLimitation) {
        this.ignoreDateLimitation = ignoreDateLimitation;
    }
}
