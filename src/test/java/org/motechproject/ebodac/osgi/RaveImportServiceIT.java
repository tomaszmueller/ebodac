package org.motechproject.ebodac.osgi;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Gender;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.RaveImportService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class RaveImportServiceIT extends BasePaxIT {

    @Inject
    private RaveImportService raveImportService;

    @Inject
    private SubjectDataService subjectDataService;

    @Inject
    private VisitDataService visitDataService;

    @Before
    public void cleanBefore() {
        visitDataService.deleteAll();
        subjectDataService.deleteAll();
    }

    @After
    public void cleanAfter() {
        visitDataService.deleteAll();
        subjectDataService.deleteAll();
    }

    @Test
    public void shouldImportCsv() throws Exception {
        List<Subject> subjects = subjectDataService.retrieveAll();
        assertEquals(0, subjects.size());
        List<Visit> visits = visitDataService.retrieveAll();
        assertEquals(0, visits.size());

        InputStream in = getClass().getResourceAsStream("/sample.csv");
        assertNotNull(in);

        raveImportService.importCsv(new InputStreamReader(in));
        in.close();

        subjects = subjectDataService.retrieveAll();
        assertEquals(8, subjects.size());
        visits = visitDataService.retrieveAll();
        assertEquals(27, visits.size());
    }

    @Test
    public void shouldImportFromCsvWithBrokenData() throws Exception {
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        InputStream in = getClass().getResourceAsStream("/rave.csv");
        assertNotNull(in);
        raveImportService.importCsv(new InputStreamReader(in));
        in.close();

        checkSubjectData();

        checkVisitData();
    }

    @Test
    public void checkIfZetesFieldsAreNotOverride() throws Exception {
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        Subject beforeUpdate = new Subject("1000000161", "Michal", "Abacki", "Cabacki",
                "729402018364", "address", Language.English, "community", "B05-SL10001");

        subjectDataService.create(beforeUpdate);

        assertEquals(1, subjectDataService.retrieveAll().size());

        InputStream in = getClass().getResourceAsStream("/rave.csv");
        assertNotNull(in);
        raveImportService.importCsv(new InputStreamReader(in));
        in.close();

        List<Subject> subjects = subjectDataService.retrieveAll();
        assertEquals(2, subjects.size());

        Subject subject = subjectDataService.findSubjectBySubjectId("1000000161");

        assertEquals(beforeUpdate.getSubjectId(),       subject.getSubjectId());
        assertEquals(beforeUpdate.getName(),            subject.getName());
        assertEquals(beforeUpdate.getHouseholdName(),   subject.getHouseholdName());
        assertEquals(beforeUpdate.getHeadOfHousehold(), subject.getHeadOfHousehold());
        assertEquals(beforeUpdate.getAddress(),         subject.getAddress());
        assertEquals(beforeUpdate.getLanguageCode(),    subject.getLanguageCode());
        assertEquals(beforeUpdate.getCommunity(),       subject.getCommunity());
        assertEquals(beforeUpdate.getSiteId(),          subject.getSiteId());
    }

    private void checkSubjectData() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);
        List<Subject> subjects = subjectDataService.retrieveAll();
        assertEquals(2, subjects.size());

        Subject subject = subjectDataService.findSubjectBySubjectId("1000000161");

        assertEquals("B05-SL10001", subject.getSiteId());
        assertEquals("1000000161",  subject.getSubjectId());
        assertEquals(Gender.Male, subject.getGender());
        assertEquals(1, (long) subject.getStageId());
        assertEquals(DateTime.parse("1968-12-29", formatter), subject.getDateOfBirth());
        assertEquals(DateTime.parse("2015-06-27", formatter), subject.getPrimerVaccinationDate());
        assertEquals(DateTime.parse("2015-08-22", formatter), subject.getBoosterVaccinationDate());
        assertEquals(DateTime.parse("2015-07-05", formatter), subject.getDateOfDisconVac()); // firstly 2015-07-06 then changed to 2015-07-05
        assertEquals(DateTime.parse("2015-06-21", formatter), subject.getDateOfDisconStd());

        subject = subjectDataService.findSubjectBySubjectId("1000000646");

        assertEquals("B05-SL10001", subject.getSiteId());
        assertEquals("1000000646", subject.getSubjectId());
        assertEquals(Gender.Female, subject.getGender());
        assertEquals(2, (long) subjects.get(1).getStageId());
        assertEquals(DateTime.parse("2009-06-23", formatter), subject.getDateOfBirth());
        assertEquals(DateTime.parse("2015-06-28", formatter), subject.getPrimerVaccinationDate());
        assertEquals(DateTime.parse("2015-08-22", formatter), subject.getBoosterVaccinationDate());
        assertEquals(DateTime.parse("2015-07-05", formatter), subject.getDateOfDisconVac());
        assertEquals(DateTime.parse("2015-06-21", formatter), subject.getDateOfDisconStd());

    }

    private void checkVisitData() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);
        List<Visit> visits = visitDataService.retrieveAll();
        List<Subject> subjects = subjectDataService.retrieveAll();
        assertEquals(2, subjects.size());
        assertEquals(11, visits.size());

        for(Visit visit : visits) {
            /* in row with this visit is bad date formatting so it shouldn't be in collection */
            assertFalse(visit.getType().equals(VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT)); // this visit shouldn't be in collection
        }

        visits = subjects.get(1).getVisits();
        assertEquals(3, visits.size());

        checkVisit(visits.get(0), subjects.get(1), VisitType.SCREENING,
                DateTime.parse("2015-06-14", formatter), null);

        checkVisit(visits.get(1), subjects.get(1), VisitType.PRIME_VACCINATION_DAY,
                null, DateTime.parse("2015-07-12", formatter));

        checkVisit(visits.get(2), subjects.get(1), VisitType.PRIME_VACCINATION_FOLLOW_UP_VISIT,
                null, null);
    }

    private void checkVisit(Visit visit, Subject subject, VisitType type, DateTime date, DateTime dateProjected) {
        assertSame(subject, visit.getSubject());
        assertEquals(type, visit.getType());
        assertEquals(date, visit.getDate());
        assertEquals(dateProjected, visit.getDateProjected());
    }
}
