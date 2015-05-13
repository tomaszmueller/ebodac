package org.motechproject.ebodac.service;

import org.motechproject.ebodac.domain.Subject;
import org.motechproject.mds.annotations.InstanceLifecycleListener;
import org.motechproject.mds.domain.InstanceLifecycleListenerType;

import java.util.List;

/**
 * Service interface for CRUD on Subject
 */
public interface SubjectService {

    Subject createOrUpdate(Subject newSubject);

    Subject findSubjectByName(String name);

    Subject findSubjectBySubjectId(String subjectId);

    List<Subject> findModifiedSubjects();

    List<Subject> getAll();

    void delete(Subject record);

    Subject update(Subject record, Boolean preserveModified);

    @InstanceLifecycleListener(InstanceLifecycleListenerType.PRE_STORE)
    void subjectChanged(Subject subject);
}
