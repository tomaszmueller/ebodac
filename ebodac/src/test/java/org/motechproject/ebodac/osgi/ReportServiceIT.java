package org.motechproject.ebodac.osgi;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.domain.IvrAndSmsStatisticReport;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.ReportBoosterVaccination;
import org.motechproject.ebodac.domain.ReportPrimerVaccination;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.repository.IvrAndSmsStatisticReportDataService;
import org.motechproject.ebodac.repository.ReportBoosterVaccinationDataService;
import org.motechproject.ebodac.repository.ReportPrimerVaccinationDataService;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.RaveImportService;
import org.motechproject.ebodac.service.ReportService;
import org.motechproject.ebodac.service.ReportUpdateService;
import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.domain.CallDirection;
import org.motechproject.ivr.repository.CallDetailRecordDataService;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ReportServiceIT extends BasePaxIT {

    @Inject
    private IvrAndSmsStatisticReportDataService ivrAndSmsStatisticReportDataService;

    @Inject
    private CallDetailRecordDataService callDetailRecordDataService;

    @Inject
    private SubjectDataService subjectDataService;

    @Inject
    private SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    @Inject
    private VisitDataService visitDataService;

    @Inject
    private ConfigService configService;

    @Inject
    private ReportPrimerVaccinationDataService primerVaccinationDataService;

    @Inject
    private ReportBoosterVaccinationDataService boosterVaccinationDataService;

    @Inject
    private RaveImportService raveImportService;

    @Inject
    private ReportUpdateService reportUpdateService;

    @Inject
    private ReportService reportService;

    private Config savedConfig;

    private Config config;

    @Before
    public void setUp() throws Exception {
        savedConfig = configService.getConfig();
        callDetailRecordDataService.deleteAll();
        ivrAndSmsStatisticReportDataService.deleteAll();
        visitDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        subjectDataService.deleteAll();
        boosterVaccinationDataService.deleteAll();
        primerVaccinationDataService.deleteAll();
        reportUpdateService.setConfigService(configService);
    }

    @After
    public void tearDown() throws Exception {
        configService.updateConfig(savedConfig);
        callDetailRecordDataService.deleteAll();
        ivrAndSmsStatisticReportDataService.deleteAll();
        visitDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        subjectDataService.deleteAll();
        boosterVaccinationDataService.deleteAll();
        primerVaccinationDataService.deleteAll();
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
            LocalDate startDate = LocalDate.parse("2015-06-27", formatter);

            InputStream in = getClass().getResourceAsStream("/report.csv");
            assertNotNull(in);
            raveImportService.importCsv(new InputStreamReader(in), "/report.csv");
            in.close();

            assertEquals(98, subjectDataService.retrieveAll().size());
            assertEquals(98, visitDataService.retrieveAll().size());

            reportService.generateDailyReportsFromDate(startDate);

            LocalDate now = LocalDate.parse("2015-06-29", formatter);
            for (LocalDate date = startDate; date.isBefore(now); date = date.plusDays(1)) {
                checkUpdateBoosterVaccinationReportsForDates(date);
                checkUpdatePrimerVaccinationReportsForDates(date);
            }
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldGenerateNewReports() throws IOException {
        try {
            fakeNow(newDateTime(2015, 7, 6, 1, 0, 0));
            DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);
            assertEquals(0, primerVaccinationDataService.retrieveAll().size());
            assertEquals(0, boosterVaccinationDataService.retrieveAll().size());

            config = configService.getConfig();
            config.setLastCalculationDate(DateUtil.now().minusDays(5).toString(formatter));
            config.setGenerateReports(true);
            configService.updateConfig(config);

            reportService.generateDailyReports();
            assertEquals(5, primerVaccinationDataService.retrieveAll().size());
            assertEquals(5, boosterVaccinationDataService.retrieveAll().size());
            for (int i = 2; i < 6; i++) {
                assertNotNull(primerVaccinationDataService.findByDate(new LocalDate(2015, 7, i)));
                assertNotNull(boosterVaccinationDataService.findByDate(new LocalDate(2015, 7, i)));
            }
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldUpdateDailyReports() throws IOException {
        try {
            fakeNow(newDateTime(2015, 7, 6, 12, 0, 0));
            DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

            config = configService.getConfig();
            config.setLastCalculationDate(DateUtil.now().minusDays(5).toString(formatter));
            config.setGenerateReports(true);
            config.setPrimerVaccinationReportsToUpdate(null);
            config.setBoosterVaccinationReportsToUpdate(null);
            configService.updateConfig(config);

            assertEquals(0, config.getPrimerVaccinationReportsToUpdate().size());
            assertEquals(0, config.getBoosterVaccinationReportsToUpdate().size());

            InputStream in = getClass().getResourceAsStream("/sample2.csv");
            raveImportService.importCsv(new InputStreamReader(in), "/sample2.csv");
            in.close();

            config = configService.getConfig();
            assertEquals(2, config.getPrimerVaccinationReportsToUpdate().size());
            assertEquals(3, config.getBoosterVaccinationReportsToUpdate().size());

            reportService.generateDailyReports();
            config = configService.getConfig();
            assertEquals(0, config.getPrimerVaccinationReportsToUpdate().size());
            assertEquals(0, config.getBoosterVaccinationReportsToUpdate().size());

            for (int i = 2; i < 6; i++) {
                assertNotNull(primerVaccinationDataService.findByDate(new LocalDate(2015, 7, i)));
                assertNotNull(boosterVaccinationDataService.findByDate(new LocalDate(2015, 7, i)));
            }
            assertNotNull(primerVaccinationDataService.findByDate(new LocalDate(2015, 6, 21)));
            assertNotNull(primerVaccinationDataService.findByDate(new LocalDate(2015, 6, 29)));
            assertNotNull(boosterVaccinationDataService.findByDate(new LocalDate(2015, 6, 19)));
            assertNotNull(boosterVaccinationDataService.findByDate(new LocalDate(2015, 6, 21)));
            assertNotNull(boosterVaccinationDataService.findByDate(new LocalDate(2015, 6, 27)));
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldStartGenerateFromOldestVaccinationDay() throws IOException {
        try {
            fakeNow(newDateTime(2015, 6, 28, 12, 0, 0));

            config = new Config();
            config.setGenerateReports(true);
            configService.updateConfig(config);

            assertNull(config.getLastCalculationDate());
            assertNull(config.getFirstCalculationStartDate());

            InputStream in = getClass().getResourceAsStream("/sample2.csv");
            raveImportService.importCsv(new InputStreamReader(in), "/sample2.csv");
            in.close();

            reportService.generateDailyReports();
            assertEquals(7, primerVaccinationDataService.retrieveAll().size());
            assertEquals(7, boosterVaccinationDataService.retrieveAll().size());

            for (int i = 21; i < 28; i++) {
                assertNotNull(primerVaccinationDataService.findByDate(new LocalDate(2015, 6, i)));
                assertNotNull(boosterVaccinationDataService.findByDate(new LocalDate(2015, 6, i)));
            }
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldUseReportStartDateIfLastReportDateIsNull() throws IOException {
        try {
            fakeNow(newDateTime(2015, 6, 28, 12, 0, 0));

            config = new Config();
            config.setFirstCalculationStartDate("2015-06-25");
            config.setGenerateReports(true);
            configService.updateConfig(config);

            assertNull(config.getLastCalculationDate());

            InputStream in = getClass().getResourceAsStream("/sample2.csv");
            raveImportService.importCsv(new InputStreamReader(in), "/sample2.csv");
            in.close();

            reportService.generateDailyReports();
            assertEquals(3, primerVaccinationDataService.retrieveAll().size());
            assertEquals(3, boosterVaccinationDataService.retrieveAll().size());

            for (int i = 25; i < 28; i++) {
                assertNotNull(primerVaccinationDataService.findByDate(new LocalDate(2015, 6, i)));
                assertNotNull(boosterVaccinationDataService.findByDate(new LocalDate(2015, 6, i)));
            }
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldGenerateIvrAndSmsStatisticReportsForAllRecordsIfLastCalculationDateIsEmpty() {
        config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        configService.updateConfig(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "", "", "", "");
        subjectDataService.create(subject);

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "1");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "22");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "102", "02", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "1");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-09-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "02", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "1");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        reportService.generateIvrAndSmsStatisticReports();

        assertEquals(3, ivrAndSmsStatisticReportDataService.count());
    }

    @Test
    public void shouldGenerateIvrAndSmsStatisticReportsFromLastCalculationDate() {
        config = new Config();
        config.setLastCalculationDateForIvrReports("2015-10-01");
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        configService.updateConfig(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "", "", "", "");
        subjectDataService.create(subject);

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "1");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "22");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "102", "02", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "1");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-09-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "02", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "1");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        reportService.generateIvrAndSmsStatisticReports();

        assertEquals(2, ivrAndSmsStatisticReportDataService.count());
    }

    @Test
    public void shouldAddReportToUpdateListIfSmsHasNoFinishedStatus() {
        config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        configService.updateConfig(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "", "", "", "");
        subjectDataService.create(subject);

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "22");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "102", "02", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-09-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "02", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "02", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "1");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        reportService.generateIvrAndSmsStatisticReports();

        assertEquals(3, ivrAndSmsStatisticReportDataService.count());

        config = configService.getConfig();

        assertEquals(2, config.getIvrAndSmsStatisticReportsToUpdate().size());
        assertTrue(config.getIvrAndSmsStatisticReportsToUpdate().contains("101"));
        assertTrue(config.getIvrAndSmsStatisticReportsToUpdate().contains("102"));
    }

    @Test
    public void shouldUpdateReports() {
        config = new Config();
        config.setLastCalculationDateForIvrReports("2015-10-20");
        config.setIvrAndSmsStatisticReportsToUpdate(new HashSet<>(Arrays.asList("101", "102")));
        configService.updateConfig(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "", "", "", "");
        subjectDataService.create(subject);

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_END_TIMESTAMP, "2015-10-10 8:06:00");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:06:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "22");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "102", "02", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-09-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "02", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "02", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "1");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        reportService.generateIvrAndSmsStatisticReports();

        assertEquals(3, ivrAndSmsStatisticReportDataService.count());

        config = configService.getConfig();

        assertEquals(1, config.getIvrAndSmsStatisticReportsToUpdate().size());
        assertTrue(config.getIvrAndSmsStatisticReportsToUpdate().contains("102"));
    }

    @Test
    public void shouldNotAddSmsReceivedDateIfSmsSentButHasNoFinishedRecord() {
        config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        configService.updateConfig(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "", "", "", "");
        subjectDataService.create(subject);

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        reportService.generateIvrAndSmsStatisticReports();

        assertEquals(1, ivrAndSmsStatisticReportDataService.count());
        IvrAndSmsStatisticReport ivrAndSmsStatisticReport = ivrAndSmsStatisticReportDataService.retrieveAll().get(0);

        assertTrue(ivrAndSmsStatisticReport.getSms());
        assertNull(ivrAndSmsStatisticReport.getSmsReceivedDate());
    }

    @Test
    public void shouldSetAllDataAndGetReceivedDateFromFinishedRecordForCallAndSms() {
        config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        configService.updateConfig(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "", "", "", "");
        subjectDataService.create(subject);

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_END_TIMESTAMP, "2015-10-10 8:06:00");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:07:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "2");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        reportService.generateIvrAndSmsStatisticReports();

        List<IvrAndSmsStatisticReport> ivrAndSmsStatisticReports = ivrAndSmsStatisticReportDataService.retrieveAll();
        assertEquals(2, ivrAndSmsStatisticReports.size());

        IvrAndSmsStatisticReport smsStatisticReport;
        IvrAndSmsStatisticReport ivrStatisticReport;

        if ("01".equals(ivrAndSmsStatisticReports.get(0).getProviderCallId())) {
            smsStatisticReport = ivrAndSmsStatisticReports.get(0);
            ivrStatisticReport = ivrAndSmsStatisticReports.get(1);
        } else {
            smsStatisticReport = ivrAndSmsStatisticReports.get(1);
            ivrStatisticReport = ivrAndSmsStatisticReports.get(0);
        }

        assertEquals("21", smsStatisticReport.getMessageId());
        assertEquals(new DateTime(2015, 10, 10, 8, 0, 0), smsStatisticReport.getSendDate());
        assertEquals(3, smsStatisticReport.getNumberOfAttempts());
        assertEquals(0, smsStatisticReport.getExpectedDuration(), 0.001);
        assertEquals(0, smsStatisticReport.getMessagePercentListened(), 0.001);
        assertEquals(0, smsStatisticReport.getTimeListenedTo(), 0.001);
        assertNull(smsStatisticReport.getReceivedDate());
        assertTrue(smsStatisticReport.getSms());
        assertEquals(new DateTime(2015, 10, 10, 8, 6, 0), smsStatisticReport.getSmsReceivedDate());
        assertEquals(subject, smsStatisticReport.getSubject());

        assertEquals("23", ivrStatisticReport.getMessageId());
        assertEquals(new DateTime(2015, 10, 21, 8, 0, 0), ivrStatisticReport.getSendDate());
        assertEquals(2, ivrStatisticReport.getNumberOfAttempts());
        assertEquals(20, ivrStatisticReport.getExpectedDuration(), 0.001);
        assertEquals(100, ivrStatisticReport.getMessagePercentListened(), 0.001);
        assertEquals(22, ivrStatisticReport.getTimeListenedTo(), 0.001);
        assertEquals(new DateTime(2015, 10, 21, 8, 4, 0), ivrStatisticReport.getReceivedDate());
        assertFalse(ivrStatisticReport.getSms());
        assertNull(ivrStatisticReport.getSmsReceivedDate());
        assertEquals(subject, ivrStatisticReport.getSubject());
    }

    @Test
    public void shouldCreateReportsForAllSubjectsInTheList() {
        config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        configService.updateConfig(config);

        Subject subject1 = new Subject("1", "", "", "", "123456789","", Language.English, "com", "", "", "", "");
        subjectDataService.create(subject1);
        Subject subject2 = new Subject("2", "", "", "", "111111111","", Language.English, "com", "", "", "", "");
        subjectDataService.create(subject2);
        Subject subject3 = new Subject("3", "", "", "", "222222222","", Language.English, "com", "", "", "", "");
        subjectDataService.create(subject3);

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1,2,3");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "2");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        reportService.generateIvrAndSmsStatisticReports();

        List<IvrAndSmsStatisticReport> ivrAndSmsStatisticReports = ivrAndSmsStatisticReportDataService.retrieveAll();
        assertEquals(3, ivrAndSmsStatisticReports.size());

        assertEquals(subject1, ivrAndSmsStatisticReports.get(0).getSubject());
        assertEquals(subject2, ivrAndSmsStatisticReports.get(1).getSubject());
        assertEquals(subject3, ivrAndSmsStatisticReports.get(2).getSubject());
    }

    @Test
    public void shouldNotCreateReportsWhenProviderCallIdOrProviderExtraDataAreEmpty() {
        config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        configService.updateConfig(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "", "", "", "");
        subjectDataService.create(subject);

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_END_TIMESTAMP, "2015-10-10 8:06:00");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:07:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "2");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        reportService.generateIvrAndSmsStatisticReports();

        assertEquals(0, ivrAndSmsStatisticReportDataService.count());
    }

    @Test
    public void shouldNotCreateReportsWhenSubjectIdsIsEmptyOrNoSubjectsFound() {
        config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        configService.updateConfig(config);

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_END_TIMESTAMP, "2015-10-10 8:06:00");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:07:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "2");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        reportService.generateIvrAndSmsStatisticReports();

        assertEquals(0, ivrAndSmsStatisticReportDataService.count());
    }

    @Test
    public void shouldNotCreateReportsWhenNoProviderTimestampFoundForFinishedRecord() {
        config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        configService.updateConfig(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "", "", "", "");
        subjectDataService.create(subject);

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:07:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "2");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        reportService.generateIvrAndSmsStatisticReports();

        assertEquals(0, ivrAndSmsStatisticReportDataService.count());
    }

    @Test
    public void shouldNotCreateReportsWhenTooMuchRecordsWithFinishedStatus() {
        config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        configService.updateConfig(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "", "", "", "");
        subjectDataService.create(subject);

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_END_TIMESTAMP, "2015-10-10 8:06:00");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:07:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_END_TIMESTAMP, "2015-10-10 8:06:00");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:07:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "2");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "2");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        reportService.generateIvrAndSmsStatisticReports();

        assertEquals(0, ivrAndSmsStatisticReportDataService.count());
    }

    @Test
    public void shouldNotCreateReportsWhenSmsSentAndThereAreNoFailedCallRecord() {
        config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        configService.updateConfig(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "", "", "", "");
        subjectDataService.create(subject);

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_END_TIMESTAMP, "2015-10-10 8:06:00");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:07:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        reportService.generateIvrAndSmsStatisticReports();

        assertEquals(0, ivrAndSmsStatisticReportDataService.count());
    }

    @Test
    public void shouldNotCreateReportsWhenNoSmsSentAndThereAreNoFinishedCallRecord() {
        config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        configService.updateConfig(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "", "", "", "");
        subjectDataService.create(subject);

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        callDetailRecordDataService.create(callDetailRecord);

        reportService.generateIvrAndSmsStatisticReports();

        assertEquals(0, ivrAndSmsStatisticReportDataService.count());
    }

    private void checkUpdateBoosterVaccinationReportsForDates(LocalDate date) {
        ReportBoosterVaccination existingBoosterReport = boosterVaccinationDataService.findByDate(date);

        assertEquals(7, (int) existingBoosterReport.getChildrenFrom1To5());
        assertEquals(7, (int) existingBoosterReport.getChildrenFrom6To11());
        assertEquals(7, (int) existingBoosterReport.getChildrenFrom12To17());
        assertEquals(7, (int) existingBoosterReport.getAdultFemales());
        assertEquals(7, (int) existingBoosterReport.getAdultMales());
        assertEquals(7, (int) existingBoosterReport.getAdultUndifferentiated());
        assertEquals(7, (int) existingBoosterReport.getAdultUnidentified());

        assertEquals(35, (int) existingBoosterReport.getPeopleBoostered());
    }

    private void checkUpdatePrimerVaccinationReportsForDates(LocalDate date) {
        ReportPrimerVaccination existingPrimerReport = primerVaccinationDataService.findByDate(date);

        assertEquals(7, (int) existingPrimerReport.getChildrenFrom1To5());
        assertEquals(7, (int) existingPrimerReport.getChildrenFrom6To11());
        assertEquals(7, (int) existingPrimerReport.getChildrenFrom12To17());
        assertEquals(7, (int) existingPrimerReport.getAdultFemales());
        assertEquals(7, (int) existingPrimerReport.getAdultMales());
        assertEquals(7, (int) existingPrimerReport.getAdultUndifferentiated());
        assertEquals(7, (int) existingPrimerReport.getAdultUnidentified());

        assertEquals(35, (int) existingPrimerReport.getPeopleVaccinated());
    }
}

