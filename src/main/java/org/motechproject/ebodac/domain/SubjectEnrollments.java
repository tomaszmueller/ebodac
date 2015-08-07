package org.motechproject.ebodac.domain;

import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;

import java.util.HashSet;
import java.util.Set;

@Entity
public class SubjectEnrollments {

    @Field
    private Subject subject;

    @Field
    private EnrollmentStatus status;

    @Field
    @Cascade(delete = true)
    private Set<Enrollment> enrollments = new HashSet<>();

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

    @Ignore
    public Enrollment findEnrolmentByCampaignName(String campaignName) {
        for (Enrollment enrollment: enrollments) {
            if (enrollment.getCampaignName().equals(campaignName)) {
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
