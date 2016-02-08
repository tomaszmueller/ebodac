package org.motechproject.ebodac.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.domain.Gender;
import org.motechproject.ebodac.domain.IvrAndSmsStatisticReport;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.ReportBoosterVaccination;
import org.motechproject.ebodac.domain.ReportPrimerVaccination;
import org.motechproject.ebodac.domain.SmsStatus;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.repository.IvrAndSmsStatisticReportDataService;
import org.motechproject.ebodac.repository.ReportBoosterVaccinationDataService;
import org.motechproject.ebodac.repository.ReportPrimerVaccinationDataService;
import org.motechproject.ebodac.service.impl.ReportServiceImpl;
import org.motechproject.ebodac.service.impl.ReportUpdateServiceImpl;
import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.domain.CallDirection;
import org.motechproject.ivr.repository.CallDetailRecordDataService;
import org.motechproject.mds.query.QueryParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;

public class ReportServiceTest {

    @InjectMocks
    private ReportService reportService = new ReportServiceImpl();

    @Mock
    private ConfigService configService;

    @Mock
    private SubjectService subjectService;

    @Mock
    private CallDetailRecordDataService callDetailRecordDataService;

    @Mock
    private IvrAndSmsStatisticReportDataService ivrAndSmsStatisticReportDataService;

    @Mock
    private ReportPrimerVaccinationDataService primerVaccinationDataService;

    @Mock
    private ReportBoosterVaccinationDataService boosterVaccinationDataService;

    @Mock
    private ReportUpdateServiceImpl reportUpdateService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldGenerateDailyReports() {
        try {
            fakeNow(newDateTime(2015, 6, 29, 10, 0, 0));

            DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);
            LocalDate startDate = LocalDate.parse("2015-06-28", formatter);

            List<Subject> subjectList = new ArrayList<>();

            Subject subject = new Subject();
            subject.setDateOfBirth(new LocalDate(2010, 12, 29));
            subject.setGender(Gender.Male);
            subjectList.add(subject);

            subject = new Subject();
            subject.setDateOfBirth(new LocalDate(2007, 12, 29));
            subject.setGender(Gender.Male);
            subjectList.add(subject);

            subject = new Subject();
            subject.setDateOfBirth(new LocalDate(2001, 12, 29));
            subject.setGender(Gender.Male);
            subjectList.add(subject);

            subject = new Subject();
            subject.setDateOfBirth(new LocalDate(1968, 12, 29));
            subject.setGender(Gender.Male);
            subjectList.add(subject);

            subject = new Subject();
            subject.setDateOfBirth(new LocalDate(1968, 12, 29));
            subject.setGender(Gender.Female);
            subjectList.add(subject);

            subject = new Subject();
            subject.setDateOfBirth(new LocalDate(1968, 12, 29));
            subject.setGender(Gender.Undifferentiated);
            subjectList.add(subject);

            subject = new Subject();
            subject.setDateOfBirth(new LocalDate(1968, 12, 29));
            subject.setGender(Gender.Unknown);
            subjectList.add(subject);

            subject = new Subject();
            subject.setDateOfBirth(new LocalDate(1968, 12, 29));
            subjectList.add(subject);


            Mockito.when(subjectService.findSubjectsPrimerVaccinatedAtDay(startDate)).thenReturn(subjectList);
            Mockito.when(subjectService.findSubjectsBoosterVaccinatedAtDay(startDate)).thenReturn(subjectList);

            reportService.generateDailyReportsFromDate(startDate);

            ArgumentCaptor<ReportPrimerVaccination> reportPrimerCaptor = ArgumentCaptor.forClass(ReportPrimerVaccination.class);
            Mockito.verify(primerVaccinationDataService, Mockito.times(1)).create(reportPrimerCaptor.capture());
            ReportPrimerVaccination reportPrimer = reportPrimerCaptor.getValue();

            ArgumentCaptor<ReportBoosterVaccination> reportBoosterCaptor = ArgumentCaptor.forClass(ReportBoosterVaccination.class);
            Mockito.verify(boosterVaccinationDataService, Mockito.times(1)).create(reportBoosterCaptor.capture());
            ReportBoosterVaccination reportBooster = reportBoosterCaptor.getValue();

            assertEquals(1, (int) reportPrimer.getChildrenFrom1To5());
            assertEquals(1, (int) reportPrimer.getChildrenFrom6To11());
            assertEquals(1, (int) reportPrimer.getChildrenFrom12To17());
            assertEquals(1, (int) reportPrimer.getAdultFemales());
            assertEquals(1, (int) reportPrimer.getAdultMales());
            assertEquals(1, (int) reportPrimer.getAdultUndifferentiated());
            assertEquals(2, (int) reportPrimer.getAdultUnidentified());
            assertEquals(8, (int) reportPrimer.getPeopleVaccinated());

            assertEquals(1, (int) reportBooster.getChildrenFrom1To5());
            assertEquals(1, (int) reportBooster.getChildrenFrom6To11());
            assertEquals(1, (int) reportBooster.getChildrenFrom12To17());
            assertEquals(1, (int) reportBooster.getAdultFemales());
            assertEquals(1, (int) reportBooster.getAdultMales());
            assertEquals(1, (int) reportBooster.getAdultUndifferentiated());
            assertEquals(2, (int) reportBooster.getAdultUnidentified());
            assertEquals(8, (int) reportBooster.getPeopleBoostered());
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldGenerateNewReports() {
        try {
            fakeNow(newDateTime(2015, 7, 6, 1, 0, 0));
            DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

            Config config = new Config();
            config.setLastCalculationDate(DateUtil.now().minusDays(5).toString(formatter));
            config.setGenerateReports(true);
            Mockito.when(configService.getConfig()).thenReturn(config);

            reportService.generateDailyReports();

            ArgumentCaptor<ReportPrimerVaccination> reportPrimerCaptor = ArgumentCaptor.forClass(ReportPrimerVaccination.class);
            Mockito.verify(primerVaccinationDataService, Mockito.times(4)).create(reportPrimerCaptor.capture());
            List<ReportPrimerVaccination> reportPrimerList = reportPrimerCaptor.getAllValues();

            ArgumentCaptor<ReportBoosterVaccination> reportBoosterCaptor = ArgumentCaptor.forClass(ReportBoosterVaccination.class);
            Mockito.verify(boosterVaccinationDataService, Mockito.times(4)).create(reportBoosterCaptor.capture());
            List<ReportBoosterVaccination> reportBoosterList = reportBoosterCaptor.getAllValues();

            for (int i = 0; i < 4; i++) {
                LocalDate date = new LocalDate(2015, 7, i + 2);
                assertEquals(reportPrimerList.get(i).getDate(), date);
                assertEquals(reportBoosterList.get(i).getDate(), date);
            }
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldUpdateDailyReports() {
        try {
            fakeNow(newDateTime(2015, 7, 6, 12, 0, 0));
            DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

            Config config = new Config();
            config.setLastCalculationDate(DateUtil.now().minusDays(5).toString(formatter));
            config.setGenerateReports(true);
            config.setPrimerVaccinationReportsToUpdate(new HashSet<>(Arrays.asList("2015-06-21", "2015-06-19")));
            config.setBoosterVaccinationReportsToUpdate(new HashSet<>(Arrays.asList("2015-06-17", "2015-06-14")));
            Mockito.when(configService.getConfig()).thenReturn(config);

            Mockito.doCallRealMethod().when(reportUpdateService).setConfigService(Mockito.any(ConfigService.class));
            Mockito.when(reportUpdateService.getPrimerVaccinationReportsToUpdate()).thenCallRealMethod();
            Mockito.when(reportUpdateService.getBoosterVaccinationReportsToUpdate()).thenCallRealMethod();

            reportUpdateService.setConfigService(configService);

            reportService.generateDailyReports();

            assertEquals(0, config.getPrimerVaccinationReportsToUpdate().size());
            assertEquals(0, config.getBoosterVaccinationReportsToUpdate().size());

            Mockito.verify(primerVaccinationDataService, Mockito.times(6)).create(Mockito.any(ReportPrimerVaccination.class));
            Mockito.verify(boosterVaccinationDataService, Mockito.times(6)).create(Mockito.any(ReportBoosterVaccination.class));
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldStartGenerateFromOldestVaccinationDay() {
        try {
            fakeNow(newDateTime(2015, 6, 28, 12, 0, 0));

            Config config = new Config();
            config.setGenerateReports(true);
            Mockito.when(configService.getConfig()).thenReturn(config);

            assertNull(config.getLastCalculationDate());
            assertNull(config.getFirstCalculationStartDate());

            Mockito.when(subjectService.findOldestPrimerVaccinationDate()).thenReturn(new LocalDate(2015, 6, 21));

            reportService.generateDailyReports();

            ArgumentCaptor<ReportPrimerVaccination> reportPrimerCaptor = ArgumentCaptor.forClass(ReportPrimerVaccination.class);
            Mockito.verify(primerVaccinationDataService, Mockito.times(7)).create(reportPrimerCaptor.capture());
            List<ReportPrimerVaccination> reportPrimerList = reportPrimerCaptor.getAllValues();

            ArgumentCaptor<ReportBoosterVaccination> reportBoosterCaptor = ArgumentCaptor.forClass(ReportBoosterVaccination.class);
            Mockito.verify(boosterVaccinationDataService, Mockito.times(7)).create(reportBoosterCaptor.capture());
            List<ReportBoosterVaccination> reportBoosterList = reportBoosterCaptor.getAllValues();

            for (int i = 0; i < 7; i++) {
                LocalDate date = new LocalDate(2015, 6, i + 21);
                assertEquals(reportPrimerList.get(i).getDate(), date);
                assertEquals(reportBoosterList.get(i).getDate(), date);
            }
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldUseReportStartDateIfLastReportDateIsNull() {
        try {
            fakeNow(newDateTime(2015, 6, 28, 12, 0, 0));

            Config config = new Config();
            config.setFirstCalculationStartDate("2015-06-25");
            config.setGenerateReports(true);
            Mockito.when(configService.getConfig()).thenReturn(config);

            assertNull(config.getLastCalculationDate());

            reportService.generateDailyReports();

            ArgumentCaptor<ReportPrimerVaccination> reportPrimerCaptor = ArgumentCaptor.forClass(ReportPrimerVaccination.class);
            Mockito.verify(primerVaccinationDataService, Mockito.times(3)).create(reportPrimerCaptor.capture());
            List<ReportPrimerVaccination> reportPrimerList = reportPrimerCaptor.getAllValues();

            ArgumentCaptor<ReportBoosterVaccination> reportBoosterCaptor = ArgumentCaptor.forClass(ReportBoosterVaccination.class);
            Mockito.verify(boosterVaccinationDataService, Mockito.times(3)).create(reportBoosterCaptor.capture());
            List<ReportBoosterVaccination> reportBoosterList = reportBoosterCaptor.getAllValues();

            for (int i = 0; i < 3; i++) {
                LocalDate date = new LocalDate(2015, 6, i + 25);
                assertEquals(reportPrimerList.get(i).getDate(), date);
                assertEquals(reportBoosterList.get(i).getDate(), date);
            }
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldGenerateIvrAndSmsStatisticReportsForAllRecordsIfLastCalculationDateIsEmpty() {
        Config config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        Mockito.when(configService.getConfig()).thenReturn(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "",  "", "", "", "");
        Mockito.when(subjectService.findSubjectBySubjectId("1")).thenReturn(subject);

        List<CallDetailRecord> initialCallDetailRecords = new ArrayList<>();

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "1");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("01"), Mockito.any(QueryParams.class))).thenReturn(Collections.singletonList(callDetailRecord));

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "22");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "102", "02", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "1");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-09-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "02", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:05:00.0000");
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("02"), Mockito.any(QueryParams.class))).thenReturn(Collections.singletonList(callDetailRecord));

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "1");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("03"), Mockito.any(QueryParams.class))).thenReturn(Collections.singletonList(callDetailRecord));


        Mockito.when(callDetailRecordDataService.findByCallStatus(EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED)).thenReturn(initialCallDetailRecords);


        reportService.generateIvrAndSmsStatisticReports();

        Mockito.verify(ivrAndSmsStatisticReportDataService, Mockito.times(3)).create(Mockito.any(IvrAndSmsStatisticReport.class));
    }

    @Test
    public void shouldGenerateIvrAndSmsStatisticReportsFromLastCalculationDate() {
        Config config = new Config();
        config.setLastCalculationDateForIvrReports("2015-10-01");
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        Mockito.when(configService.getConfig()).thenReturn(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "",  "", "", "", "");
        Mockito.when(subjectService.findSubjectBySubjectId("1")).thenReturn(subject);

        List<CallDetailRecord> initialCallDetailRecords = new ArrayList<>();

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "1");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("01"), Mockito.any(QueryParams.class))).thenReturn(Collections.singletonList(callDetailRecord));

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "22");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "102", "02", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "1");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-09-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "02", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:05:00.0000");
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("02"), Mockito.any(QueryParams.class))).thenReturn(Collections.singletonList(callDetailRecord));

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "1");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("03"), Mockito.any(QueryParams.class))).thenReturn(Collections.singletonList(callDetailRecord));


        Mockito.when(callDetailRecordDataService.findByCallStatus(EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED)).thenReturn(initialCallDetailRecords);

        for (CallDetailRecord detailRecord : initialCallDetailRecords) {
            Mockito.when(callDetailRecordDataService.findByMotechTimestampAndCallStatus(detailRecord.getMotechTimestamp().split(" ")[0], EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED)).thenReturn(Collections.singletonList(detailRecord));
        }


        reportService.generateIvrAndSmsStatisticReports();

        Mockito.verify(ivrAndSmsStatisticReportDataService, Mockito.times(2)).create(Mockito.any(IvrAndSmsStatisticReport.class));
    }

    @Test
    public void shouldAddReportToUpdateListIfSmsHasNoFinishedStatus() {
        Config config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        Mockito.when(configService.getConfig()).thenReturn(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "",  "", "", "", "");
        Mockito.when(subjectService.findSubjectBySubjectId("1")).thenReturn(subject);

        List<CallDetailRecord> initialCallDetailRecords = new ArrayList<>();
        List<CallDetailRecord> callDetailRecords1 = new ArrayList<>();
        List<CallDetailRecord> callDetailRecords2 = new ArrayList<>();

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecords1.add(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecords1.add(callDetailRecord);
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("01"), Mockito.any(QueryParams.class))).thenReturn(callDetailRecords1);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "22");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "102", "02", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-09-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "02", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:05:00.0000");
        callDetailRecords2.add(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "02", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:05:00.0000");
        callDetailRecords2.add(callDetailRecord);
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("02"), Mockito.any(QueryParams.class))).thenReturn(callDetailRecords2);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "1");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("03"), Mockito.any(QueryParams.class))).thenReturn(Collections.singletonList(callDetailRecord));


        Mockito.when(callDetailRecordDataService.findByCallStatus(EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED)).thenReturn(initialCallDetailRecords);


        reportService.generateIvrAndSmsStatisticReports();

        Mockito.verify(ivrAndSmsStatisticReportDataService, Mockito.times(3)).create(Mockito.any(IvrAndSmsStatisticReport.class));

        assertEquals(2, config.getIvrAndSmsStatisticReportsToUpdate().size());
        assertTrue(config.getIvrAndSmsStatisticReportsToUpdate().contains("101"));
        assertTrue(config.getIvrAndSmsStatisticReportsToUpdate().contains("102"));
    }

    @Test
    public void shouldUpdateReports() {
        Config config = new Config();
        config.setLastCalculationDateForIvrReports("2015-10-20");
        config.setIvrAndSmsStatisticReportsToUpdate(new HashSet<>(Arrays.asList("101", "102")));
        Mockito.when(configService.getConfig()).thenReturn(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "",  "", "", "", "");
        Mockito.when(subjectService.findSubjectBySubjectId("1")).thenReturn(subject);

        List<CallDetailRecord> initialCallDetailRecords = new ArrayList<>();
        List<CallDetailRecord> callDetailRecords1 = new ArrayList<>();
        List<CallDetailRecord> callDetailRecords2 = new ArrayList<>();
        List<CallDetailRecord> recordsToUpdate = new ArrayList<>();

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);
        recordsToUpdate.add(callDetailRecord);


        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecords1.add(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecords1.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_END_TIMESTAMP, "2015-10-10 8:06:00");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:06:00.0000");
        callDetailRecords1.add(callDetailRecord);
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("01"), Mockito.any(QueryParams.class))).thenReturn(callDetailRecords1);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "22");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "102", "02", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);
        recordsToUpdate.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-09-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "02", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:05:00.0000");
        callDetailRecords2.add(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "02", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-09-10 8:05:00.0000");
        callDetailRecords2.add(callDetailRecord);
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("02"), Mockito.any(QueryParams.class))).thenReturn(callDetailRecords2);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "1");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("03"), Mockito.any(QueryParams.class))).thenReturn(Collections.singletonList(callDetailRecord));


        Mockito.when(callDetailRecordDataService.findByMotechCallIds(config.getIvrAndSmsStatisticReportsToUpdate())).thenReturn(recordsToUpdate);
        Mockito.when(callDetailRecordDataService.findByCallStatus(EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED)).thenReturn(initialCallDetailRecords);

        for (CallDetailRecord detailRecord : initialCallDetailRecords) {
            Mockito.when(callDetailRecordDataService.findByMotechTimestampAndCallStatus(detailRecord.getMotechTimestamp().split(" ")[0], EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED)).thenReturn(Collections.singletonList(detailRecord));
        }


        reportService.generateIvrAndSmsStatisticReports();

        Mockito.verify(ivrAndSmsStatisticReportDataService, Mockito.times(3)).create(Mockito.any(IvrAndSmsStatisticReport.class));


        assertEquals(1, config.getIvrAndSmsStatisticReportsToUpdate().size());
        assertTrue(config.getIvrAndSmsStatisticReportsToUpdate().contains("102"));
    }

    @Test
    public void shouldNotAddSmsReceivedDateIfSmsSentButHasNoFinishedRecord() {
        Config config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        Mockito.when(configService.getConfig()).thenReturn(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "", "",  "", "", "");
        Mockito.when(subjectService.findSubjectBySubjectId("1")).thenReturn(subject);

        List<CallDetailRecord> initialCallDetailRecords = new ArrayList<>();
        List<CallDetailRecord> callDetailRecords1 = new ArrayList<>();

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecords1.add(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecords1.add(callDetailRecord);


        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("01"), Mockito.any(QueryParams.class))).thenReturn(callDetailRecords1);
        Mockito.when(callDetailRecordDataService.findByCallStatus(EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED)).thenReturn(initialCallDetailRecords);


        reportService.generateIvrAndSmsStatisticReports();


        ArgumentCaptor<IvrAndSmsStatisticReport> reportCaptor = ArgumentCaptor.forClass(IvrAndSmsStatisticReport.class);
        Mockito.verify(ivrAndSmsStatisticReportDataService, Mockito.times(1)).create(reportCaptor.capture());

        IvrAndSmsStatisticReport ivrAndSmsStatisticReport = reportCaptor.getValue();

        assertEquals(SmsStatus.YES, ivrAndSmsStatisticReport.getSmsStatus());
        assertNull(ivrAndSmsStatisticReport.getSmsReceivedDate());
    }

    @Test
    public void shouldSetAllDataAndGetReceivedDateFromFinishedRecordForCallAndSms() {
        Config config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        Mockito.when(configService.getConfig()).thenReturn(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "",  "", "", "", "");
        Mockito.when(subjectService.findSubjectBySubjectId("1")).thenReturn(subject);

        List<CallDetailRecord> initialCallDetailRecords = new ArrayList<>();
        List<CallDetailRecord> callDetailRecords1 = new ArrayList<>();

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecords1.add(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecords1.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_END_TIMESTAMP, "2015-10-10 8:06:00");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:07:00.0000");
        callDetailRecords1.add(callDetailRecord);
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("01"), Mockito.any(QueryParams.class))).thenReturn(callDetailRecords1);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "2");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("03"), Mockito.any(QueryParams.class))).thenReturn(Collections.singletonList(callDetailRecord));


        Mockito.when(callDetailRecordDataService.findByCallStatus(EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED)).thenReturn(initialCallDetailRecords);


        reportService.generateIvrAndSmsStatisticReports();


        ArgumentCaptor<IvrAndSmsStatisticReport> reportCaptor = ArgumentCaptor.forClass(IvrAndSmsStatisticReport.class);
        Mockito.verify(ivrAndSmsStatisticReportDataService, Mockito.times(2)).create(reportCaptor.capture());

        List<IvrAndSmsStatisticReport> ivrAndSmsStatisticReports = reportCaptor.getAllValues();
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
        assertEquals(SmsStatus.YES, smsStatisticReport.getSmsStatus());
        assertEquals(new DateTime(2015, 10, 10, 8, 6, 0), smsStatisticReport.getSmsReceivedDate());
        assertEquals(subject, smsStatisticReport.getSubject());

        assertEquals("23", ivrStatisticReport.getMessageId());
        assertEquals(new DateTime(2015, 10, 21, 8, 0, 0), ivrStatisticReport.getSendDate());
        assertEquals(2, ivrStatisticReport.getNumberOfAttempts());
        assertEquals(20, ivrStatisticReport.getExpectedDuration(), 0.001);
        assertEquals(100, ivrStatisticReport.getMessagePercentListened(), 0.001);
        assertEquals(22, ivrStatisticReport.getTimeListenedTo(), 0.001);
        assertEquals(new DateTime(2015, 10, 21, 8, 4, 0), ivrStatisticReport.getReceivedDate());
        assertEquals(SmsStatus.NO, ivrStatisticReport.getSmsStatus());
        assertNull(ivrStatisticReport.getSmsReceivedDate());
        assertEquals(subject, ivrStatisticReport.getSubject());
    }

    @Test
    public void shouldCreateReportsForAllSubjectsInTheList() {
        Config config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        Mockito.when(configService.getConfig()).thenReturn(config);

        Subject subject1 = new Subject("1", "", "", "", "123456789","", Language.English, "com", "",  "", "", "", "");
        Mockito.when(subjectService.findSubjectBySubjectId("1")).thenReturn(subject1);
        Subject subject2 = new Subject("2", "", "", "", "111111111","", Language.English, "com", "",  "", "", "", "");
        Mockito.when(subjectService.findSubjectBySubjectId("2")).thenReturn(subject2);
        Subject subject3 = new Subject("3", "", "", "", "222222222","", Language.English, "com", "",  "", "", "", "");
        Mockito.when(subjectService.findSubjectBySubjectId("3")).thenReturn(subject3);

        List<CallDetailRecord> initialCallDetailRecords = new ArrayList<>();

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1,2,3");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "2");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("03"), Mockito.any(QueryParams.class))).thenReturn(Collections.singletonList(callDetailRecord));


        Mockito.when(callDetailRecordDataService.findByCallStatus(EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED)).thenReturn(initialCallDetailRecords);


        reportService.generateIvrAndSmsStatisticReports();

        ArgumentCaptor<IvrAndSmsStatisticReport> reportCaptor = ArgumentCaptor.forClass(IvrAndSmsStatisticReport.class);
        Mockito.verify(ivrAndSmsStatisticReportDataService, Mockito.times(3)).create(reportCaptor.capture());

        List<IvrAndSmsStatisticReport> ivrAndSmsStatisticReports = reportCaptor.getAllValues();
        assertEquals(3, ivrAndSmsStatisticReports.size());

        assertEquals(subject1, ivrAndSmsStatisticReports.get(0).getSubject());
        assertEquals(subject2, ivrAndSmsStatisticReports.get(1).getSubject());
        assertEquals(subject3, ivrAndSmsStatisticReports.get(2).getSubject());
    }

    @Test
    public void shouldNotCreateReportsWhenProviderCallIdOrProviderExtraDataAreEmpty() {
        Config config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        Mockito.when(configService.getConfig()).thenReturn(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "",  "", "", "", "");
        Mockito.when(subjectService.findSubjectBySubjectId("1")).thenReturn(subject);

        List<CallDetailRecord> initialCallDetailRecords = new ArrayList<>();
        List<CallDetailRecord> callDetailRecords1 = new ArrayList<>();

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecords1.add(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecords1.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_END_TIMESTAMP, "2015-10-10 8:06:00");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:07:00.0000");
        callDetailRecords1.add(callDetailRecord);
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("01"), Mockito.any(QueryParams.class))).thenReturn(callDetailRecords1);

        providerExtraData = new HashMap<>();

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "2");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("03"), Mockito.any(QueryParams.class))).thenReturn(Collections.singletonList(callDetailRecord));


        Mockito.when(callDetailRecordDataService.findByCallStatus(EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED)).thenReturn(initialCallDetailRecords);


        reportService.generateIvrAndSmsStatisticReports();

        Mockito.verify(ivrAndSmsStatisticReportDataService, Mockito.never()).create(Mockito.any(IvrAndSmsStatisticReport.class));
    }

    @Test
    public void shouldNotCreateReportsWhenSubjectIdsIsEmptyOrNoSubjectsFound() {
        Config config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        Mockito.when(configService.getConfig()).thenReturn(config);

        List<CallDetailRecord> initialCallDetailRecords = new ArrayList<>();
        List<CallDetailRecord> callDetailRecords1 = new ArrayList<>();

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecords1.add(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecords1.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_END_TIMESTAMP, "2015-10-10 8:06:00");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:07:00.0000");
        callDetailRecords1.add(callDetailRecord);
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("01"), Mockito.any(QueryParams.class))).thenReturn(callDetailRecords1);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "2");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("03"), Mockito.any(QueryParams.class))).thenReturn(Collections.singletonList(callDetailRecord));


        Mockito.when(callDetailRecordDataService.findByCallStatus(EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED)).thenReturn(initialCallDetailRecords);


        reportService.generateIvrAndSmsStatisticReports();

        Mockito.verify(ivrAndSmsStatisticReportDataService, Mockito.never()).create(Mockito.any(IvrAndSmsStatisticReport.class));
    }

    @Test
    public void shouldNotCreateReportsWhenNoProviderTimestampFoundForFinishedRecord() {
        Config config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        Mockito.when(configService.getConfig()).thenReturn(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "", "",  "", "", "");
        Mockito.when(subjectService.findSubjectBySubjectId("1")).thenReturn(subject);

        List<CallDetailRecord> initialCallDetailRecords = new ArrayList<>();
        List<CallDetailRecord> callDetailRecords1 = new ArrayList<>();

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecords1.add(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecords1.add(callDetailRecord);

        providerExtraData = new HashMap<>();

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:07:00.0000");
        callDetailRecords1.add(callDetailRecord);
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("01"), Mockito.any(QueryParams.class))).thenReturn(callDetailRecords1);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "2");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("03"), Mockito.any(QueryParams.class))).thenReturn(Collections.singletonList(callDetailRecord));


        Mockito.when(callDetailRecordDataService.findByCallStatus(EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED)).thenReturn(initialCallDetailRecords);


        reportService.generateIvrAndSmsStatisticReports();

        Mockito.verify(ivrAndSmsStatisticReportDataService, Mockito.never()).create(Mockito.any(IvrAndSmsStatisticReport.class));
    }

    @Test
    public void shouldNotCreateReportsWhenTooMuchRecordsWithFinishedStatus() {
        Config config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        Mockito.when(configService.getConfig()).thenReturn(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "", "",  "", "", "");
        Mockito.when(subjectService.findSubjectBySubjectId("1")).thenReturn(subject);

        List<CallDetailRecord> initialCallDetailRecords = new ArrayList<>();
        List<CallDetailRecord> callDetailRecords1 = new ArrayList<>();
        List<CallDetailRecord> callDetailRecords3 = new ArrayList<>();

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecords1.add(callDetailRecord);

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecords1.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_END_TIMESTAMP, "2015-10-10 8:06:00");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:07:00.0000");
        callDetailRecords1.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_END_TIMESTAMP, "2015-10-10 8:06:00");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:07:00.0000");
        callDetailRecords1.add(callDetailRecord);
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("01"), Mockito.any(QueryParams.class))).thenReturn(callDetailRecords1);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "2");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        callDetailRecords3.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "2");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-21 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "03", providerExtraData, "22", "100");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:05:00.0000");
        callDetailRecords3.add(callDetailRecord);
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("01"), Mockito.any(QueryParams.class))).thenReturn(callDetailRecords3);


        Mockito.when(callDetailRecordDataService.findByCallStatus(EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED)).thenReturn(initialCallDetailRecords);


        reportService.generateIvrAndSmsStatisticReports();

        Mockito.verify(ivrAndSmsStatisticReportDataService, Mockito.never()).create(Mockito.any(IvrAndSmsStatisticReport.class));
    }

    @Test
    public void shouldNotCreateReportsWhenSmsSentAndThereAreNoFailedCallRecord() {
        Config config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        Mockito.when(configService.getConfig()).thenReturn(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "",  "", "", "", "");
        Mockito.when(subjectService.findSubjectBySubjectId("1")).thenReturn(subject);

        List<CallDetailRecord> initialCallDetailRecords = new ArrayList<>();
        List<CallDetailRecord> callDetailRecords1 = new ArrayList<>();

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "21");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "101", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS, "3");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_START_TIMESTAMP, "2015-10-10 8:04:00");
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_MESSAGE_SECOND_COMPLETED, "20");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED, "", "", "01", null, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:05:00.0000");
        callDetailRecords1.add(callDetailRecord);

        providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.IVR_CALL_DETAIL_RECORD_END_TIMESTAMP, "2015-10-10 8:06:00");

        callDetailRecord = new CallDetailRecord("", "", "", "", null,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED, "", "", "01", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-10 8:07:00.0000");
        callDetailRecords1.add(callDetailRecord);
        Mockito.when(callDetailRecordDataService.findByExactProviderCallId(Mockito.eq("01"), Mockito.any(QueryParams.class))).thenReturn(callDetailRecords1);


        Mockito.when(callDetailRecordDataService.findByCallStatus(EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED)).thenReturn(initialCallDetailRecords);


        reportService.generateIvrAndSmsStatisticReports();

        Mockito.verify(ivrAndSmsStatisticReportDataService, Mockito.never()).create(Mockito.any(IvrAndSmsStatisticReport.class));
    }

    @Test
    public void shouldNotCreateReportsWhenNoSmsSentAndThereAreNoFinishedCallRecord() {
        Config config = new Config();
        config.setLastCalculationDateForIvrReports(null);
        config.setIvrAndSmsStatisticReportsToUpdate(null);
        Mockito.when(configService.getConfig()).thenReturn(config);

        Subject subject = new Subject("1", "", "", "", "123456789","", Language.English, "com", "", "",  "", "", "");
        Mockito.when(subjectService.findSubjectBySubjectId("1")).thenReturn(subject);

        List<CallDetailRecord> initialCallDetailRecords = new ArrayList<>();

        Map<String, String> providerExtraData = new HashMap<>();
        providerExtraData.put(EbodacConstants.MESSAGE_ID, "23");
        providerExtraData.put(EbodacConstants.SUBJECT_IDS, "1");

        CallDetailRecord callDetailRecord = new CallDetailRecord("", "", "", "", CallDirection.OUTBOUND,
                EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED, "", "103", "03", providerExtraData, "", "");
        callDetailRecord.setMotechTimestamp("2015-10-21 8:00:00.0000");
        initialCallDetailRecords.add(callDetailRecord);


        Mockito.when(callDetailRecordDataService.findByCallStatus(EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED)).thenReturn(initialCallDetailRecords);


        reportService.generateIvrAndSmsStatisticReports();

        Mockito.verify(ivrAndSmsStatisticReportDataService, Mockito.never()).create(Mockito.any(IvrAndSmsStatisticReport.class));
    }
}
