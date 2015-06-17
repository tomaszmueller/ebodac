package org.motechproject.ebodac.service;

import org.joda.time.DateTime;
import org.motechproject.ebodac.domain.Subject;

import java.util.List;

/**
 * Service interface for CRUD on Subject
 */
public interface SubjectService {

    Subject createOrUpdate(Subject newSubject);

    Subject findSubjectByName(String name);

    Subject findSubjectBySubjectId(String subjectId);

    Subject findSubjectById(Long id);

    List<Subject> findModifiedSubjects();

    List<Subject> getAll();

    void delete(Subject record);

    Subject create(Subject record, Boolean preserveModified);

    Subject update(Subject record, Boolean preserveModified);

    List<Subject> findSubjectsPrimerVaccinatedAtDay(DateTime date);

    List<Subject> findSubjectsBoosterVaccinatedAtDay(DateTime date);

    DateTime findOldestPrimerVaccinationDate();
}
