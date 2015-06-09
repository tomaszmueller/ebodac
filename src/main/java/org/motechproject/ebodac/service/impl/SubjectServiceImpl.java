package org.motechproject.ebodac.service.impl;

import org.dom4j.datatype.DatatypeAttribute;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the {@link org.motechproject.ebodac.service.SubjectService} interface. Uses
 * {@link org.motechproject.ebodac.repository.SubjectDataService} in order to retrieve and persist records.
 */
@Service("subjectService")
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectDataService subjectDataService;

    @Override
    public Subject createOrUpdate(Subject newSubject) {

        Subject subjectInDb = findSubjectBySubjectId(newSubject.getSubjectId());

        if (subjectInDb != null) {
            subjectInDb.setName(newSubject.getName());
            subjectInDb.setHouseholdName(newSubject.getHouseholdName());
            subjectInDb.setPhoneNumber(newSubject.getPhoneNumber());
            subjectInDb.setHeadOfHousehold(newSubject.getHeadOfHousehold());
            subjectInDb.setAddress(newSubject.getAddress());
            subjectInDb.setLanguage(newSubject.getLanguage());
            subjectInDb.setCommunity(newSubject.getCommunity());
            subjectInDb.setSiteId(newSubject.getSiteId());

            return update(subjectInDb, true);
        } else {
            return create(newSubject, true);
        }
    }

    @Override
    public Subject findSubjectByName(String name) {
        return subjectDataService.findSubjectByName(name);
    }

    @Override
    public Subject findSubjectBySubjectId(String subjectId) {
        return subjectDataService.findSubjectBySubjectId(subjectId);
    }

    @Override
    public List<Subject> findModifiedSubjects() {
        return subjectDataService.findSubjectsByModified(true);
    }

    @Override
    public List<Subject> getAll() {
        return subjectDataService.retrieveAll();
    }

    @Override
    public Subject create(Subject record, Boolean preserveModified) {
        return subjectDataService.create(record);
    }

    @Override
    public Subject update(Subject record, Boolean preserveModified) {
        return subjectDataService.update(record);
    }

    @Override
    public List<Subject> findSubjectsPrimerVaccinatedAtDay(DateTime date) {
        DateTime from = DateTime.parse(date.toString(DateTimeFormat.mediumDate()));
        DateTime to = from.plusDays(1).minusSeconds(1);
        Range<DateTime> range = new Range<>(from, to);
        return subjectDataService.findSubjectsByPrimerVaccinationDate(range);
    }

    @Override
    public List<Subject> findSubjectsBoosterVaccinatedAtDay(DateTime date) {
        DateTime from = DateTime.parse(date.toString(DateTimeFormat.mediumDate()));
        DateTime to = from.plusDays(1).minusSeconds(1);
        Range<DateTime> range = new Range<>(from, to);
        return subjectDataService.findSubjectsByBoosterVaccinationDate(range);
    }

    @Override
    public void delete(Subject record) {
        subjectDataService.delete(record);
    }
}
