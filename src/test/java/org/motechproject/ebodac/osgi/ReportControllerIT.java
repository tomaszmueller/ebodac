package org.motechproject.ebodac.osgi;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Gender;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.ReportBoosterVaccination;
import org.motechproject.ebodac.domain.ReportPrimerVaccination;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.repository.ReportBoosterVaccinationDataService;
import org.motechproject.ebodac.repository.ReportPrimerVaccinationDataService;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.utils.TestContext;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ReportControllerIT extends BasePaxIT {

    @Inject
    private SubjectDataService subjectDataService;

    @Inject
    private VisitDataService visitDataService;

    @Inject
    private ReportPrimerVaccinationDataService primerVaccinationDataService;

    @Inject
    private ReportBoosterVaccinationDataService boosterVaccinationDataService;

    private Subject firstSubject;

    private Subject secondSubject;

    private DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

    @Before
    public void cleanBefore() throws IOException, InterruptedException {
        visitDataService.deleteAll();
        subjectDataService.deleteAll();
        boosterVaccinationDataService.deleteAll();
        primerVaccinationDataService.deleteAll();
        resetTestFields();
        login();
    }

    @After
    public void cleanAfter() {
        visitDataService.deleteAll();
        subjectDataService.deleteAll();
        boosterVaccinationDataService.deleteAll();
        primerVaccinationDataService.deleteAll();
    }

    private void resetTestFields() {
        firstSubject = new Subject("1000000161", "Michal", "Abacki", "Cabacki",
                "729402018364", "address", Language.English, "community", "B05-SL10001");

        secondSubject = new Subject("1000000162", "Rafal", "Dabacki", "Ebacki",
                "44443333222", "address1", Language.Susu, "community", "B05-SL10001");

        firstSubject.setDateOfBirth(LocalDate.parse("1967-09-17", formatter));
        firstSubject.setGender(Gender.Male);
        firstSubject.setPrimerVaccinationDate(LocalDate.parse("2014-10-17", formatter));
        firstSubject.setBoosterVaccinationDate(LocalDate.parse("2014-10-20", formatter));

        secondSubject.setDateOfBirth(LocalDate.parse("2005-08-04", formatter));
        secondSubject.setGender(Gender.Male);
        secondSubject.setPrimerVaccinationDate(LocalDate.parse("2014-10-17", formatter));
        secondSubject.setBoosterVaccinationDate(LocalDate.parse("2014-10-20", formatter));
    }

    @Test
    public void shouldGenerateReports() throws IOException, InterruptedException {
        try {
            fakeNow(newDateTime(2014, 10, 21, 1, 0, 0));
            assertEquals(0, subjectDataService.retrieveAll().size());
            assertEquals(0, visitDataService.retrieveAll().size());
            assertEquals(0, primerVaccinationDataService.retrieveAll().size());
            assertEquals(0, boosterVaccinationDataService.retrieveAll().size());

            subjectDataService.create(firstSubject);
            subjectDataService.create(secondSubject);
            assertEquals(2, subjectDataService.retrieveAll().size());

            HttpResponse response = getReports("2014-10-17");
            assertEquals(200, response.getStatusLine().getStatusCode());

            List<ReportPrimerVaccination> primerVaccinationReport =  primerVaccinationDataService.retrieveAll();
            List<ReportBoosterVaccination> boosterVaccinationReport = boosterVaccinationDataService.retrieveAll();

            assertEquals(4,primerVaccinationReport.size());
            assertEquals(4, boosterVaccinationReport.size());

            assertEquals(1,(long)primerVaccinationReport.get(0).getAdultMales());
            assertEquals(1,(long)primerVaccinationReport.get(0).getChildren_6_11());

            assertEquals(1, (long) boosterVaccinationReport.get(3).getAdultMales());
            assertEquals(1, (long) boosterVaccinationReport.get(3).getChildren_6_11());

        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldNotGenerateReportsWhenDateMalformedOrMissing() throws IOException, InterruptedException {
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());
        assertEquals(0, primerVaccinationDataService.retrieveAll().size());
        assertEquals(0, boosterVaccinationDataService.retrieveAll().size());

        HttpResponse response = getReports("10-17-2014");
        assertEquals(400, response.getStatusLine().getStatusCode());

        response = getReports("");
        assertEquals(400, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldNotGenerateReportsWithFutureDates() throws IOException, InterruptedException {
        try {
            fakeNow(newDateTime(2014, 10, 21, 1, 0, 0));
            assertEquals(0, subjectDataService.retrieveAll().size());
            assertEquals(0, visitDataService.retrieveAll().size());
            assertEquals(0, primerVaccinationDataService.retrieveAll().size());
            assertEquals(0, boosterVaccinationDataService.retrieveAll().size());

            subjectDataService.create(firstSubject);
            subjectDataService.create(secondSubject);
            assertEquals(2, subjectDataService.retrieveAll().size());

            HttpResponse response = getReports("2015-10-17");
            assertEquals(200, response.getStatusLine().getStatusCode());

            assertEquals(0, primerVaccinationDataService.retrieveAll().size());
            assertEquals(0, boosterVaccinationDataService.retrieveAll().size());

        } finally {
            stopFakingTime();
        }
    }

    private HttpResponse getReports(String startDate) throws IOException, InterruptedException {
        HttpPost post = new HttpPost(String.format("http://localhost:%d/ebodac/generateReports", TestContext.getJettyPort()));
        StringEntity dateEntity = new StringEntity(startDate);
        post.setEntity(dateEntity);
        post.setHeader(HttpHeaders.CONTENT_TYPE, "text/plain; charset=ISO-8859-1");

        HttpResponse response = getHttpClient().execute(post);
        assertNotNull(response);

        return response;
    }

}
