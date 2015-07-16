package org.motechproject.ebodac.service;

import org.joda.time.DateTime;
import org.motechproject.ebodac.domain.Subject;

public interface EbodacEnrollmentService {

    void enrollSubject(Subject subject, String campaignName, DateTime referenceDate, Boolean updateInactiveEnrollment);

    void unenrollSubject(Subject subject, String campaignName);

    void unenrollSubject(String subjectId, String campaignName);
}
