package org.motechproject.ebodac.service.impl;

import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.PhoneType;
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
    public void create(String phoneNumber, String firstName, String lastName, Integer age, String address,
                       Language language, PhoneType phoneType) {
        subjectRegistrationDataService.create(
                new SubjectRegistration(phoneNumber, firstName, lastName, age, address, language, phoneType));
    }

    @Override
    public void add(SubjectRegistration record) {
        subjectRegistrationDataService.create(record);
    }

    @Override
    public SubjectRegistration findRegistrationByFirstName(String FirstName) {
        SubjectRegistration record = subjectRegistrationDataService.findRegistrationByFirstName(FirstName);
        if (null == record) {
            return null;
        }
        return record;
    }

    @Override
    public List<SubjectRegistration> getAll() {
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
