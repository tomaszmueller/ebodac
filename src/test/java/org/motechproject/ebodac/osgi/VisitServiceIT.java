package org.motechproject.ebodac.osgi;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.VisitService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class VisitServiceIT extends BasePaxIT {

    @Inject
    private VisitService visitService;

    @Inject
    private SubjectDataService subjectDataService;

    @Inject
    private VisitDataService visitDataService;

    private Subject firstSubject;

    private Subject secondSubject;

    private Visit firstVisit;

    private Visit secondVisit;

    private DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

    @Before
    public void cleanBefore() {
        visitDataService.deleteAll();
        subjectDataService.deleteAll();
        resetTestFields();
    }

    @After
    public void cleanAfter() {
        visitDataService.deleteAll();
        subjectDataService.deleteAll();
    }

    private void resetTestFields() {
        firstSubject = new Subject("1000000161", "Michal", "Abacki", "Cabacki",
                "729402018364", "address", Language.English, "community", "B05-SL10001");

        secondSubject = new Subject("1000000162", "Rafal", "Dabacki", "Ebacki",
                "44443333222", "address", Language.Susu, "community", "B05-SL10001");

        firstVisit = createVisit(firstSubject, VisitType.SCREENING, DateTime.parse("2014-10-17", formatter),
                DateTime.parse("2014-10-18", formatter), "owner");

        secondVisit = createVisit(firstSubject, VisitType.PRIME_VACCINATION_FOLLOW_UP_VISIT, DateTime.parse("2014-10-19", formatter),
                DateTime.parse("2014-10-20", formatter), "owner");
    }

    @Test
    public void shouldCreateOrUpdate() {
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        visitService.create(firstVisit);
        assertEquals(1, visitDataService.retrieveAll().size());

        firstVisit.setDateProjected(DateTime.parse("2014-10-20", formatter));
        firstVisit.setType(VisitType.PRIME_VACCINATION_DAY);

        visitService.createOrUpdate(firstVisit); //should update
        List<Visit> visits = visitDataService.retrieveAll();
        assertEquals(1, visits.size());

        checkVisitFields(firstVisit, visits.get(0));

        visitService.createOrUpdate(secondVisit); // should create
        visits = visitDataService.retrieveAll();
        assertEquals(2, visits.size());

        visits = visitDataService.findVisitByDate(DateTime.parse("2014-10-19", formatter));
        assertEquals(1, visits.size());

        checkVisitFields(secondVisit, visits.get(0));

        secondVisit.setDate(DateTime.parse("2014-10-21", formatter));
        secondVisit.setSubject(secondSubject);

        visitService.update(secondVisit);
        visits = visitDataService.retrieveAll();
        assertEquals(2, visits.size());

        visits = visitDataService.findVisitByDate(DateTime.parse("2014-10-21", formatter));
        assertEquals(1, visits.size());

        checkVisitFields(secondVisit, visits.get(0));
    }

    @Test
    public void shouldDelete() {
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        visitService.createOrUpdate(firstVisit);
        visitService.createOrUpdate(secondVisit);

        assertEquals(2, visitDataService.retrieveAll().size());

        visitService.delete(firstVisit);
        List<Visit> visits = visitDataService.retrieveAll();
        assertEquals(1, visits.size());
    }

    private Visit createVisit(Subject subject, VisitType type, DateTime date,
                              DateTime projectedDate, String owner) {
        Visit ret = new Visit();
        ret.setSubject(subject);
        ret.setType(type);
        ret.setDate(date);
        ret.setDateProjected(projectedDate);
        ret.setOwner(owner);

        return ret;
    }

    private void checkVisitFields(Visit expected, Visit actual) {
        assertEquals(expected.getSubject().getSubjectId(), actual.getSubject().getSubjectId());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getDateProjected(), actual.getDateProjected());
        assertEquals(expected.getOwner(),           actual.getOwner());
    }
}
