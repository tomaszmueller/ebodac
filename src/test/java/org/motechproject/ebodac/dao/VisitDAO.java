package org.motechproject.ebodac.dao;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.util.CustomSubjectSerializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VisitDAO {

    private SubjectDAO subject;

    private VisitType type;

    private String date;

    private String owner;

    private DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    @JsonSerialize(using = CustomSubjectSerializer.class)
    public SubjectDAO getSubject() {
        return subject;
    }

    public void setSubject(SubjectDAO subject) {
        this.subject = subject;
    }

    public VisitType getType() {
        return type;
    }

    public void setType(VisitType type) {
        this.type = type;
    }

    public DateTime getDate() {
        return DateTime.parse(this.date, formatter);
    }

    public void setDate(DateTime date) {
        this.date = date.toString(formatter);
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Visit toVisit() {
        Visit ret = new Visit();
        ret.setDate(DateTime.parse(this.date, formatter));
        ret.setType(this.type);
        ret.setOwner(this.owner);
        ret.setSubject(this.subject.toSubject());
        return ret;
    }
}
