package org.motechproject.ebodac.service;

import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;

public interface EbodacEnrollmentService {

    void enrollScreening(Subject subject);

    void enrollSubject(Subject subject);

    void enrollSubject(String subjectId);

    void enrollOrCompleteCampaignForSubject(Visit visit);

    void unenrollSubject(Subject subject, String campaignName);

    void unenrollSubject(String subjectId);

    void unenrollSubject(String subjectId, String campaignName);

    void reenrollSubject(Visit visit);

    void withdrawalSubject(Subject subject);

    void completeCampaign(String subjectId, String campaignName);
}
