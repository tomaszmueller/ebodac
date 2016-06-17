package org.motechproject.ebodac.service.impl;

import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.enums.VisitType;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.service.VisitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the {@link org.motechproject.ebodac.service.VisitService} interface. Uses
 * {@link org.motechproject.ebodac.repository.VisitDataService} in order to retrieve and persist records.
 */
@Service("visitService")
public class VisitServiceImpl implements VisitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VisitServiceImpl.class);

    @Autowired
    private VisitDataService visitDataService;

    @Autowired
    private SubjectDataService subjectDataService;

    @Autowired
    private EbodacEnrollmentService ebodacEnrollmentService;

    @Autowired
    private ConfigService configService;

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
        try {
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

                if (checkStageId(visit)) {
                    return visit;
                }

                ebodacEnrollmentService.enrollOrCompleteCampaignForSubject(visit);
            }
            return visitDataService.create(visit);
        } catch (Exception ex) {
            LOGGER.error("Error when creating or updating Visit: " + visit.getType() + " for Subject: " + visit.getSubject().getSubjectId(), ex);
            return null;
        }
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

    private boolean checkStageId(Visit visit) {
        if (VisitType.UNSCHEDULED_VISIT.equals(visit.getType())) {
            return false;
        }

        Long activeStageId = configService.getConfig().getActiveStageId();
        Long stageId = visit.getSubject().getStageId();

        if (activeStageId == null && stageId == null) {
            LOGGER.warn("Visit of type: {} is not created for Participant with id: {}, because Participant Stage Id and Active Stage Id are empty",
                    visit.getType().getValue(), visit.getSubject().getSubjectId());
            return true;
        }
        if (activeStageId != null && stageId != null && !activeStageId.equals(stageId)) {
            LOGGER.warn("Visit of type: {} is not created for Participant with id: {}, because Participant Stage Id and Active Stage Id are different",
                    visit.getType().getValue(), visit.getSubject().getSubjectId());
            return true;
        }
        return false;
    }
}
