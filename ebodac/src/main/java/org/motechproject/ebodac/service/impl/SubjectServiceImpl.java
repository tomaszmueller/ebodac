package org.motechproject.ebodac.service.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.service.ReportUpdateService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the {@link org.motechproject.ebodac.service.SubjectService} interface. Uses
 * {@link org.motechproject.ebodac.repository.SubjectDataService} in order to retrieve and persist records.
 */
@Service("subjectService")
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectDataService subjectDataService;

    @Autowired
    private VisitDataService visitDataService;

    @Autowired
    private ReportUpdateService reportUpdateService;

    @Autowired
    private EbodacEnrollmentService ebodacEnrollmentService;

    @Autowired
    private EventRelay eventRelay;

    @Override
    public Subject createOrUpdateForZetes(Subject newSubject) {

        Subject subjectInDb = findSubjectBySubjectId(newSubject.getSubjectId());

        reportUpdateService.addReportsToUpdateIfNeeded(subjectInDb, newSubject);

        if (subjectInDb != null) {
            if (subjectInDb.equalsForZetes(newSubject)) {
                return subjectInDb;
            }

            if (StringUtils.isNotBlank(newSubject.getSiteId()) && !newSubject.getSiteId().equals(subjectInDb.getSiteId())) {
                sendSiteIdChangedEvent(newSubject.getSubjectId(), newSubject.getSiteId());
            }

            subjectInDb.setName(newSubject.getName());
            subjectInDb.setHouseholdName(newSubject.getHouseholdName());
            subjectInDb.setPhoneNumber(newSubject.getPhoneNumber());
            subjectInDb.setHeadOfHousehold(newSubject.getHeadOfHousehold());
            subjectInDb.setAddress(newSubject.getAddress());
            subjectInDb.setLanguage(newSubject.getLanguage());
            subjectInDb.setCommunity(newSubject.getCommunity());
            subjectInDb.setSiteId(newSubject.getSiteId());
            subjectInDb.setSiteName(newSubject.getSiteName());
            subjectInDb.setChiefdom(newSubject.getChiefdom());
            subjectInDb.setSection(newSubject.getSection());
            subjectInDb.setDistrict(newSubject.getDistrict());

            ebodacEnrollmentService.enrollSubject(subjectInDb);

            subjectInDb = update(subjectInDb);
        } else {
            subjectInDb = create(newSubject);
        }

        return subjectInDb;
    }

    @Override
    public Subject createOrUpdateForRave(Subject newSubject) {

        Subject subjectInDb = findSubjectBySubjectId(newSubject.getSubjectId());

        reportUpdateService.addReportsToUpdateIfNeeded(subjectInDb, newSubject);

        if (subjectInDb != null) {
            if (subjectInDb.equalsForRave(newSubject)) {
                return subjectInDb;
            }

            ebodacEnrollmentService.withdrawalOrEnrollSubject(subjectInDb, newSubject);

            subjectInDb.setGender(newSubject.getGender());
            subjectInDb.setStageId(newSubject.getStageId());
            subjectInDb.setDateOfBirth(newSubject.getDateOfBirth());
            subjectInDb.setPrimerVaccinationDate(newSubject.getPrimerVaccinationDate());
            subjectInDb.setBoosterVaccinationDate(newSubject.getBoosterVaccinationDate());
            subjectInDb.setDateOfDisconVac(newSubject.getDateOfDisconVac());
            subjectInDb.setDateOfDisconStd(newSubject.getDateOfDisconStd());

            return update(subjectInDb);
        } else {
            return create(newSubject);
        }
    }

    @Override
    public List<Subject> findSubjectByName(String name) {
        return subjectDataService.findByName(name);
    }

    @Override
    public Subject findSubjectBySubjectId(String subjectId) {
        return subjectDataService.findBySubjectId(subjectId);
    }

    @Override
    public Subject findSubjectById(Long id) {
        return subjectDataService.findById(id);
    }

    @Override
    public List<Subject> findByStageId(Long stageId) {
        return subjectDataService.findByStageId(stageId);
    }

    @Override
    public List<Subject> findModifiedSubjects() {
        return subjectDataService.findByModified(true);
    }

    @Override
    public List<Subject> getAll() {
        return subjectDataService.retrieveAll();
    }

    @Override
    public Subject create(Subject record) {
        return subjectDataService.create(record);
    }

    @Override
    public Subject update(Subject record) {
        return subjectDataService.update(record);
    }

    @Override
    public List<Subject> findSubjectsPrimerVaccinatedAtDay(LocalDate date) {
        return subjectDataService.findByPrimerVaccinationDate(date);
    }

    @Override
    public List<Subject> findSubjectsBoosterVaccinatedAtDay(LocalDate date) {
        return subjectDataService.findByBoosterVaccinationDate(date);
    }

    @Override
    public LocalDate findOldestPrimerVaccinationDate() {
        QueryParams queryParams = new QueryParams(new Order("primerVaccinationDate", Order.Direction.ASC));
        List<Subject> subjects = subjectDataService.retrieveAll(queryParams);
        if (subjects != null && !subjects.isEmpty()) {
            for (Subject subject : subjects) {
                if (subject.getPrimerVaccinationDate() != null) {
                    return subject.getPrimerVaccinationDate();
                }
            }
        }
        return LocalDate.now().minusDays(1);
    }

    @Override
    public void delete(Subject record) {
        subjectDataService.delete(record);
    }

    @Override
    public void deleteAll() {
        subjectDataService.deleteAll();
    }

    private void sendSiteIdChangedEvent(String subjectId, String siteId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(EbodacConstants.SUBJECT_ID, subjectId);
        parameters.put(EbodacConstants.SITE_ID, siteId);
        MotechEvent motechEvent = new MotechEvent(EbodacConstants.SITE_ID_CHANGED_EVENT, parameters);
        eventRelay.sendEventMessage(motechEvent);
    }
}
