package org.motechproject.ebodac.service;

import org.motechproject.ebodac.domain.Subject;
import org.motechproject.mds.annotations.InstanceLifecycleListener;
import org.motechproject.mds.domain.InstanceLifecycleListenerType;

import java.util.List;

/**
 * Service interface for CRUD on Subject
 */
public interface SubjectService {

    void createOrUpdate(Subject newSubject);

    Subject findSubjectByName(String name);

    List<Subject> findModifiedSubjects();

    List<Subject> getAll();

    void delete(Subject record);

    void update(Subject record, Boolean preserveModified);

    @InstanceLifecycleListener(InstanceLifecycleListenerType.PRE_STORE)
    void subjectChanged(Subject subject);
}
