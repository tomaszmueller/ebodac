package org.motechproject.ebodac.service.impl;

import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.repository.VisitDataService;
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
            List<Visit> visits = visit.getSubject().getVisits();
            if (visits.contains(visit)) {
                Visit existingVisit = visits.get(visits.indexOf(visit));
                if (existingVisit.visitDatesChanged(visit)) {
                    existingVisit.setDate(visit.getDate());
                    existingVisit.setDateProjected(visit.getDateProjected());
                    return visitDataService.update(existingVisit);
                } else {
                    return existingVisit;
                }
            }
        }
        return visitDataService.create(visit);
    }

    @Override
    public void delete(Visit visit) {
        visitDataService.delete(visit);
    }
}
