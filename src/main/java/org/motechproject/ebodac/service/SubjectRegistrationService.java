package org.motechproject.ebodac.service;

import org.motechproject.ebodac.domain.SubjectRegistration;

import java.util.List;

/**
 * Service interface for CRUD on simple repository records.
 */
public interface SubjectRegistrationService {

    void create(String name, String message);

    void add(SubjectRegistration record);

    SubjectRegistration findRecordByName(String recordName);

    List<SubjectRegistration> getRecords();

    void delete(SubjectRegistration record);

    void update(SubjectRegistration record);
}
