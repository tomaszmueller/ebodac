package org.motechproject.ebodac.service.impl;

import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the {@link org.motechproject.ebodac.service.VisitService} interface. Uses
 * {@link org.motechproject.ebodac.repository.VisitDataService} in order to retrieve and persist records.
 */
@Service("visitService")
public class VisitServiceImpl implements VisitService {

    @Autowired
    private VisitDataService visitDataService;

    @Autowired
    private SubjectDataService subjectDataService;

    @Autowired
    private EbodacEnrollmentService ebodacEnrollmentService;

    @Override
    public Visit create(Visit visit) {
        return visitDataService.create(visit);
    }

    @Override
    public Visit update(Visit visit) {
        return visitDataService.update(visit);
    }

    @Override
    public Visit createOrUpdate(Visit visit) {
        if (visit.getSubject() != null) {
            if (visit.getSubject().getPrimerVaccinationDate() != null) {
                visit.setMotechProjectedDate(visit.getDateProjected());
            }
            List<Visit> visits = visit.getSubject().getVisits();
            if (visits.contains(visit)) {
                Visit existingVisit = visits.get(visits.indexOf(visit));
                if (existingVisit.visitDatesChanged(visit)) {
                    checkAndSetMotechProjectedDate(visit, existingVisit);
                    if (visit.getDate() == null && existingVisit.getDate() != null) {
                        ebodacEnrollmentService.rollbackOrRemoveEnrollment(visit);
                    }

                    existingVisit.setDate(visit.getDate());
                    existingVisit.setDateProjected(visit.getDateProjected());

                    ebodacEnrollmentService.enrollOrCompleteCampaignForSubject(existingVisit);

                    return visitDataService.update(existingVisit);
                } else {
                    return existingVisit;
                }
            }

            ebodacEnrollmentService.enrollOrCompleteCampaignForSubject(visit);
        }
        return visitDataService.create(visit);
    }

    @Override
    public void delete(Visit visit) {
        visitDataService.delete(visit);
    }

    @Override
    public Visit findVisitBySubjectIdAndVisitType(String subjectId, VisitType visitType) {
        Subject subject = subjectDataService.findBySubjectId(subjectId);
        List<Visit> visits = subject.getVisits();

        if (visits != null) {
            for (Visit visit: visits) {
                if (visitType.equals(visit.getType())) {
                    return visit;
                }
            }
        }
        return null;
    }

    private void checkAndSetMotechProjectedDate(Visit visit, Visit existingVisit) {
        if (visit.getMotechProjectedDate() != null && (existingVisit.getMotechProjectedDate() == null
                || !visit.getDateProjected().equals(existingVisit.getDateProjected()))) {
            existingVisit.setMotechProjectedDate(visit.getMotechProjectedDate());
        } else if (visit.getDateProjected() == null && existingVisit.getMotechProjectedDate() != null) {
            existingVisit.setMotechProjectedDate(null);
            ebodacEnrollmentService.unenrollAndRemoveEnrollment(existingVisit);
        }
    }
}
