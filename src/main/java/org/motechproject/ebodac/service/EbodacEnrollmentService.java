package org.motechproject.ebodac.service;

import org.joda.time.DateTime;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;

public interface EbodacEnrollmentService {

    void enrollSubject(Subject subject);

    void enrollOrCompleteCampaignForSubject(Visit visit);

    void enrollSubject(Subject subject, String campaignName, DateTime referenceDate, boolean updateInactiveEnrollment);

    void unenrollSubject(Subject subject, String campaignName);

    void unenrollSubject(String subjectId, String campaignName);

    void reenrollSubject(Visit visit);

    void enrollScreening(Subject subject);
}
