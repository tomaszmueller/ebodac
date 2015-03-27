package org.motechproject.ebodac.service.impl;

import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.ebodac.domain.Subject;

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
    public void create(String phoneNumber, String name, String householdName, String zetesId,
                       String siteId, String address, Language language, String community) {
        subjectDataService.create(
                new Subject(phoneNumber, name, householdName, zetesId, siteId, address, language, community));
    }

    @Override
    public void add(Subject record) {
        subjectDataService.create(record);
    }

    @Override
    public Subject findSubjectByName(String FirstName) {
        Subject record = subjectDataService.findSubjectByName(FirstName);
        if (null == record) {
            return null;
        }
        return record;
    }

    @Override
    public List<Subject> getAll() {
        return subjectDataService.retrieveAll();
    }

    @Override
    public void update(Subject record) {
        subjectDataService.update(record);
    }

    @Override
    public void delete(Subject record) {
        subjectDataService.delete(record);
    }
}
