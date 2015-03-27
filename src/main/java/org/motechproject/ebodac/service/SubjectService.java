package org.motechproject.ebodac.service;

import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.PhoneType;
import org.motechproject.ebodac.domain.Subject;

import java.util.List;

/**
 * Service interface for CRUD on Subject
 */
public interface SubjectService {

    void create(String phoneNumber, String name, String householdName, String zetesId,
                String siteId, String address, Language language, String community);

    void add(Subject record);

    Subject findSubjectByName(String name);

    List<Subject> getAll();

    void delete(Subject record);

    void update(Subject record);
}
