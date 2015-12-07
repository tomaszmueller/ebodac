package org.motechproject.bookingapp.domain;

import org.motechproject.ebodac.domain.Subject;
import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Persistent;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "ParticipantBookingDetails", maxFetchDepth = 4)
public class SubjectBookingDetails {

    @Field
    private Long id;

    @Field
    private Boolean femaleChildBearingAge;

    @Field(required = true)
    private Subject subject;

    @Field
    @Persistent(mappedBy = "subjectBookingDetails")
    @Cascade(delete = true)
    private List<VisitBookingDetails> visitBookingDetailsList = new ArrayList<>();

    public SubjectBookingDetails() {
    }

    public SubjectBookingDetails(Subject subject) {
        this.subject = subject;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getFemaleChildBearingAge() {
        return femaleChildBearingAge;
    }

    public void setFemaleChildBearingAge(Boolean femaleChildBearingAge) {
        this.femaleChildBearingAge = femaleChildBearingAge;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public List<VisitBookingDetails> getVisitBookingDetailsList() {
        return visitBookingDetailsList;
    }

    public void setVisitBookingDetailsList(List<VisitBookingDetails> visitBookingDetailsList) {
        this.visitBookingDetailsList = visitBookingDetailsList;
    }
}
