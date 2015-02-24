package org.motechproject.ebodac.service.impl;

import org.motechproject.ebodac.service.SubjectRegistrationService;
import org.motechproject.ebodac.repository.SubjectRegistrationDataService;
import org.motechproject.ebodac.domain.SubjectRegistration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the {@link org.motechproject.ebodac.service.SubjectRegistrationService} interface. Uses
 * {@link org.motechproject.ebodac.repository.SubjectRegistrationDataService} in order to retrieve and persist records.
 */
@Service("subjectRegistrationService")
public class SubjectRegistrationServiceImpl implements SubjectRegistrationService {

    @Autowired
    private SubjectRegistrationDataService subjectRegistrationDataService;

    @Override
    public void create(String name, String message) {
        subjectRegistrationDataService.create(
                new SubjectRegistration(name, message)
        );
    }

    @Override
    public void add(SubjectRegistration record) {
        subjectRegistrationDataService.create(record);
    }

    @Override
    public SubjectRegistration findRecordByName(String recordName) {
        SubjectRegistration record = subjectRegistrationDataService.findRegistrationByFirstName(recordName);
        if (null == record) {
            return null;
        }
        return record;
    }

    @Override
    public List<SubjectRegistration> getRecords() {
        return subjectRegistrationDataService.retrieveAll();
    }

    @Override
    public void update(SubjectRegistration record) {
        subjectRegistrationDataService.update(record);
    }

    @Override
    public void delete(SubjectRegistration record) {
        subjectRegistrationDataService.delete(record);
    }
}
