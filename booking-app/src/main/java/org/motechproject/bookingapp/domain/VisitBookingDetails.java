package org.motechproject.bookingapp.domain;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.NonEditable;

@Entity(maxFetchDepth = 4)
public class VisitBookingDetails {

    public static final String VISIT_TYPE_PROPERTY_NAME = "visit.type";
    public static final String VISIT_PLANNED_DATE_PROPERTY_NAME = "visit.motechProjectedDate";
    public static final String SUBJECT_PRIME_VACCINATION_DATE_PROPERTY_NAME = "subject.primerVaccinationDate";
    public static final String SUBJECT_NAME_PROPERTY_NAME = "subject.name";
    public static final String BOOKING_PLANNED_DATE_PROPERTY_NAME = "bookingPlannedDate";

    @Field
    private Long id;

    @Field
    private LocalDate bookingPlannedDate;

    @Field
    private LocalDate bookingActualDate;

    @Field
    private Time startTime;

    @Field
    private Time endTime;

    @Field
    private Clinic clinic;

    @Field(required = true)
    private Visit visit;

    @Field(required = true, displayName = "Participant")
    private Subject subject;

    @Field(required = true)
    @JsonBackReference
    private SubjectBookingDetails subjectBookingDetails;

    @NonEditable(display = false)
    @Field
    private String owner;

    public VisitBookingDetails() {
    }

    public VisitBookingDetails(LocalDate bookingPlannedDate, Visit visit) {
        this.bookingPlannedDate = bookingPlannedDate;
        this.visit = visit;
        this.subject = visit.getSubject();
    }

    public VisitBookingDetails(Visit visit, LocalDate bookingActualDate) {
        this.visit = visit;
        this.bookingActualDate = bookingActualDate;
        this.subject = visit.getSubject();
    }

    public VisitBookingDetails(Visit visit, SubjectBookingDetails subjectBookingDetails) {
        this.visit = visit;
        this.subject = visit.getSubject();
        this.subjectBookingDetails = subjectBookingDetails;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getBookingPlannedDate() {
        return bookingPlannedDate;
    }

    public void setBookingPlannedDate(LocalDate bookingPlannedDate) {
        this.bookingPlannedDate = bookingPlannedDate;
    }

    public LocalDate getBookingActualDate() {
        return bookingActualDate;
    }

    public void setBookingActualDate(LocalDate bookingActualDate) {
        this.bookingActualDate = bookingActualDate;
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

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
        if (visit != null) {
            this.subject = visit.getSubject();
        }
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public SubjectBookingDetails getSubjectBookingDetails() {
        return subjectBookingDetails;
    }

    public void setSubjectBookingDetails(SubjectBookingDetails subjectBookingDetails) {
        this.subjectBookingDetails = subjectBookingDetails;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
