package org.motechproject.ebodac.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity(recordHistory = true)
public class Visit {

    @Field
    private Subject subject;

    @Field
    private VisitType type;

    @Field
    private DateTime date;

    @Field
    private DateTime dateProjected;

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public VisitType getType() {
        return type;
    }

    public void setType(VisitType type) {
        this.type = type;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public DateTime getDateProjected() {
        return dateProjected;
    }

    public void setDateProjected(DateTime dateProjected) {
        this.dateProjected = dateProjected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Visit visit = (Visit) o;

        if (subject != null && visit.getSubject() != null) {
            if (!subject.getSubjectId().equals(visit.getSubject().getSubjectId())) {
                return false;
            }
        } else if (subject != null || visit.getSubject() != null) {
            return false;
        }
        return type == visit.type;
    }

    @Override
    public int hashCode() {
        int result = subject != null ? subject.getSubjectId().hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
