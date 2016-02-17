package org.motechproject.bookingapp.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.bookingapp.util.CustomDateSerializer;
import org.motechproject.bookingapp.util.CustomTimeSerializer;
import org.motechproject.commons.date.model.Time;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity(maxFetchDepth = 3)
public class UnscheduledVisit {

    @Field
    private Long id;

    @Field(required = true, displayName = "Participant")
    private Subject subject;

    @Field
    private Clinic clinic;

    @Field(required = true)
    @JsonSerialize(using = CustomDateSerializer.class)
    private LocalDate date;

    @Field(required = true)
    @JsonSerialize(using = CustomTimeSerializer.class)
    private Time startTime;

    @Field(required = true)
    @JsonSerialize(using = CustomTimeSerializer.class)
    private Time endTime;

    @Field
    private String purpose;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

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
