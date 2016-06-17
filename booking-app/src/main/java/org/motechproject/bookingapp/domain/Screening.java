package org.motechproject.bookingapp.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.bookingapp.util.CustomDateSerializer;
import org.motechproject.bookingapp.util.CustomScreeningStatusSerializer;
import org.motechproject.bookingapp.util.CustomTimeSerializer;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.NonEditable;

@Entity(maxFetchDepth = 3)
public class Screening {

    public static final String DATE_PROPERTY_NAME = "date";

    @Field
    private Long id;

    @Field
    private Clinic clinic;

    @Field(required = true)
    private Volunteer volunteer;

    @Field(required = true)
    @JsonSerialize(using = CustomDateSerializer.class)
    private LocalDate date;

    @Field
    @JsonSerialize(using = CustomTimeSerializer.class)
    private Time startTime;

    @Field
    @JsonSerialize(using = CustomTimeSerializer.class)
    private Time endTime;

    @Field
    @JsonSerialize(using = CustomScreeningStatusSerializer.class)
    private ScreeningStatus status;

    @NonEditable(display = false)
    @Field
    private String owner;

    public Screening() {
        status = ScreeningStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
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

    public ScreeningStatus getStatus() {
        return status;
    }

    public void setStatus(ScreeningStatus status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
