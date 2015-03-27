package org.motechproject.ebodac.service;

import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.PhoneType;
import org.motechproject.ebodac.domain.Subject;

import java.util.List;

/**
 * Service interface for CRUD on Subject
 */
public interface SubjectService {

    void create(String phoneNumber, String firstName, String lastName, Integer age, String address,
                Language language, PhoneType phoneType);

    void add(Subject record);

    Subject findRegistrationByFirstName(String firstName);

    List<Subject> getAll();

    void delete(Subject record);

    void update(Subject record);
}
