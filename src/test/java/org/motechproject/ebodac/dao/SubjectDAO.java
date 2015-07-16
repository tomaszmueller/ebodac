package org.motechproject.ebodac.dao;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubjectDAO {

    private String subjectId;

    private String name;

    private String address;

    private List<Visit> visits = new ArrayList<>();

    public SubjectDAO() {
    }

    public SubjectDAO(String subjectId, String name, String address) {
        this.subjectId = subjectId;
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public List<Visit> getVisits() {
        return visits;
    }

    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }

    public Subject toSubject() {
        Subject ret = new Subject();
        ret.setAddress(this.address);
        ret.setName(this.name);
        ret.setSubjectId(this.subjectId);

        return ret;
    }
}
