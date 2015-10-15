package org.motechproject.ebodac.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.motechproject.ebodac.util.CustomEnrollmentStatusSerializer;
import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.mds.annotations.NonEditable;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "ParticipantEnrollments", nonEditable = true, maxFetchDepth = 2)
public class SubjectEnrollments {

    @NonEditable
    @Field(displayName = "Participant")
    private Subject subject;

    @Field
    private EnrollmentStatus status;

    @NonEditable
    @Field
    @Cascade(delete = true)
    private Set<Enrollment> enrollments = new HashSet<>();

    @NonEditable(display = false)
    @Field
    private String owner;

    public SubjectEnrollments() {
    }

    public SubjectEnrollments(Subject subject) {
        this.subject = subject;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @JsonSerialize(using = CustomEnrollmentStatusSerializer.class)
    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public Set<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(Set<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Ignore
    public Enrollment findEnrolmentByCampaignName(String campaignName) {
        for (Enrollment enrollment: enrollments) {
            if (enrollment.getCampaignName().startsWith(campaignName)) {
                return enrollment;
            }
        }
        return null;
    }

    @Ignore
    public void addEnrolment(Enrollment enrollment) {
        enrollments.add(enrollment);
    }

    @Ignore
    public void removeEnrolment(Enrollment enrollment) {
        enrollments.remove(enrollment);
    }
}
