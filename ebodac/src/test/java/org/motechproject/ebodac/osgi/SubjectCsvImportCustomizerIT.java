package org.motechproject.ebodac.osgi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.domain.Enrollment;
import org.motechproject.ebodac.domain.enums.EnrollmentStatus;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.SubjectEnrollments;
import org.motechproject.ebodac.repository.EnrollmentDataService;
import org.motechproject.ebodac.repository.IvrAndSmsStatisticReportDataService;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.service.RaveImportService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.ebodac.service.VisitService;
import org.motechproject.ebodac.service.impl.csv.SubjectCsvImportCustomizer;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.service.CsvImportExportService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.NameMatcher;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class SubjectCsvImportCustomizerIT extends BasePaxIT {

    @Inject
    private IvrAndSmsStatisticReportDataService ivrAndSmsStatisticReportDataService;

    @Inject
    private BundleContext bundleContext;

    @Inject
    private EbodacEnrollmentService ebodacEnrollmentService;

    @Inject
    private SubjectService subjectService;

    @Inject
    private VisitService visitService;

    @Inject
    private EnrollmentDataService enrollmentDataService;

    @Inject
    private SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    @Inject
    private RaveImportService raveImportService;

    @Inject
    private CsvImportExportService csvImportExportService;

    @Inject
    private EntityService entityService;

    private SubjectCsvImportCustomizer subjectCsvImportCustomizer;

    private Scheduler scheduler;

    @Before
    public void cleanBefore() throws SchedulerException {
        subjectCsvImportCustomizer = new SubjectCsvImportCustomizer();
        subjectCsvImportCustomizer.setEbodacEnrollmentService(ebodacEnrollmentService);
        subjectCsvImportCustomizer.setSubjectService(subjectService);
        scheduler = (Scheduler) getQuartzScheduler(bundleContext);
        clearJobs();
        ivrAndSmsStatisticReportDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        enrollmentDataService.deleteAll();
        subjectService.deleteAll();
    }

    @After
    public void cleanAfter() throws SchedulerException {
        clearJobs();
        ivrAndSmsStatisticReportDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        enrollmentDataService.deleteAll();
        subjectService.deleteAll();
    }

    @Test
    public void shouldUnerollSubjectWhenPhoneNumberOrLanguageRemoved() throws IOException, InterruptedException {
        assertEquals(0, enrollmentDataService.retrieveAll().size());

        EntityDto entityDto = entityService.getEntityByClassName(Subject.class.getName());
        InputStream inputStream = getClass().getResourceAsStream("/participants3WithRequiredData.csv");
        Reader reader = new InputStreamReader(inputStream);
        csvImportExportService.importCsv(entityDto.getId(), reader, "participants3WithRequiredData.csv", subjectCsvImportCustomizer);
        inputStream.close();
        reader.close();

        Subject subject1 = subjectService.findSubjectBySubjectId("1");
        Subject subject3 = subjectService.findSubjectBySubjectId("3");

        assertNotNull(subject1.getLanguage());
        assertNotNull(subject1.getPhoneNumber());
        assertNotNull(subject3.getLanguage());
        assertNotNull(subject3.getPhoneNumber());

        inputStream = getClass().getResourceAsStream("/enrollDuplicatedSimpleStage1.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollDuplicatedSimpleStage1.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments1 = subjectEnrollmentsDataService.findBySubjectId("1");
        SubjectEnrollments subjectEnrollments2 = subjectEnrollmentsDataService.findBySubjectId("3");

        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments1.getStatus());
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments2.getStatus());

        inputStream = getClass().getResourceAsStream("/participantsWithoutRequiredData.csv");
        reader = new InputStreamReader(inputStream);
        csvImportExportService.importCsv(entityDto.getId(), reader, "participantsWithoutRequiredData.csv", subjectCsvImportCustomizer);
        inputStream.close();
        reader.close();

        subject1 = subjectService.findSubjectBySubjectId("1");
        subject3 = subjectService.findSubjectBySubjectId("3");

        assertNotNull(subject1.getLanguage());
        assertNull(subject1.getPhoneNumber());
        assertNull(subject3.getLanguage());
        assertNotNull(subject3.getPhoneNumber());

        subjectEnrollments1 = subjectEnrollmentsDataService.findBySubjectId("1");
        subjectEnrollments2 = subjectEnrollmentsDataService.findBySubjectId("3");

        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments1.getStatus());
        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments2.getStatus());
    }

    @Test
    public void shouldScheduleJobsForDuplicatedEnrolmentWhenSubjectPhoneNumberChangedForThisEnrollment() throws IOException, InterruptedException, SchedulerException {
        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";

        assertEquals(0, enrollmentDataService.retrieveAll().size());

        EntityDto entityDto = entityService.getEntityByClassName(Subject.class.getName());
        InputStream inputStream = getClass().getResourceAsStream("/participants2WithRequiredData.csv");
        Reader reader = new InputStreamReader(inputStream);
        csvImportExportService.importCsv(entityDto.getId(), reader, "participants2WithRequiredData.csv", subjectCsvImportCustomizer);
        inputStream.close();
        reader.close();

        Subject subject1 = subjectService.findSubjectBySubjectId("1");
        Subject subject2 = subjectService.findSubjectBySubjectId("2");

        assertEquals("123456789", subject1.getPhoneNumber());
        assertEquals("123456789", subject2.getPhoneNumber());

        inputStream = getClass().getResourceAsStream("/enrollDuplicatedSimpleStage1.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollDuplicatedSimpleStage1.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments1 = subjectEnrollmentsDataService.findBySubjectId(subject1.getSubjectId());
        SubjectEnrollments subjectEnrollments2 = subjectEnrollmentsDataService.findBySubjectId(subject2.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());

        Enrollment enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        Enrollment enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();

        String triggerKeyString1 = campaignCompletedString + enrollment1.getCampaignNameWithBoostVacDayAndStageId() + "." + enrollment1.getExternalId() + runonce;
        String triggerKeyString2 = campaignCompletedString + enrollment2.getCampaignNameWithBoostVacDayAndStageId() + "." + enrollment2.getExternalId() + runonce;

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));
        assertEquals(1, enrollment1.getDuplicatedEnrollments().size());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(enrollment1, enrollment2.getParentEnrollment());

        inputStream = getClass().getResourceAsStream("/participant2PhoneNumberChanged.csv");
        reader = new InputStreamReader(inputStream);
        csvImportExportService.importCsv(entityDto.getId(), reader, "participant2PhoneNumberChanged.csv", subjectCsvImportCustomizer);
        inputStream.close();
        reader.close();

        subject1 = subjectService.findSubjectBySubjectId("1");
        subject2 = subjectService.findSubjectBySubjectId("2");

        assertEquals("123456789", subject1.getPhoneNumber());
        assertEquals("987654321", subject2.getPhoneNumber());

        subjectEnrollments1 = subjectEnrollmentsDataService.findBySubjectId(subject1.getSubjectId());
        subjectEnrollments2 = subjectEnrollmentsDataService.findBySubjectId(subject2.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());

        enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(null, enrollment2.getParentEnrollment());
    }

    @Test
    public void shouldFindNewParentWhenSubjectPhoneNumberChangedForOldParent() throws IOException, InterruptedException, SchedulerException {
        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";

        assertEquals(0, enrollmentDataService.retrieveAll().size());

        EntityDto entityDto = entityService.getEntityByClassName(Subject.class.getName());
        InputStream inputStream = getClass().getResourceAsStream("/participants2WithRequiredData.csv");
        Reader reader = new InputStreamReader(inputStream);
        csvImportExportService.importCsv(entityDto.getId(), reader, "participants2WithRequiredData.csv", subjectCsvImportCustomizer);
        inputStream.close();
        reader.close();

        Subject subject1 = subjectService.findSubjectBySubjectId("1");
        Subject subject2 = subjectService.findSubjectBySubjectId("2");

        assertEquals("123456789", subject1.getPhoneNumber());
        assertEquals("123456789", subject2.getPhoneNumber());

        inputStream = getClass().getResourceAsStream("/enrollDuplicatedSimpleStage1.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollDuplicatedSimpleStage1.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments1 = subjectEnrollmentsDataService.findBySubjectId(subject1.getSubjectId());
        SubjectEnrollments subjectEnrollments2 = subjectEnrollmentsDataService.findBySubjectId(subject2.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());

        Enrollment enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        Enrollment enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();

        String triggerKeyString1 = campaignCompletedString + enrollment1.getCampaignNameWithBoostVacDayAndStageId() + "." + enrollment1.getExternalId() + runonce;
        String triggerKeyString2 = campaignCompletedString + enrollment2.getCampaignNameWithBoostVacDayAndStageId() + "." + enrollment2.getExternalId() + runonce;

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));
        assertEquals(1, enrollment1.getDuplicatedEnrollments().size());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(enrollment1, enrollment2.getParentEnrollment());

        inputStream = getClass().getResourceAsStream("/participant1PhoneNumberChanged.csv");
        reader = new InputStreamReader(inputStream);
        csvImportExportService.importCsv(entityDto.getId(), reader, "participant1PhoneNumberChanged.csv", subjectCsvImportCustomizer);
        inputStream.close();
        reader.close();

        subject1 = subjectService.findSubjectBySubjectId("1");
        subject2 = subjectService.findSubjectBySubjectId("2");

        assertEquals("987654321", subject1.getPhoneNumber());
        assertEquals("123456789", subject2.getPhoneNumber());

        subjectEnrollments1 = subjectEnrollmentsDataService.findBySubjectId(subject1.getSubjectId());
        subjectEnrollments2 = subjectEnrollmentsDataService.findBySubjectId(subject2.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());

        enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(null, enrollment2.getParentEnrollment());
    }

    @Test
    public void shouldChangeGroupForDuplicatedEnrolmentWhenSubjectPhoneNumberChanged() throws IOException, InterruptedException, SchedulerException {
        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";

        assertEquals(0, enrollmentDataService.retrieveAll().size());

        EntityDto entityDto = entityService.getEntityByClassName(Subject.class.getName());
        InputStream inputStream = getClass().getResourceAsStream("/participants3WithRequiredData.csv");
        Reader reader = new InputStreamReader(inputStream);
        csvImportExportService.importCsv(entityDto.getId(), reader, "participants3WithRequiredData.csv", subjectCsvImportCustomizer);
        inputStream.close();
        reader.close();

        Subject subject1 = subjectService.findSubjectBySubjectId("1");
        Subject subject2 = subjectService.findSubjectBySubjectId("2");
        Subject subject3 = subjectService.findSubjectBySubjectId("3");

        assertEquals("123456789", subject1.getPhoneNumber());
        assertEquals("123456789", subject2.getPhoneNumber());
        assertEquals("987654321", subject3.getPhoneNumber());

        inputStream = getClass().getResourceAsStream("/enrollDuplicatedSimpleStage1.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollDuplicatedSimpleStage1.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments1 = subjectEnrollmentsDataService.findBySubjectId(subject1.getSubjectId());
        SubjectEnrollments subjectEnrollments2 = subjectEnrollmentsDataService.findBySubjectId(subject2.getSubjectId());
        SubjectEnrollments subjectEnrollments3 = subjectEnrollmentsDataService.findBySubjectId(subject3.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());
        assertEquals(1, subjectEnrollments3.getEnrollments().size());

        Enrollment enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        Enrollment enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();
        Enrollment enrollment3 = subjectEnrollments3.getEnrollments().iterator().next();

        String triggerKeyString1 = campaignCompletedString + enrollment1.getCampaignNameWithBoostVacDayAndStageId() + "." + enrollment1.getExternalId() + runonce;
        String triggerKeyString2 = campaignCompletedString + enrollment2.getCampaignNameWithBoostVacDayAndStageId() + "." + enrollment2.getExternalId() + runonce;
        String triggerKeyString3 = campaignCompletedString + enrollment3.getCampaignNameWithBoostVacDayAndStageId() + "." + enrollment3.getExternalId() + runonce;

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));
        assertEquals(1, enrollment1.getDuplicatedEnrollments().size());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(enrollment1, enrollment2.getParentEnrollment());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment3.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
        assertEquals(0, enrollment3.getDuplicatedEnrollments().size());

        inputStream = getClass().getResourceAsStream("/participant2PhoneNumberChanged.csv");
        reader = new InputStreamReader(inputStream);
        csvImportExportService.importCsv(entityDto.getId(), reader, "participant2PhoneNumberChanged.csv", subjectCsvImportCustomizer);
        inputStream.close();
        reader.close();

        subject1 = subjectService.findSubjectBySubjectId("1");
        subject2 = subjectService.findSubjectBySubjectId("2");
        subject3 = subjectService.findSubjectBySubjectId("3");

        assertEquals("123456789", subject1.getPhoneNumber());
        assertEquals("987654321", subject2.getPhoneNumber());
        assertEquals("987654321", subject3.getPhoneNumber());

        subjectEnrollments1 = subjectEnrollmentsDataService.findBySubjectId(subject1.getSubjectId());
        subjectEnrollments2 = subjectEnrollmentsDataService.findBySubjectId(subject2.getSubjectId());
        subjectEnrollments3 = subjectEnrollmentsDataService.findBySubjectId(subject3.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());
        assertEquals(1, subjectEnrollments3.getEnrollments().size());

        enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();
        enrollment3 = subjectEnrollments3.getEnrollments().iterator().next();

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(enrollment3, enrollment2.getParentEnrollment());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment3.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
        assertEquals(1, enrollment3.getDuplicatedEnrollments().size());
    }

    @Test
    public void shouldFindNewParentAndUnscheduleJobsForOldParenWhenParentAddedToDifferentGroupAfterSubjectPhoneNumberChanged() throws IOException, InterruptedException, SchedulerException {
        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";

        assertEquals(0, enrollmentDataService.retrieveAll().size());

        EntityDto entityDto = entityService.getEntityByClassName(Subject.class.getName());
        InputStream inputStream = getClass().getResourceAsStream("/participants3WithRequiredData.csv");
        Reader reader = new InputStreamReader(inputStream);
        csvImportExportService.importCsv(entityDto.getId(), reader, "participants3WithRequiredData.csv", subjectCsvImportCustomizer);
        inputStream.close();
        reader.close();

        Subject subject1 = subjectService.findSubjectBySubjectId("1");
        Subject subject2 = subjectService.findSubjectBySubjectId("2");
        Subject subject3 = subjectService.findSubjectBySubjectId("3");

        assertEquals("123456789", subject1.getPhoneNumber());
        assertEquals("123456789", subject2.getPhoneNumber());
        assertEquals("987654321", subject3.getPhoneNumber());

        inputStream = getClass().getResourceAsStream("/enrollDuplicatedSimpleStage1.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollDuplicatedSimpleStage1.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments1 = subjectEnrollmentsDataService.findBySubjectId(subject1.getSubjectId());
        SubjectEnrollments subjectEnrollments2 = subjectEnrollmentsDataService.findBySubjectId(subject2.getSubjectId());
        SubjectEnrollments subjectEnrollments3 = subjectEnrollmentsDataService.findBySubjectId(subject3.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());
        assertEquals(1, subjectEnrollments3.getEnrollments().size());

        Enrollment enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        Enrollment enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();
        Enrollment enrollment3 = subjectEnrollments3.getEnrollments().iterator().next();

        String triggerKeyString1 = campaignCompletedString + enrollment1.getCampaignNameWithBoostVacDayAndStageId() + "." + enrollment1.getExternalId() + runonce;
        String triggerKeyString2 = campaignCompletedString + enrollment2.getCampaignNameWithBoostVacDayAndStageId() + "." + enrollment2.getExternalId() + runonce;
        String triggerKeyString3 = campaignCompletedString + enrollment3.getCampaignNameWithBoostVacDayAndStageId() + "." + enrollment3.getExternalId() + runonce;

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));
        assertEquals(1, enrollment1.getDuplicatedEnrollments().size());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(enrollment1, enrollment2.getParentEnrollment());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment3.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
        assertEquals(0, enrollment3.getDuplicatedEnrollments().size());

        inputStream = getClass().getResourceAsStream("/participant1PhoneNumberChanged.csv");
        reader = new InputStreamReader(inputStream);
        csvImportExportService.importCsv(entityDto.getId(), reader, "participant1PhoneNumberChanged.csv", subjectCsvImportCustomizer);
        inputStream.close();
        reader.close();

        subject1 = subjectService.findSubjectBySubjectId("1");
        subject2 = subjectService.findSubjectBySubjectId("2");
        subject3 = subjectService.findSubjectBySubjectId("3");

        assertEquals("987654321", subject1.getPhoneNumber());
        assertEquals("123456789", subject2.getPhoneNumber());
        assertEquals("987654321", subject3.getPhoneNumber());

        subjectEnrollments1 = subjectEnrollmentsDataService.findBySubjectId(subject1.getSubjectId());
        subjectEnrollments2 = subjectEnrollmentsDataService.findBySubjectId(subject2.getSubjectId());
        subjectEnrollments3 = subjectEnrollmentsDataService.findBySubjectId(subject3.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());
        assertEquals(1, subjectEnrollments3.getEnrollments().size());

        enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();
        enrollment3 = subjectEnrollments3.getEnrollments().iterator().next();

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));
        assertEquals(enrollment3, enrollment1.getParentEnrollment());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(null, enrollment2.getParentEnrollment());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment3.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
        assertEquals(1, enrollment3.getDuplicatedEnrollments().size());
    }

    @Test
    public void shouldCreateEnrollmentRecordsForSubjectWhenLanguageOrPhoneNumberIsAdded() throws IOException, InterruptedException {

        Subject subject2 = new Subject();
        subject2.setSubjectId("2");
        subject2.setSiteName("siteName");
        subject2.setSiteId("siteId");
        subjectService.create(subject2);

        EntityDto entityDto = entityService.getEntityByClassName(Subject.class.getName());
        InputStream inputStream = getClass().getResourceAsStream("/participantsWithoutRequiredData.csv");
        Reader reader = new InputStreamReader(inputStream);
        csvImportExportService.importCsv(entityDto.getId(), reader, "participantsWithoutRequiredData.csv", subjectCsvImportCustomizer);
        inputStream.close();
        reader.close();

        Subject subject1 = subjectService.findSubjectBySubjectId("1");
        Subject subject3 = subjectService.findSubjectBySubjectId("3");

        assertNotNull(subject1.getLanguage());
        assertNull(subject1.getPhoneNumber());
        assertNull(subject3.getLanguage());
        assertNotNull(subject3.getPhoneNumber());

        inputStream = getClass().getResourceAsStream("/enrollDuplicatedSimpleStage1.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollDuplicatedSimpleStage1.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments1 = subjectEnrollmentsDataService.findBySubjectId(subject1.getSubjectId());
        SubjectEnrollments subjectEnrollments3 = subjectEnrollmentsDataService.findBySubjectId(subject3.getSubjectId());
        assertNull(subjectEnrollments1);
        assertNull(subjectEnrollments3);

        inputStream = getClass().getResourceAsStream("/participants3WithRequiredData.csv");
        reader = new InputStreamReader(inputStream);
        csvImportExportService.importCsv(entityDto.getId(), reader, "participants3WithRequiredData.csv", subjectCsvImportCustomizer);
        inputStream.close();
        reader.close();

        subject1 = subjectService.findSubjectBySubjectId("1");
        subject3 = subjectService.findSubjectBySubjectId("3");

        assertNotNull(subject1.getLanguage());
        assertNotNull(subject1.getPhoneNumber());
        assertNotNull(subject3.getLanguage());
        assertNotNull(subject3.getPhoneNumber());

        subjectEnrollments1 = subjectEnrollmentsDataService.findBySubjectId(subject1.getSubjectId());
        subjectEnrollments3 = subjectEnrollmentsDataService.findBySubjectId(subject3.getSubjectId());
        assertNotNull(subjectEnrollments1);
        assertNotNull(subjectEnrollments3);
        assertEquals(EnrollmentStatus.INITIAL, subjectEnrollments1.getStatus());
        assertEquals(EnrollmentStatus.INITIAL, subjectEnrollments3.getStatus());
        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments3.getEnrollments().size());

        for (Enrollment enrollment: subjectEnrollments1.getEnrollments()) {
            assertEquals(EnrollmentStatus.INITIAL, enrollment.getStatus());
        }

        for (Enrollment enrollment: subjectEnrollments3.getEnrollments()) {
            assertEquals(EnrollmentStatus.INITIAL, enrollment.getStatus());
        }
    }

    private void clearJobs() throws SchedulerException {
        NameMatcher<JobKey> nameMatcher = NameMatcher.jobNameStartsWith("org.motechproject.messagecampaign");
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupStartsWith("default"));
        for (JobKey jobKey : jobKeys) {
            if (nameMatcher.isMatch(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        }
    }
}
