package org.motechproject.ebodac.service.impl;

import org.dom4j.datatype.DatatypeAttribute;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
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
    public Subject findSubjectById(Long id) {
        return subjectDataService.findById(id);
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
        DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);
        DateTime from = formatter.parseDateTime(date.toString(formatter));
        DateTime to = from.plusDays(1).minusSeconds(1);
        Range<DateTime> range = new Range<>(from, to);
        return subjectDataService.findSubjectsByPrimerVaccinationDate(range);
    }

    @Override
    public List<Subject> findSubjectsBoosterVaccinatedAtDay(DateTime date) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);
        DateTime from = formatter.parseDateTime(date.toString(formatter));
        DateTime to = from.plusDays(1).minusSeconds(1);
        Range<DateTime> range = new Range<>(from, to);
        return subjectDataService.findSubjectsByBoosterVaccinationDate(range);
    }

    @Override
    public DateTime findOldestPrimerVaccinationDate() {
        QueryParams queryParams = new QueryParams(new Order("primerVaccinationDate", Order.Direction.ASC));
        List<Subject> subjects = subjectDataService.retrieveAll(queryParams);
        if (subjects != null && !subjects.isEmpty()) {
            for(Subject subject : subjects) {
                if (subject.getPrimerVaccinationDate() != null) {
                    return subject.getPrimerVaccinationDate();
                }
            }
        }
        return DateTime.now().minusDays(2);
    }

    @Override
    public void delete(Subject record) {
        subjectDataService.delete(record);
    }
}
