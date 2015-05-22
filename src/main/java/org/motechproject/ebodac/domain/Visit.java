package org.motechproject.ebodac.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
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
}
