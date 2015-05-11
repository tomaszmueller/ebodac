package org.motechproject.ebodac.service.impl;

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
    public void createOrUpdate(Subject newSubject) {

        Subject subjectInDb = subjectDataService.findSubjectBySubjectId(newSubject.getSubjectId());

        if (subjectInDb != null) {
            subjectInDb.setName(newSubject.getName());
            subjectInDb.setHouseholdName(newSubject.getHouseholdName());
            subjectInDb.setPhoneNumber(newSubject.getPhoneNumber());
            subjectInDb.setHeadOfHousehold(newSubject.getHeadOfHousehold());
            subjectInDb.setAddress(newSubject.getAddress());
            subjectInDb.setLanguage(newSubject.getLanguage());
            subjectInDb.setCommunity(newSubject.getCommunity());
            subjectInDb.setSiteId(newSubject.getSiteId());

            subjectDataService.update(subjectInDb);
        } else {
            subjectDataService.create(newSubject);
        }
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
