package org.motechproject.ebodac.osgi;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.domain.Enrollment;
import org.motechproject.ebodac.domain.enums.EnrollmentStatus;
import org.motechproject.ebodac.domain.enums.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.SubjectEnrollments;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.enums.VisitType;
import org.motechproject.ebodac.exception.EbodacEnrollmentException;
import org.motechproject.ebodac.repository.EnrollmentDataService;
import org.motechproject.ebodac.repository.IvrAndSmsStatisticReportDataService;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.service.RaveImportService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.ebodac.service.VisitService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class EbodacEnrollmentServiceIT extends BasePaxIT {

    private static final List<String> DAYS_OF_WEEK = new ArrayList<>(Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"));

    @Inject
    private BundleContext bundleContext;

    @Inject
    private ConfigService configService;

    @Inject
    private IvrAndSmsStatisticReportDataService ivrAndSmsStatisticReportDataService;

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
    private VisitDataService visitDataService;

    private Scheduler scheduler;

    private Config savedConfig;

    @Before
    public void cleanBefore() throws SchedulerException {
        scheduler = (Scheduler) getQuartzScheduler(bundleContext);
        clearJobs();
        ivrAndSmsStatisticReportDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        enrollmentDataService.deleteAll();
        subjectService.deleteAll();
        savedConfig = configService.getConfig();
    }

    @After
    public void cleanAfter() throws SchedulerException {
        clearAll();
    }

    @Test
    public void shouldEnrollAllVisitTypes() throws IOException, SchedulerException {
        shouldEnrollAllVisitTypesForStage("1");
        clearAll();
        shouldEnrollAllVisitTypesForStage("2");
    }

    @Ignore
    @Test
    public void shouldEnrollWhenGetZetesDataAfterRave() throws IOException, SchedulerException {
        shouldEnrollWhenGetZetesDataAfterRaveForStage("1");
        clearAll();
        shouldEnrollWhenGetZetesDataAfterRaveForStage("2");
    }

    @Test
    public void shouldReenrollWhenRaveProjectedDateWasChanged() throws IOException, SchedulerException {
        shouldReenrollWhenRaveProjectedDateWasChangedForStage("1");
        clearAll();
        shouldReenrollWhenRaveProjectedDateWasChangedForStage("2");
    }

    @Test
    public void shouldNotEnrollWhenSubjectDoesntHaveLanguageOrPhoneNumber() throws IOException, SchedulerException {
        shouldNotEnrollWhenSubjectDoesntHaveLanguageOrPhoneNumberForStage("1");
        clearAll();
        shouldNotEnrollWhenSubjectDoesntHaveLanguageOrPhoneNumberForStage("2");
    }

    @Ignore
    @Test
    public void shouldEnrollScreening() throws IOException {
        Subject subject = createSubjectWithRequireData("1");
        subjectService.createOrUpdateForZetes(subject);

        List<Enrollment> enrollmentList = enrollmentDataService.retrieveAll();
        assertEquals(1, enrollmentList.size());
    }

    @Test
    public void shouldEnrollBoostVaccinationForSpecificDay() throws IOException, SchedulerException {
        shouldEnrollBoostVaccinationForSpecificDayForStage("1");
        clearAll();
        shouldEnrollBoostVaccinationForSpecificDayForStage("2");
    }

    @Test(expected = EbodacEnrollmentException.class)
    public void shouldReenrollOnlyEnrolledCampaigns() {
        Subject subject = createSubjectWithRequireData("1");

        Visit visit = new Visit();
        visit.setDateProjected(new LocalDate(2115, 8, 17));
        visit.setType(VisitType.BOOST_VACCINATION_DAY);
        visit.setSubject(subject);

        ebodacEnrollmentService.reenrollSubject(visit);
    }

    @Test
    public void shouldCompleteCampaignWhenVisitAlreadyTookPlace() throws IOException, SchedulerException {
        shouldCompleteCampaignWhenVisitAlreadyTookPlaceForStage("1");
        clearAll();
        shouldCompleteCampaignWhenVisitAlreadyTookPlaceForStage("2");
    }

    @Test
    public void shouldCompleteCampaignWhenSubjectHaveDisconStdDate() throws IOException, SchedulerException {
        shouldCompleteCampaignWhenSubjectHaveDisconStdDateForStage("1");
        clearAll();
        shouldCompleteCampaignWhenSubjectHaveDisconStdDateForStage("2");
    }

    @Test
    public void shouldCompleteCampaignWhenSubjectHaveDisconVacDate() throws IOException, SchedulerException {
        List<String> campaignList = new ArrayList<>();
        campaignList.add("Boost Vaccination Second Follow-up visit");
        shouldCompleteCampaignWhenSubjectHaveDisconVacDateForStage("1", campaignList);
        clearAll();
        campaignList = new ArrayList<>();
        campaignList.add("Boost Vaccination Second Follow-up visit - stage 2");
        shouldCompleteCampaignWhenSubjectHaveDisconVacDateForStage("2", campaignList);

    }

    @Test
    public void shouldEnrollSubject() throws IOException, SchedulerException {
        shouldEnrollSubjectForStage("1");
        clearAll();
        shouldEnrollSubjectForStage("2");
    }

    @Test
    public void shouldEnrollSubjectToCampaign() throws IOException, SchedulerException {
        shouldEnrollSubjectToCampaignForStage("1");
        clearAll();
        shouldEnrollSubjectToCampaignForStage("2");
    }

    @Test
    public void shouldEnrollSubjectToCampaignWithNewDate() throws IOException, SchedulerException {
        shouldEnrollSubjectToCampaignWithNewDateForStage("1");
        clearAll();
        shouldEnrollSubjectToCampaignWithNewDateForStage("2");

    }

    @Test(expected = EbodacEnrollmentException.class)
    public void shouldNotEnrollSubjectWithEnrolledStatus() throws IOException {
        Subject subject = createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollBasic.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBasic.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());

        ebodacEnrollmentService.enrollSubject(subjectEnrollments.getSubject().getSubjectId());
    }

    @Test
    public void shouldUnenrollSubject() throws IOException, SchedulerException {
        shouldUnenrollSubjectForStage("1");
        clearAll();
        shouldUnenrollSubjectForStage("2");
    }

    @Test
    public void shouldUnenrollSubjectFromCampaign() throws IOException, SchedulerException {
        shouldUnenrollSubjectFromCampaignForStage("1");
        clearAll();
        shouldUnenrollSubjectFromCampaignForStage("2");
    }

    @Test(expected = EbodacEnrollmentException.class)
    public void shouldNotUnenrollSubjectWithUnenrolledStatus() throws IOException {
        Subject subject = createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        ebodacEnrollmentService.unenrollSubject(subject.getSubjectId());
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());
        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments.getStatus());

        ebodacEnrollmentService.unenrollSubject(subject.getSubjectId());
    }

    @Test
    public void shouldReenrollSubject() throws IOException, SchedulerException {
        shouldReenrollSubjectForStage("1");
        clearAll();
        shouldReenrollSubjectForStage("2");
    }

    @Test
    public void shouldReenrollSubjectToCampaignWithNewDate() throws IOException, SchedulerException {
        shouldReenrollSubjectToCampaignWithNewDateForStage("1");
        clearAll();
        shouldReenrollSubjectToCampaignWithNewDateForStage("2");
    }

    @Test
    public void shouldCorrectlySetSubjectEnrollmentStatus() throws IOException, SchedulerException {
        shouldCorrectlySetSubjectEnrollmentStatusForStage("1");
        clearAll();
        shouldCorrectlySetSubjectEnrollmentStatusForStage("2");

    }

    @Test
    public void shouldCheckIfSubjectIsEnrolled() throws IOException, SchedulerException {
        shouldCheckIfSubjectIsEnrolledForStage("1");
        clearAll();
        shouldCheckIfSubjectIsEnrolledForStage("2");
    }

    @Test
    public void shouldNotCreateJobsForDuplicatedEnrollments() throws IOException, SchedulerException {
        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";

        Subject subject1 = createSubjectWithRequireData("1");
        Subject subject2 = createSubjectWithRequireData("2");
        Subject subject3 = createSubjectWithRequireData("3");
        Subject subject4 = createSubjectWithRequireData("4");
        Subject subject5 = createSubjectWithRequireData("5");

        InputStream inputStream = getClass().getResourceAsStream("/enrollDuplicated.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollDuplicated.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments1 = subjectEnrollmentsDataService.findBySubjectId(subject1.getSubjectId());
        SubjectEnrollments subjectEnrollments2 = subjectEnrollmentsDataService.findBySubjectId(subject2.getSubjectId());
        SubjectEnrollments subjectEnrollments3 = subjectEnrollmentsDataService.findBySubjectId(subject3.getSubjectId());
        SubjectEnrollments subjectEnrollments4 = subjectEnrollmentsDataService.findBySubjectId(subject4.getSubjectId());
        SubjectEnrollments subjectEnrollments5 = subjectEnrollmentsDataService.findBySubjectId(subject5.getSubjectId());

        assertEquals(10, subjectEnrollments1.getEnrollments().size());
        assertEquals(10, subjectEnrollments2.getEnrollments().size());
        assertEquals(10, subjectEnrollments3.getEnrollments().size());
        assertEquals(10, subjectEnrollments4.getEnrollments().size());
        assertEquals(10, subjectEnrollments5.getEnrollments().size());

        for (Enrollment enrollment : subjectEnrollments1.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String triggerKeyString = campaignCompletedString + enrollment.getCampaignName() + "." + enrollment.getExternalId() + runonce;
            assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
            assertEquals(4, enrollment.getDuplicatedEnrollments().size());
        }

        for (Enrollment enrollment : subjectEnrollments2.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String triggerKeyString = campaignCompletedString + enrollment.getCampaignName() + "." + enrollment.getExternalId() + runonce;
            assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
        }

        for (Enrollment enrollment : subjectEnrollments3.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String triggerKeyString = campaignCompletedString + enrollment.getCampaignName() + "." + enrollment.getExternalId() + runonce;
            assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
        }

        for (Enrollment enrollment : subjectEnrollments4.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String triggerKeyString = campaignCompletedString + enrollment.getCampaignName() + "." + enrollment.getExternalId() + runonce;
            assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
        }

        for (Enrollment enrollment : subjectEnrollments5.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String triggerKeyString = campaignCompletedString + enrollment.getCampaignName() + "." + enrollment.getExternalId() + runonce;
            assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
        }
    }

    @Test
    public void shouldNotGroupEnrollmentsBetweenStages() throws IOException, SchedulerException {
        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";

        Subject subject1 = createSubjectWithRequireData("1");
        Subject subject2 = createSubjectWithRequireData("2");
        Subject subject3 = createSubjectWithRequireData("3");
        Subject subject4 = createSubjectWithRequireData("4");

        InputStream inputStream = getClass().getResourceAsStream("/enrollDuplicatedBetweenStages.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollDuplicatedBetweenStages.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments1 = subjectEnrollmentsDataService.findBySubjectId(subject1.getSubjectId());
        SubjectEnrollments subjectEnrollments2 = subjectEnrollmentsDataService.findBySubjectId(subject2.getSubjectId());
        SubjectEnrollments subjectEnrollments3 = subjectEnrollmentsDataService.findBySubjectId(subject3.getSubjectId());
        SubjectEnrollments subjectEnrollments4 = subjectEnrollmentsDataService.findBySubjectId(subject4.getSubjectId());

        assertEquals(9, subjectEnrollments1.getEnrollments().size());
        assertEquals(9, subjectEnrollments2.getEnrollments().size());
        assertEquals(9, subjectEnrollments3.getEnrollments().size());
        assertEquals(9, subjectEnrollments4.getEnrollments().size());

        for (Enrollment enrollment : subjectEnrollments1.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String triggerKeyString = campaignCompletedString + enrollment.getCampaignName() + "." + enrollment.getExternalId() + runonce;
            assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
            assertEquals(1, enrollment.getDuplicatedEnrollments().size());
        }

        for (Enrollment enrollment : subjectEnrollments2.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String triggerKeyString = campaignCompletedString + enrollment.getCampaignName() + "." + enrollment.getExternalId() + runonce;
            assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
        }

        for (Enrollment enrollment : subjectEnrollments3.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String triggerKeyString = campaignCompletedString + enrollment.getCampaignName() + "." + enrollment.getExternalId() + runonce;
            assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
            assertEquals(1, enrollment.getDuplicatedEnrollments().size());
        }

        for (Enrollment enrollment : subjectEnrollments4.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String triggerKeyString = campaignCompletedString + enrollment.getCampaignName() + "." + enrollment.getExternalId() + runonce;
            assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
        }
    }

    @Test
    public void shouldFindNewParentWhenOldUnenrolled() throws IOException, SchedulerException {
        shouldFindNewParentWhenOldUnenrolledForStage("1");
        clearAll();
        shouldFindNewParentWhenOldUnenrolledForStage("2");
    }

    @Test
    public void shouldUnenrollDuplicatedEnrollment() throws IOException, SchedulerException {
        shouldUnenrollDuplicatedEnrollmentForStage("1");
        clearAll();
        shouldUnenrollDuplicatedEnrollmentForStage("2");
    }

    @Test
    public void shouldUpdateGroupsWhenSubjectReenrolled() throws IOException, SchedulerException {
        shouldUpdateGroupsWhenSubjectReenrolledForStage("1");
        clearAll();
        shouldUpdateGroupsWhenSubjectReenrolledForStage("2");
    }

    @Test
    public void shouldEnrollWhenDataAreComingInTwoParts() throws IOException {
        Subject subject = createSubjectWithRequireData("1020000111");

        InputStream inputStream = getClass().getResourceAsStream("/enrollWhenDataCommingInTwoParts_part1.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollWhenDataCommingInTwoParts_part1.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());
        assertNull(subjectEnrollments);

        inputStream = getClass().getResourceAsStream("/enrollWhenDataCommingInTwoParts_part2.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollWhenDataCommingInTwoParts_part2.csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());
        assertEquals(10, subjectEnrollments.getEnrollments().size());

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }
    }

    @Test
    public void shouldEnrollPrimeVaccinationDayCampaignByActualVisitDate() throws IOException, SchedulerException {
        shouldEnrollPrimeVaccinationDayCampaignByActualVisitDateForStage("1");
        clearAll();
        shouldEnrollPrimeVaccinationDayCampaignByActualVisitDateForStage("2");
    }

    @Test(expected = EbodacEnrollmentException.class)
    public void shouldNotReenrollPrimeVaccinationDayCampaign() throws IOException {
        Subject subject = createSubjectWithRequireData("1");
       createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollPrimeVaccinationActualDate.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollPrimeVaccinationActualDate.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());
        assertEquals(4, subjectEnrollments.getEnrollments().size());

        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(VisitType.PRIME_VACCINATION_DAY.getValue());
        assertNotNull(enrollment);
        assertEquals(new LocalDate(2115, 10, 10), enrollment.getReferenceDate());
        assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());

        ebodacEnrollmentService.reenrollSubjectWithNewDate(subject.getSubjectId(),
                VisitType.PRIME_VACCINATION_DAY.getValue(), new LocalDate(2115, 11, 11));
    }

    @Test(expected = EbodacEnrollmentException.class)
    public void shouldNotEnrollPrimeVaccinationDayCampaignWithNewDate() throws IOException {
        Subject subject = createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollPrimeVaccinationActualDate.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollPrimeVaccinationActualDate.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());
        assertEquals(4, subjectEnrollments.getEnrollments().size());

        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(VisitType.PRIME_VACCINATION_DAY.getValue());
        assertNotNull(enrollment);
        assertEquals(new LocalDate(2115, 10, 10), enrollment.getReferenceDate());
        assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());

        ebodacEnrollmentService.unenrollSubject(subject.getSubjectId(), VisitType.PRIME_VACCINATION_DAY.getValue());

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());
        enrollment = subjectEnrollments.findEnrolmentByCampaignName(VisitType.PRIME_VACCINATION_DAY.getValue());
        assertNotNull(enrollment);
        assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());

        ebodacEnrollmentService.enrollSubjectToCampaignWithNewDate(subject.getSubjectId(),
                VisitType.PRIME_VACCINATION_DAY.getValue(), new LocalDate(2115, 11, 11));
    }

    @Test(expected = EbodacEnrollmentException.class)
    public void shouldNotReenrollPrimeVaccinationDayCampaignWhenVisitUpdated() throws IOException {
        Subject subject = createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollPrimeVaccinationActualDate.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollPrimeVaccinationActualDate.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());
        assertEquals(4, subjectEnrollments.getEnrollments().size());

        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(VisitType.PRIME_VACCINATION_DAY.getValue());
        assertNotNull(enrollment);
        assertEquals(new LocalDate(2115, 10, 10), enrollment.getReferenceDate());
        assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());

        Visit visit = visitService.findVisitBySubjectIdAndVisitType(subject.getSubjectId(), VisitType.PRIME_VACCINATION_DAY);
        visit.setMotechProjectedDate(new LocalDate(2115, 11, 11));

        ebodacEnrollmentService.reenrollSubject(visit);
    }

    @Test
    public void shouldNotEnrollWhenSubjectDoesNotHavePrimeVaccinationDate() throws IOException, SchedulerException {
        shouldNotEnrollWhenSubjectDoesNotHavePrimeVaccinationDateForStage("1");
        clearAll();
        shouldNotEnrollWhenSubjectDoesNotHavePrimeVaccinationDateForStage("2");
    }

    @Test
    public void shouldEnrollWhenSubjectPrimeVaccinationDateIsAddedFromRave() throws IOException, SchedulerException {
        shouldEnrollWhenSubjectPrimeVaccinationDateIsAddedFromRaveForStage("1");
        clearAll();
        shouldEnrollWhenSubjectPrimeVaccinationDateIsAddedFromRaveForStage("2");

    }

    @Test
    public void shouldRollbackEnrolledWhenWithdrawalDateRemoved() throws IOException, SchedulerException {
        shouldRollbackEnrolledWhenWithdrawalDateRemovedForStage("1");
        clearAll();
        shouldRollbackEnrolledWhenWithdrawalDateRemovedForStage("2");

    }

    @Test
    public void shouldRollbackUnenrolledWhenWithdrawalDateRemoved() throws IOException, SchedulerException {
        shouldRollbackUnenrolledWhenWithdrawalDateRemovedForStage("1");
        clearAll();
        shouldRollbackUnenrolledWhenWithdrawalDateRemovedForStage("2");

    }

    @Test
    public void shouldRollbackEnrolledWhenBoostDiscontinuationDateRemoved() throws IOException, SchedulerException {
        List<String> campaignList = new ArrayList<>();
        campaignList.add("Booster related messages");
        campaignList.add("Boost Vaccination Day");
        shouldRollbackEnrolledWhenBoostDiscontinuationDateRemovedForStage("1", campaignList);
        clearAll();
        campaignList = new ArrayList<>();
        campaignList.add("Booster related messages - stage 2");
        campaignList.add("Boost Vaccination Day - stage 2");
        shouldRollbackEnrolledWhenBoostDiscontinuationDateRemovedForStage("2", campaignList);
    }

    @Test
    public void shouldRollbackUnenrolledWhenBoostDiscontinuationDateRemoved() throws IOException, SchedulerException {
        List<String> campaignList = new ArrayList<>();
        campaignList.add("Booster related messages");
        campaignList.add("Boost Vaccination Day");
        shouldRollbackUnenrolledWhenBoostDiscontinuationDateRemovedForStage("1", campaignList);
        clearAll();
        campaignList = new ArrayList<>();
        campaignList.add("Booster related messages - stage 2");
        campaignList.add("Boost Vaccination Day - stage 2");
        shouldRollbackUnenrolledWhenBoostDiscontinuationDateRemovedForStage("2", campaignList);
    }

    @Test
    public void shouldNotReenrollVisitAndNotOverrideMotechPlannedDateWhenRavePlannedDateWasNotChanged() throws IOException, SchedulerException {
        shouldNotReenrollVisitAndNotOverrideMotechPlannedDateWhenRavePlannedDateWasNotChangedForStage("1");
        clearAll();
        shouldNotReenrollVisitAndNotOverrideMotechPlannedDateWhenRavePlannedDateWasNotChangedForStage("2");
    }

    @Test
    public void shouldUnenrollAndDeleteEnrollmentsWhenPlannedDateRemoved() throws IOException, SchedulerException {
        shouldUnenrollAndDeleteEnrollmentsWhenPlannedDateRemovedForStage("1");
        clearAll();
        shouldUnenrollAndDeleteEnrollmentsWhenPlannedDateRemovedForStage("2");
    }

    @Test
    public void shouldUnenrollAndDeleteEnrollmentsWhenPrimeVaccinationDateRemoved() throws IOException, SchedulerException {
        shouldUnenrollAndDeleteEnrollmentsWhenPrimeVaccinationDateRemovedForStage("1");
        clearAll();
        shouldUnenrollAndDeleteEnrollmentsWhenPrimeVaccinationDateRemovedForStage("2");
    }

    @Test
    public void shouldUnenrollAndDeleteBoosterRelatedEnrollmentsWhenBoosterVaccinationDateRemoved() throws IOException, SchedulerException {
        List<String> boosterRelatedMessages = Arrays.asList("Boost Vaccination First Follow-up visit",
                "Boost Vaccination Second Follow-up visit", "Boost Vaccination Third Follow-up visit");
        shouldUnenrollAndDeleteBoosterRelatedEnrollmentsWhenBoosterVaccinationDateRemovedForStage("1", boosterRelatedMessages);
        boosterRelatedMessages = Arrays.asList("Boost Vaccination First Follow-up visit - stage 2",
                "Boost Vaccination Second Follow-up visit - stage 2", "Boost Vaccination Third Follow-up visit - stage 2");
        clearAll();
        shouldUnenrollAndDeleteBoosterRelatedEnrollmentsWhenBoosterVaccinationDateRemovedForStage("2", boosterRelatedMessages);
    }

    @Test
    public void shouldRollbackWhenActualDateRemoved() throws IOException, SchedulerException {
        shouldRollbackWhenActualDateRemovedForStage("1");
        clearAll();
        shouldRollbackWhenActualDateRemovedForStage("2");
    }

    @Test
    public void shouldRollbackUnenrolledWhenActualDateRemoved() throws IOException, SchedulerException {
        shouldRollbackUnenrolledWhenActualDateRemovedForStage("1");
        clearAll();
        shouldRollbackUnenrolledWhenActualDateRemovedForStage("2");
    }

    @Test
    public void shouldCreateVisitsAndEnrollWithActiveStageIdWhenSubjectStageIdIsNull() throws IOException {
        Config config = new Config();
        config.setActiveStageId(2L);
        configService.updateConfig(config);

        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimpleWithEmptyStage.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimpleWithEmptyStage.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findBySubjectId(subject.getSubjectId());
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String [] nameParts = enrollment.getCampaignName().split(EbodacConstants.STAGE);
            assertEquals(2, nameParts.length);
            assertEquals("2", nameParts[1]);
        }

        assertEquals(3, visitDataService.retrieveAll().size());

        config = new Config();
        config.setActiveStageId(2L);
        configService.updateConfig(config);
    }

    @Test
    public void shouldCreateVisitsAndEnrollForAllStagesWhenActiveStageIdIsNull() throws IOException {
        Config config = new Config();
        config.setActiveStageId(null);
        configService.updateConfig(config);

        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findBySubjectId("2");
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String [] nameParts = enrollment.getCampaignName().split(EbodacConstants.STAGE);
            assertEquals(2, nameParts.length);
            assertEquals("2", nameParts[1]);
        }

        enrollmentList = enrollmentDataService.findBySubjectId("1");
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String [] nameParts = enrollment.getCampaignName().split(EbodacConstants.STAGE);
            assertEquals(1, nameParts.length);
        }

        assertEquals(3, visitDataService.findBySubjectId("2").size());
        assertEquals(3, visitDataService.findBySubjectId("1").size());

        config = new Config();
        config.setActiveStageId(2L);
        configService.updateConfig(config);
    }

    @Test
    public void shouldNotCreateVisitsAndNotEnrollWhenActiveStageIdAndSubjectStageIdAreNotNullAndAreDifferent() throws IOException {
        Config config = new Config();
        config.setActiveStageId(2L);
        configService.updateConfig(config);

        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findBySubjectId("2");
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String [] nameParts = enrollment.getCampaignName().split(EbodacConstants.STAGE);
            assertEquals(2, nameParts.length);
            assertEquals("2", nameParts[1]);
        }

        enrollmentList = enrollmentDataService.findBySubjectId("1");
        assertEquals(0, enrollmentList.size());

        assertEquals(3, visitDataService.findBySubjectId("2").size());
        assertEquals(0, visitDataService.findBySubjectId("1").size());

        config = new Config();
        config.setActiveStageId(2L);
        configService.updateConfig(config);
    }

    @Test
    public void shouldNotCreateVisitsAndNotEnrollWhenActiveStageIdAndSubjectStageIdAreNull() throws IOException {
        Config config = new Config();
        config.setActiveStageId(null);
        configService.updateConfig(config);

        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimpleWithEmptyStage.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimpleWithEmptyStage.csv");
        inputStream.close();

        assertEquals(0, visitDataService.findBySubjectId("1").size());
        assertNull(subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId()));
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

    private Subject createSubjectWithRequireData(String subjectId) {
        Subject subject = new Subject();
        subject.setSubjectId(subjectId);
        subject.setLanguage(Language.English);
        subject.setPhoneNumber("123456789");
        subject.setSiteId("asdas");
        subject.setSiteName("siteName");
        subjectService.create(subject);
        return subject;
    }

    private void clearAll() throws SchedulerException {
        clearJobs();
        ivrAndSmsStatisticReportDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        enrollmentDataService.deleteAll();
        subjectService.deleteAll();
        configService.updateConfig(savedConfig);
    }

    public void shouldEnrollAllVisitTypesForStage(String stage) throws IOException, SchedulerException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollBasic.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBasic.csv");
        inputStream.close();

        Subject subject = subjectService.findSubjectBySubjectId(stage);
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertEquals(10, subjectEnrollments.getEnrollments().size());

        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            String triggerKeyString = campaignCompletedString + enrollment.getCampaignName() + "." + stage + runonce;
            assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
        }

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            assertEquals(new LocalDate(2115, 9, 10), enrollment.getReferenceDate());
        }
    }

    private void shouldEnrollWhenGetZetesDataAfterRaveForStage(String stage) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        Subject subject = subjectService.findSubjectBySubjectId(stage);
        List<Enrollment> enrollmentList = enrollmentDataService.retrieveAll();
        assertEquals(0, enrollmentList.size());

        subject.setPhoneNumber("123456789");
        subject.setLanguage(Language.English);
        subjectService.createOrUpdateForZetes(subject);

        subject = subjectService.findSubjectBySubjectId(stage);
        enrollmentList = enrollmentDataService.retrieveAll();
        assertEquals(3, enrollmentList.size());

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }
    }

    private void shouldReenrollWhenRaveProjectedDateWasChangedForStage(String stage) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        inputStream = getClass().getResourceAsStream("/enrollChangeProjectedDate.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollChangeProjectedDate.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(new LocalDate(2115, 10, 11), enrollment.getReferenceDate());
        }

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(3, subjectEnrollments.getEnrollments().size());

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(new LocalDate(2115, 10, 11), enrollment.getReferenceDate());
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }
    }

  private void shouldNotEnrollWhenSubjectDoesntHaveLanguageOrPhoneNumberForStage(String stage) throws IOException {
        Subject subject = new Subject();
        subject.setSubjectId("1");
        subject.setSiteName("siteName");
        subjectService.create(subject);

        subject = new Subject();
        subject.setSubjectId("2");
        subject.setSiteName("siteName");
        subjectService.create(subject);

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        subject = subjectService.findSubjectBySubjectId(stage);

        subject.setPhoneNumber("123456789");
        subject.setLanguage(null);
        subjectService.createOrUpdateForZetes(subject);

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertNull(subjectEnrollments);

        subject.setLanguage(Language.English);
        subject.setPhoneNumber(null);
        subjectService.createOrUpdateForZetes(subject);

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertNull(subjectEnrollments);
    }

    private void shouldEnrollBoostVaccinationForSpecificDayForStage(String stage) throws IOException {
        for (int i = 0; i < 7; i++) {
            createSubjectWithRequireData("1" + Integer.toString(i + 1));
            createSubjectWithRequireData("2" + Integer.toString(i + 1));
        }

        InputStream inputStream = getClass().getResourceAsStream("/enrollBoostVaccination.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBoostVaccination.csv");
        inputStream.close();
        String stageString = "2".equals(stage) ? " - stage 2" : "";

        int i = 0;
        for (Subject subject : subjectService.findByStageId(Long.parseLong(stage))) {

            SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());
            assertEquals(1, subjectEnrollments.getEnrollments().size());
            Enrollment enrollment = subjectEnrollments.getEnrollments().iterator().next();
            assertEquals(VisitType.BOOST_VACCINATION_DAY.getValue() + " " + DAYS_OF_WEEK.get(i) + stageString, enrollment.getCampaignName());
            assertEquals(new LocalDate(2115, 9, 22 + i), subjectEnrollments.getEnrollments().iterator().next().getReferenceDate());
            i++;
        }
    }

    private void shouldCompleteCampaignWhenVisitAlreadyTookPlaceForStage(String stage) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findBySubjectId(stage);
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }

        inputStream = getClass().getResourceAsStream("/enrollActualDateStage" + stage + ".csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollActualDateStage" + stage + ".csv");
        inputStream.close();

        enrollmentList = enrollmentDataService.findBySubjectId(stage);

        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.COMPLETED, enrollment.getStatus());
        }
    }

    private void shouldCompleteCampaignWhenSubjectHaveDisconStdDateForStage(String stage) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findBySubjectId(stage);
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }

        Subject subject = subjectService.findSubjectBySubjectId(stage);
        subject.setDateOfDisconStd(new LocalDate(2115, 8, 8));
        subjectService.createOrUpdateForRave(subject);

        enrollmentList = enrollmentDataService.findBySubjectId(stage);
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.WITHDRAWN_FROM_STUDY, enrollment.getStatus());
        }
    }

    private void shouldCompleteCampaignWhenSubjectHaveDisconVacDateForStage(String stage, List<String> campaignList) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findBySubjectId(stage);
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }

        Config config = new Config();
        config.setDisconVacCampaignsList(campaignList);
        configService.updateConfig(config);

        Subject subject = subjectService.findSubjectBySubjectId(stage);
        subject.setDateOfDisconVac(new LocalDate(2115, 8, 8));
        subjectService.createOrUpdateForRave(subject);

        enrollmentList = enrollmentDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(0).getStatus());
        assertEquals(EnrollmentStatus.UNENROLLED_FROM_BOOSTER, enrollmentList.get(1).getStatus());
        assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(2).getStatus());
    }

    private void shouldEnrollSubjectForStage(String stage) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        ebodacEnrollmentService.unenrollSubject(subjectEnrollments.getSubject().getSubjectId());

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
        }

        ebodacEnrollmentService.enrollSubject(stage);

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(3, subjectEnrollments.getEnrollments().size());

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }
    }

    private void shouldEnrollSubjectToCampaignForStage(String stage) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        ebodacEnrollmentService.unenrollSubject(stage);

        List<Enrollment> enrollmentList = enrollmentDataService.findBySubjectId(stage);
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
        }
        ebodacEnrollmentService.enrollSubjectToCampaign(stage, VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue());

        enrollmentList = enrollmentDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.UNENROLLED, enrollmentList.get(0).getStatus());
        assertEquals(EnrollmentStatus.UNENROLLED, enrollmentList.get(1).getStatus());
        assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(2).getStatus());
    }

    private void shouldEnrollSubjectToCampaignWithNewDateForStage(String stage) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        ebodacEnrollmentService.unenrollSubject(stage);

        List<Enrollment> enrollmentList = enrollmentDataService.findBySubjectId(stage);
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
            assertEquals(new LocalDate(2115, 10, 10), enrollment.getReferenceDate());
        }
        ebodacEnrollmentService.enrollSubjectToCampaignWithNewDate(stage, VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue(), new LocalDate(2115, 12, 1));

        enrollmentList = enrollmentDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(2).getStatus());
        assertEquals(new LocalDate(2115, 12, 1), enrollmentList.get(2).getReferenceDate());
    }

    private void shouldUnenrollSubjectForStage(String stage) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollBasic.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBasic.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findBySubjectId(stage);
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }

        ebodacEnrollmentService.unenrollSubject(stage);

        enrollmentList = enrollmentDataService.findBySubjectId(stage);
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
        }
    }

    private void shouldUnenrollSubjectFromCampaignForStage(String stage) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findBySubjectId(stage);
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }

        ebodacEnrollmentService.unenrollSubject(stage, VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue());

        enrollmentList = enrollmentDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(0).getStatus());
        assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(1).getStatus());
        assertEquals(EnrollmentStatus.UNENROLLED, enrollmentList.get(2).getStatus());

    }

    private void shouldReenrollSubjectForStage(String stage) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findBySubjectId(stage);
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(new LocalDate(2115, 10, 10), enrollment.getReferenceDate());
        }

        Subject subject = subjectService.findSubjectBySubjectId(stage);
        for (Visit visit : subject.getVisits()) {
            visit.setMotechProjectedDate(new LocalDate(2115, 10, 11));
            ebodacEnrollmentService.reenrollSubject(visit);
        }
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(new LocalDate(2115, 10, 11), enrollment.getReferenceDate());
        }
    }

    private void shouldReenrollSubjectToCampaignWithNewDateForStage(String stage) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findBySubjectId(stage);
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(new LocalDate(2115, 10, 10), enrollment.getReferenceDate());
        }

        ebodacEnrollmentService.reenrollSubjectWithNewDate(stage, VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue(), new LocalDate(2115, 12, 12));
        enrollmentList = enrollmentDataService.findBySubjectId(stage);

        assertEquals(new LocalDate(2115, 10, 10), enrollmentList.get(0).getReferenceDate());
        assertEquals(new LocalDate(2115, 10, 10), enrollmentList.get(1).getReferenceDate());
        assertEquals(new LocalDate(2115, 12, 12), enrollmentList.get(2).getReferenceDate());
    }

    private void shouldCorrectlySetSubjectEnrollmentStatusForStage(String stage) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());

        ebodacEnrollmentService.unenrollSubject(stage, subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue()).getCampaignName());
        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());

        ebodacEnrollmentService.unenrollSubject(stage, subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_SECOND_FOLLOW_UP_VISIT.getValue()).getCampaignName());
        ebodacEnrollmentService.unenrollSubject(stage, subjectEnrollments.findEnrolmentByCampaignName("Boost Vaccination Day Thursday").getCampaignName());

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments.getStatus());

        ebodacEnrollmentService.enrollSubject(subjectEnrollments.getSubject().getSubjectId());

        Subject subject = subjectService.findSubjectBySubjectId(stage);
        Visit visit = subject.getVisits().get(0);
        visit.setDate(new LocalDate(2115, 8, 8));
        visitService.createOrUpdate(visit);

        subject = subjectService.findSubjectBySubjectId(stage);
        subject.setDateOfDisconStd(new LocalDate(2115, 8, 8));
        subjectService.createOrUpdateForRave(subject);
        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertEquals(EnrollmentStatus.WITHDRAWN_FROM_STUDY, subjectEnrollments.getStatus());
    }

    private void shouldCheckIfSubjectIsEnrolledForStage(String stage) throws IOException {
        Subject subject = createSubjectWithRequireData(stage);
        subject.setPrimerVaccinationDate(new LocalDate(2115, 10, 11));
        subject.setStageId(1L);
        subjectService.update(subject);

        Visit visit = new Visit();
        visit.setType(VisitType.BOOST_VACCINATION_DAY);
        visit.setMotechProjectedDate(new LocalDate(2115, 10, 10));
        visit.setSubject(subject);
        assertFalse(ebodacEnrollmentService.checkIfEnrolledAndUpdateEnrollment(visit));

        visitService.create(visit);
        subject = subjectService.findSubjectBySubjectId(stage);
        ebodacEnrollmentService.enrollSubject(subject);

        assertTrue(ebodacEnrollmentService.checkIfEnrolledAndUpdateEnrollment(visit));

        ebodacEnrollmentService.unenrollSubject(subject.getSubjectId());
        assertFalse(ebodacEnrollmentService.checkIfEnrolledAndUpdateEnrollment(visit));
    }

    private void shouldEnrollPrimeVaccinationDayCampaignByActualVisitDateForStage(String stage) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollPrimeVaccinationPlannedDate.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollPrimeVaccinationPlannedDate.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());
        assertEquals(2, subjectEnrollments.getEnrollments().size());

        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(VisitType.PRIME_VACCINATION_DAY.getValue());
        assertNull(enrollment);

        inputStream = getClass().getResourceAsStream("/enrollPrimeVaccinationActualDate.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollPrimeVaccinationActualDate.csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());
        assertEquals(4, subjectEnrollments.getEnrollments().size());

        enrollment = subjectEnrollments.findEnrolmentByCampaignName(VisitType.PRIME_VACCINATION_DAY.getValue());
        assertNotNull(enrollment);
        assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
    }


    private void shouldNotEnrollWhenSubjectDoesNotHavePrimeVaccinationDateForStage(String stage) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollNoPrimeVaccinationDate.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollNoPrimeVaccinationDate.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertNull(subjectEnrollments);
    }

    private void shouldEnrollWhenSubjectPrimeVaccinationDateIsAddedFromRaveForStage(String stage) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollNoPrimeVaccinationDate.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollNoPrimeVaccinationDate.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertNull(subjectEnrollments);

        inputStream = getClass().getResourceAsStream("/enrollPrimeVaccinationActualDate.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollPrimeVaccinationActualDate.csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());
        assertEquals(4, subjectEnrollments.getEnrollments().size());

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }
    }

    private void shouldRollbackEnrolledWhenWithdrawalDateRemovedForStage(String stage) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }

        inputStream = getClass().getResourceAsStream("/enrollWithdrawn.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollWithdrawn.csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.WITHDRAWN_FROM_STUDY, subjectEnrollments.getStatus());
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.WITHDRAWN_FROM_STUDY, enrollment.getStatus());
        }

        inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }
    }

    private void shouldRollbackUnenrolledWhenWithdrawalDateRemovedForStage(String stage) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        ebodacEnrollmentService.unenrollSubject(stage);

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments.getStatus());
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
        }

        inputStream = getClass().getResourceAsStream("/enrollWithdrawn.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollWithdrawn.csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.WITHDRAWN_FROM_STUDY, subjectEnrollments.getStatus());
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.WITHDRAWN_FROM_STUDY, enrollment.getStatus());
        }

        inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments.getStatus());
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
        }
    }

    private void shouldRollbackEnrolledWhenBoostDiscontinuationDateRemovedForStage(String stage, List<String> campaignList) throws IOException {
        Config config = new Config();
        config.setDisconVacCampaignsList(campaignList);
        configService.updateConfig(config);

        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }

        inputStream = getClass().getResourceAsStream("/enrollBoosterDiscontinuation.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBoosterDiscontinuation.csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());
        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_DAY.getValue());
        assertEquals(EnrollmentStatus.UNENROLLED_FROM_BOOSTER, enrollment.getStatus());

        inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());
        enrollment = subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_DAY.getValue());
        assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
    }

    private void shouldRollbackUnenrolledWhenBoostDiscontinuationDateRemovedForStage(String stage, List<String> campaignList) throws IOException {
        Config config = new Config();
        config.setDisconVacCampaignsList(campaignList);
        configService.updateConfig(config);

        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        ebodacEnrollmentService.unenrollSubject(stage);

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments.getStatus());
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
        }

        inputStream = getClass().getResourceAsStream("/enrollBoosterDiscontinuation.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBoosterDiscontinuation.csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments.getStatus());
        Enrollment enrollment = subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_DAY.getValue());
        assertEquals(EnrollmentStatus.UNENROLLED_FROM_BOOSTER, enrollment.getStatus());

        inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments.getStatus());
        enrollment = subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_DAY.getValue());
        assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
    }

    private void shouldNotReenrollVisitAndNotOverrideMotechPlannedDateWhenRavePlannedDateWasNotChangedForStage(String stage) throws IOException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findBySubjectId(stage);
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(new LocalDate(2115, 10, 10), enrollment.getReferenceDate());
        }

        Subject subject = subjectService.findSubjectBySubjectId(stage);
        for (Visit visit : subject.getVisits()) {
            assertEquals(new LocalDate(2115, 10, 10), visit.getMotechProjectedDate());
            visit.setMotechProjectedDate(new LocalDate(2115, 10, 11));
            visitService.update(visit);
            ebodacEnrollmentService.reenrollSubject(visit);
        }

        subject = subjectService.findSubjectBySubjectId(stage);
        for (Visit visit : subject.getVisits()) {
            assertEquals(new LocalDate(2115, 10, 11), visit.getMotechProjectedDate());
        }

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(new LocalDate(2115, 10, 11), enrollment.getReferenceDate());
        }

        inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        subject = subjectService.findSubjectBySubjectId(stage);
        for (Visit visit : subject.getVisits()) {
            assertEquals(new LocalDate(2115, 10, 11), visit.getMotechProjectedDate());
        }

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(stage);
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(new LocalDate(2115, 10, 11), enrollment.getReferenceDate());
        }
    }

    private void shouldUnenrollAndDeleteEnrollmentsWhenPlannedDateRemovedForStage(String stage) throws IOException, SchedulerException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollBasic.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBasic.csv");
        inputStream.close();

        Subject subject = subjectService.findSubjectBySubjectId(stage);
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertEquals(10, subjectEnrollments.getEnrollments().size());

        List<String> campaignNamesList = new ArrayList<>();

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            assertEquals(new LocalDate(2115, 9, 10), enrollment.getReferenceDate());
            campaignNamesList.add(enrollment.getCampaignName());
        }

        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";
        for (String campaignName : campaignNamesList) {
            String triggerKeyString = campaignCompletedString + campaignName + "." + stage + runonce;
            assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
        }

        inputStream = getClass().getResourceAsStream("/enrollBasicWithoutPlannedDates.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBasicWithoutPlannedDates.csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertNull(subjectEnrollments);

        for (String campaignName : campaignNamesList) {
            String triggerKeyString = campaignCompletedString + campaignName + "." + stage + runonce;
            assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
        }
    }

    private void shouldUnenrollAndDeleteEnrollmentsWhenPrimeVaccinationDateRemovedForStage(String stage) throws IOException, SchedulerException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollBasic.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBasic.csv");
        inputStream.close();

        Subject subject = subjectService.findSubjectBySubjectId(stage);
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertEquals(10, subjectEnrollments.getEnrollments().size());

        List<String> campaignNamesList = new ArrayList<>();

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            assertEquals(new LocalDate(2115, 9, 10), enrollment.getReferenceDate());
            campaignNamesList.add(enrollment.getCampaignName());
        }

        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";
        for (String campaignName : campaignNamesList) {
            String triggerKeyString = campaignCompletedString + campaignName + "." + stage + runonce;
            assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
        }

        inputStream = getClass().getResourceAsStream("/enrollNoPrimeVaccinationDate.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollNoPrimeVaccinationDate.csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertNull(subjectEnrollments);

        for (String campaignName : campaignNamesList) {
            String triggerKeyString = campaignCompletedString + campaignName + "." + stage + runonce;
            assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
        }
    }

    private void shouldUnenrollAndDeleteBoosterRelatedEnrollmentsWhenBoosterVaccinationDateRemovedForStage(String stage, List<String> boosterRelatedMessages) throws IOException {

        Config config = new Config();
        config.setBoosterRelatedMessages(boosterRelatedMessages);
        configService.updateConfig(config);

        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollBasic.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBasic.csv");
        inputStream.close();

        Subject subject = subjectService.findSubjectBySubjectId(stage);
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertEquals(10, subjectEnrollments.getEnrollments().size());

        for (String campaignName : boosterRelatedMessages) {
            assertNotNull(subjectEnrollments.findEnrolmentByCampaignName(campaignName));
        }

        inputStream = getClass().getResourceAsStream("/enrollBasicWithoutBoosterVaccinationDate.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBasicWithoutBoosterVaccinationDate.csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertEquals(7, subjectEnrollments.getEnrollments().size());

        for (String campaignName : boosterRelatedMessages) {
            assertNull(subjectEnrollments.findEnrolmentByCampaignName(campaignName));
        }
    }

    public void shouldFindNewParentWhenOldUnenrolledForStage(String stage) throws IOException, SchedulerException {
        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";

        Subject subject1 = createSubjectWithRequireData(("1".equals(stage) ? "" : "2") + "1");
        Subject subject2 = createSubjectWithRequireData(("1".equals(stage) ? "" : "2") + "2");
        Subject subject3 = createSubjectWithRequireData(("1".equals(stage) ? "" : "2") + "3");

        InputStream inputStream = getClass().getResourceAsStream("/enrollDuplicatedSimpleStage" + stage + ".csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollDuplicatedSimpleStage" + stage + ".csv");
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

        String triggerKeyString1 = campaignCompletedString + enrollment1.getCampaignName() + "." + enrollment1.getExternalId() + runonce;
        String triggerKeyString2 = campaignCompletedString + enrollment2.getCampaignName() + "." + enrollment2.getExternalId() + runonce;
        String triggerKeyString3 = campaignCompletedString + enrollment3.getCampaignName() + "." + enrollment3.getExternalId() + runonce;

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));
        assertEquals(2, enrollment1.getDuplicatedEnrollments().size());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(enrollment1, enrollment2.getParentEnrollment());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment3.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
        assertEquals(enrollment1, enrollment3.getParentEnrollment());

        ebodacEnrollmentService.unenrollSubject(subject1.getSubjectId());

        subjectEnrollments1 = subjectEnrollmentsDataService.findBySubjectId(subject1.getSubjectId());
        subjectEnrollments2 = subjectEnrollmentsDataService.findBySubjectId(subject2.getSubjectId());
        subjectEnrollments3 = subjectEnrollmentsDataService.findBySubjectId(subject3.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());
        assertEquals(1, subjectEnrollments3.getEnrollments().size());

        enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();
        enrollment3 = subjectEnrollments3.getEnrollments().iterator().next();

        assertEquals(EnrollmentStatus.UNENROLLED, enrollment1.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));
        assertEquals(0, enrollment1.getDuplicatedEnrollments().size());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertEquals(EnrollmentStatus.ENROLLED, enrollment3.getStatus());

        if (enrollment2.getParentEnrollment() == null) {
            assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
            assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
            assertEquals(enrollment2, enrollment3.getParentEnrollment());
        } else if (enrollment3.getParentEnrollment() == null) {
            assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
            assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
            assertEquals(enrollment3, enrollment2.getParentEnrollment());
        } else {
            fail();
        }
    }

    public void shouldUnenrollDuplicatedEnrollmentForStage(String stage) throws IOException, SchedulerException {
        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";

        Subject subject1 = createSubjectWithRequireData(("1".equals(stage) ? "" : "2") + "1");
        Subject subject2 = createSubjectWithRequireData(("1".equals(stage) ? "" : "2") + "2");
        Subject subject3 = createSubjectWithRequireData(("1".equals(stage) ? "" : "2") + "3");

        InputStream inputStream = getClass().getResourceAsStream("/enrollDuplicatedSimpleStage" + stage + ".csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollDuplicatedSimpleStage" + stage + ".csv");
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

        String triggerKeyString1 = campaignCompletedString + enrollment1.getCampaignName() + "." + enrollment1.getExternalId() + runonce;
        String triggerKeyString2 = campaignCompletedString + enrollment2.getCampaignName() + "." + enrollment2.getExternalId() + runonce;
        String triggerKeyString3 = campaignCompletedString + enrollment3.getCampaignName() + "." + enrollment3.getExternalId() + runonce;

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));
        assertEquals(2, enrollment1.getDuplicatedEnrollments().size());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(enrollment1, enrollment2.getParentEnrollment());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment3.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
        assertEquals(enrollment1, enrollment3.getParentEnrollment());

        ebodacEnrollmentService.unenrollSubject(subject3.getSubjectId());

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
        assertEquals(enrollment1, enrollment2.getParentEnrollment());

        assertEquals(EnrollmentStatus.UNENROLLED, enrollment3.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
    }

    public void shouldUpdateGroupsWhenSubjectReenrolledForStage(String stage) throws IOException, SchedulerException {
        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";

        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");
        createSubjectWithRequireData("3");
        createSubjectWithRequireData("21");
        createSubjectWithRequireData("22");
        createSubjectWithRequireData("23");

        InputStream inputStream = getClass().getResourceAsStream("/enrollDuplicatedSimpleStage" + stage + ".csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollDuplicatedSimpleStage" + stage + ".csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments1 = subjectEnrollmentsDataService.findBySubjectId(("1".equals(stage) ? "" : "2") + "1");
        SubjectEnrollments subjectEnrollments2 = subjectEnrollmentsDataService.findBySubjectId(("1".equals(stage) ? "" : "2") + "2");

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());

        Enrollment enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        Enrollment enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();

        String triggerKeyString1 = campaignCompletedString + enrollment1.getCampaignName() + "." + enrollment1.getExternalId() + runonce;
        String triggerKeyString2 = campaignCompletedString + enrollment2.getCampaignName() + "." + enrollment2.getExternalId() + runonce;

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));
        assertEquals(2, enrollment1.getDuplicatedEnrollments().size());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(enrollment1, enrollment2.getParentEnrollment());

        ebodacEnrollmentService.reenrollSubjectWithNewDate(enrollment2.getExternalId(), enrollment2.getCampaignName(), new LocalDate(2115, 9, 22));

        subjectEnrollments1 = subjectEnrollmentsDataService.findBySubjectId(("1".equals(stage) ? "" : "2") + "1");
        subjectEnrollments2 = subjectEnrollmentsDataService.findBySubjectId(("1".equals(stage) ? "" : "2") + "2");

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

    public void shouldRollbackWhenActualDateRemovedForStage(String stage) throws IOException, SchedulerException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        Subject subject = subjectService.findSubjectBySubjectId(stage);
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertEquals(3, subjectEnrollments.getEnrollments().size());

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }

        ebodacEnrollmentService.unenrollSubject(subject.getSubjectId(), VisitType.BOOST_VACCINATION_SECOND_FOLLOW_UP_VISIT.getValue());

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertEquals(3, subjectEnrollments.getEnrollments().size());

        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_DAY.getValue()).getStatus());
        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_SECOND_FOLLOW_UP_VISIT.getValue()).getStatus());
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue()).getStatus());

        inputStream = getClass().getResourceAsStream("/enrollActualDateStage" + stage + ".csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollActualDateStage" + stage + ".csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertEquals(3, subjectEnrollments.getEnrollments().size());

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.COMPLETED, enrollment.getStatus());
        }

        inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertEquals(3, subjectEnrollments.getEnrollments().size());

        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_DAY.getValue()).getStatus());
        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_SECOND_FOLLOW_UP_VISIT.getValue()).getStatus());
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue()).getStatus());
    }

    public void shouldRollbackUnenrolledWhenActualDateRemovedForStage(String stage) throws IOException, SchedulerException {
        createSubjectWithRequireData("1");
        createSubjectWithRequireData("2");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        Subject subject = subjectService.findSubjectBySubjectId(stage);
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertEquals(3, subjectEnrollments.getEnrollments().size());

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }

        inputStream = getClass().getResourceAsStream("/enrollSimpleWithActualDateStage" + stage + ".csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimpleWithActualDateStage" + stage + ".csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertEquals(3, subjectEnrollments.getEnrollments().size());

        assertEquals(EnrollmentStatus.COMPLETED, subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_DAY.getValue()).getStatus());
        assertEquals(EnrollmentStatus.COMPLETED, subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_SECOND_FOLLOW_UP_VISIT.getValue()).getStatus());
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue()).getStatus());

        ebodacEnrollmentService.unenrollSubject(subject.getSubjectId());

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertEquals(3, subjectEnrollments.getEnrollments().size());

        assertEquals(EnrollmentStatus.COMPLETED, subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_DAY.getValue()).getStatus());
        assertEquals(EnrollmentStatus.COMPLETED, subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_SECOND_FOLLOW_UP_VISIT.getValue()).getStatus());
        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue()).getStatus());

        inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findBySubjectId(subject.getSubjectId());

        assertEquals(3, subjectEnrollments.getEnrollments().size());

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
        }
    }
}
