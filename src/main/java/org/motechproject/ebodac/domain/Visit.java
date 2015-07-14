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

    @Field
    private DateTime motechProjectedDate;

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

    public DateTime getMotechProjectedDate() {
        return motechProjectedDate;
    }

    public void setMotechProjectedDate(DateTime motechProjectedDate) {
        this.motechProjectedDate = motechProjectedDate;
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

    public boolean visitDatesChanged(Visit visit) {
        if (date != null ? !date.isEqual(visit.date) : visit.date != null) {
            return true;
        }
        if (dateProjected != null ? !dateProjected.isEqual(visit.dateProjected) : visit.dateProjected != null) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return type.getValue() +
                (getDateProjected() != null ? " / Planned Date: " + getDateProjected().toLocalDate().toString() : "") +
                (getDate() != null ? " / Actual Date: " + getDate().toLocalDate().toString() : "");
    }
}
