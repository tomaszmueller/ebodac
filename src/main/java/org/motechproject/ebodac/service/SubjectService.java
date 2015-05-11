package org.motechproject.ebodac.service;

import org.motechproject.ebodac.domain.Subject;

import java.util.List;

/**
 * Service interface for CRUD on Subject
 */
public interface SubjectService {

    void createOrUpdate(Subject newSubject);

    Subject findSubjectByName(String name);

    List<Subject> getAll();

    void delete(Subject record);

    void update(Subject record);
}
