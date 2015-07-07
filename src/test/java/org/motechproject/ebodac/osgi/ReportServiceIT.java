package org.motechproject.ebodac.osgi;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.ReportBoosterVaccination;
import org.motechproject.ebodac.domain.ReportPrimerVaccination;
import org.motechproject.ebodac.repository.ReportBoosterVaccinationDataService;
import org.motechproject.ebodac.repository.ReportPrimerVaccinationDataService;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.*;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ReportServiceIT extends BasePaxIT {

    @Inject
    private ReportService reportService;

    @Inject
    private ReportPrimerVaccinationDataService primerVaccinationDataService;

    @Inject
    private ReportBoosterVaccinationDataService boosterVaccinationDataService;

    @Inject
    private RaveImportService raveImportService;

    @Inject
    private SubjectDataService subjectDataService;

    @Inject
    private VisitDataService visitDataService;

    @Before
    public void cleanBefore() {
        primerVaccinationDataService.deleteAll();
        boosterVaccinationDataService.deleteAll();
        visitDataService.deleteAll();
        subjectDataService.deleteAll();
    }

    @After
    public void cleanAfter() {
        primerVaccinationDataService.deleteAll();
        boosterVaccinationDataService.deleteAll();
        visitDataService.deleteAll();
        subjectDataService.deleteAll();
    }

    @Test
    public void shouldGenerateDailyReports() throws IOException {
        try {
            fakeNow(newDateTime(2015, 6, 29, 10, 0, 0));

            assertEquals(0, primerVaccinationDataService.retrieveAll().size());
            assertEquals(0, boosterVaccinationDataService.retrieveAll().size());

            assertEquals(0, subjectDataService.retrieveAll().size());
            assertEquals(0, visitDataService.retrieveAll().size());

            DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);
            DateTime startDate = DateTime.parse("2015-06-27", formatter);

            InputStream in = getClass().getResourceAsStream("/report.csv");
            assertNotNull(in);
            raveImportService.importCsv(new InputStreamReader(in));
            in.close();

            assertEquals(98, subjectDataService.retrieveAll().size());
            assertEquals(98, visitDataService.retrieveAll().size());

            reportService.generateDailyReportsFromDate(startDate);

            DateTime now = formatter.parseDateTime("2015-06-29");
            for (DateTime date = startDate; date.isBefore(now); date = date.plusDays(1)) {
                checkUpdateBoosterVaccinationReportsForDates(date);
                checkUpdatePrimerVaccinationReportsForDates(date);
            }
        }finally {
            stopFakingTime();
        }
    }

    private void checkUpdateBoosterVaccinationReportsForDates(DateTime date) {
        ReportBoosterVaccination existingBoosterReport = boosterVaccinationDataService.findReportByDate(date);

        assertEquals(7, (int) existingBoosterReport.getChildren_0_5());
        assertEquals(7, (int) existingBoosterReport.getChildren_6_11());
        assertEquals(7, (int) existingBoosterReport.getChildren_12_17());
        assertEquals(7, (int) existingBoosterReport.getAdultFemales());
        assertEquals(7, (int) existingBoosterReport.getAdultMales());
        assertEquals(7, (int) existingBoosterReport.getAdultUndifferentiated());
        assertEquals(7, (int) existingBoosterReport.getAdultUnidentified());

        assertEquals(35, (int) existingBoosterReport.getPeopleBoostered());
    }

    private void checkUpdatePrimerVaccinationReportsForDates(DateTime date) {
        ReportPrimerVaccination existingPrimerReport = primerVaccinationDataService.findReportByDate(date);

        assertEquals(7, (int) existingPrimerReport.getChildren_0_5());
        assertEquals(7, (int) existingPrimerReport.getChildren_6_11());
        assertEquals(7, (int) existingPrimerReport.getChildren_12_17());
        assertEquals(7, (int) existingPrimerReport.getAdultFemales());
        assertEquals(7, (int) existingPrimerReport.getAdultMales());
        assertEquals(7, (int) existingPrimerReport.getAdultUndifferentiated());
        assertEquals(7, (int) existingPrimerReport.getAdultUnidentified());

        assertEquals(35, (int) existingPrimerReport.getPeopleVaccinated());
    }

}