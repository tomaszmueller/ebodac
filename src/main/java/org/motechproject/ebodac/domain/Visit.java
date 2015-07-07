package org.motechproject.ebodac.domain;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.motechproject.ebodac.util.CustomDateDeserializer;
import org.motechproject.ebodac.util.CustomDateSerializer;
import org.motechproject.ebodac.util.CustomSubjectSerializer;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.NonEditable;

@Entity(recordHistory = true)
public class Visit {

    @NonEditable
    @Field
    private Subject subject;

    @NonEditable
    @Field
    private VisitType type;

    @NonEditable
    @Field
    private DateTime date;

    @NonEditable
    @Field
    private DateTime dateProjected;

    @NonEditable(display = false)
    @Field
    private String owner;

    @JsonSerialize(using = CustomSubjectSerializer.class)
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

    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    @Override
    public String toString() {
        return type.toString() +
                (getDateProjected() != null ? " / Date Planned: " + getDateProjected().toLocalDate().toString() : "") +
                (getDate() != null ? " / Date Actual: " + getDate().toLocalDate().toString() : "");
    }
}
