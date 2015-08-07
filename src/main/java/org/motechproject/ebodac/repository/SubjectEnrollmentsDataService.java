package org.motechproject.ebodac.repository;

import org.motechproject.ebodac.domain.SubjectEnrollments;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

public interface SubjectEnrollmentsDataService extends MotechDataService<SubjectEnrollments> {

    @Lookup
    SubjectEnrollments findEnrollmentBySubjectId(@LookupField(name = "subject.subjectId") String subjectId);
}
