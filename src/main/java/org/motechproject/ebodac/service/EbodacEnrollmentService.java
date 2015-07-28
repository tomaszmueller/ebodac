package org.motechproject.ebodac.service;

import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;

public interface EbodacEnrollmentService {

    void enrollScreening(Subject subject);

    void enrollSubject(Subject subject);

    void enrollOrCompleteCampaignForSubject(Visit visit);

    void unenrollSubject(Subject subject, String campaignName);

    void unenrollSubject(String subjectId, String campaignName);

    void reenrollSubject(Visit visit);
}
