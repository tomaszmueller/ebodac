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
import org.motechproject.ebodac.domain.EnrollmentStatus;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.SubjectEnrollments;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.exception.EbodacEnrollmentException;
import org.motechproject.ebodac.repository.EnrollmentDataService;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.service.RaveImportService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.ebodac.service.VisitService;
import org.motechproject.messagecampaign.service.MessageCampaignService;
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
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class EbodacEnrollmentServiceIT extends BasePaxIT {

    @Inject
    private BundleContext bundleContext;

    @Inject
    private ConfigService configService;

    @Inject
    EbodacEnrollmentService ebodacEnrollmentService;

    @Inject
    SubjectService subjectService;

    @Inject
    VisitService visitService;

    @Inject
    MessageCampaignService messageCampaignService;

    @Inject
    EnrollmentDataService enrollmentDataService;

    @Inject
    SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    @Inject
    RaveImportService raveImportService;

    Scheduler scheduler;

    private Config savedConfig;

    @Before
    public void cleanBefore() throws SchedulerException {
        scheduler = (Scheduler) getQuartzScheduler(bundleContext);
        clearJobs();
        subjectEnrollmentsDataService.deleteAll();
        enrollmentDataService.deleteAll();
        subjectService.deleteAll();
        savedConfig = configService.getConfig();
    }

    @After
    public void cleanAfter() throws SchedulerException {
        clearJobs();
        subjectEnrollmentsDataService.deleteAll();
        enrollmentDataService.deleteAll();
        subjectService.deleteAll();
        configService.updateConfig(savedConfig);
    }

    @Test
    public void shouldEnrollAllVisitTypes() throws IOException, SchedulerException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            Subject subject = createSubjectWithRequireData("1");

            InputStream inputStream = getClass().getResourceAsStream("/enrollBasic.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBasic.csv");
            inputStream.close();

            subject = subjectService.findSubjectBySubjectId("1");
            SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());

            assertEquals(10, subjectEnrollments.getEnrollments().size());

            final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
            final String runonce = "-runonce";
            for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
                String triggerKeyString = campaignCompletedString + enrollment.getCampaignName() + ".1" + runonce;
                assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
            }

            for(Enrollment enrollment : subjectEnrollments.getEnrollments()) {
                assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
                assertEquals(new LocalDate(2015, 9, 10), enrollment.getReferenceDate());
            }
        } finally {
            stopFakingTime();
        }

    }

    @Test
    public void shouldEnrollWhenGetZetesDataAfterRave() throws IOException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
            inputStream.close();

            Subject subject = subjectService.findSubjectBySubjectId("1");
            List<Enrollment> enrollmentList = enrollmentDataService.retrieveAll();
            assertEquals(0, enrollmentList.size());

            subject.setPhoneNumber("123456789");
            subject.setLanguage(Language.English);
            subjectService.createOrUpdateForZetes(subject);

            subject = subjectService.findSubjectBySubjectId("1");
            enrollmentList = enrollmentDataService.retrieveAll();
            assertEquals(3, enrollmentList.size());

            SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
            for(Enrollment enrollment : subjectEnrollments.getEnrollments()) {
                assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            }
        } finally {
            stopFakingTime();
        }

    }

    @Test
    public void shouldNotReenrollWhenMotechProjectedDateWasChanged() throws IOException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            Subject subject = createSubjectWithRequireData("1");

            InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
            inputStream.close();

            inputStream = getClass().getResourceAsStream("/enrollChangeProjectedDate.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollChangeProjectedDate.csv");
            inputStream.close();

            SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
            for(Enrollment enrollment : subjectEnrollments.getEnrollments()) {
                assertEquals(new LocalDate(2015, 10, 10), enrollment.getReferenceDate());
            }

            subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
            assertEquals(3, subjectEnrollments.getEnrollments().size());

            for(Enrollment enrollment : subjectEnrollments.getEnrollments()) {
                assertEquals(new LocalDate(2015, 10, 10), enrollment.getReferenceDate());
                assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            }

        } finally {
            stopFakingTime();
        }

    }

    @Test
    public void shouldNotEnrollWhenSubjectDoesntHaveLanguageOrPhoneNumber() throws IOException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
            inputStream.close();

            Subject subject = subjectService.findSubjectBySubjectId("1");

            subject.setPhoneNumber("123456789");
            subject.setLanguage(null);
            subjectService.createOrUpdateForZetes(subject);

            SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());

            assertNull(subjectEnrollments);

            subject.setLanguage(Language.English);
            subject.setPhoneNumber(null);
            subjectService.createOrUpdateForZetes(subject);

            subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());

            assertNull(subjectEnrollments);

        } finally {
            stopFakingTime();
        }

    }

    @Ignore
    @Test
    public void shouldEnrollScreening() throws IOException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            Subject subject = createSubjectWithRequireData("1");
            subjectService.createOrUpdateForZetes(subject);

            List<Enrollment> enrollmentList = enrollmentDataService.retrieveAll();
            assertEquals(1, enrollmentList.size());

        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldEnrollBoostVaccinationForSpecificDay() throws IOException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));
            for(int i = 0; i < 7; i++){
                Subject subject = createSubjectWithRequireData(Integer.toString(i + 1));
            }

            InputStream inputStream = getClass().getResourceAsStream("/enrollBoostVaccination.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBoostVaccination.csv");
            inputStream.close();

            int i = 0;
            for (Subject subject : subjectService.getAll()) {

                SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
                assertEquals(1, subjectEnrollments.getEnrollments().size());
                Enrollment enrollment = subjectEnrollments.getEnrollments().iterator().next();
                assertEquals(VisitType.BOOST_VACCINATION_DAY.getValue() + " " + EbodacConstants.DAYS_OF_WEEK.get(i), enrollment.getCampaignName());
                assertEquals(new LocalDate(2015, 9, 20 + i), subjectEnrollments.getEnrollments().iterator().next().getReferenceDate());
                i++;
            }
        } finally {
            stopFakingTime();
        }
    }

    @Test(expected = EbodacEnrollmentException.class)
    public void shouldReenrollOnlyEnrolledCampaigns() {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            Subject subject = createSubjectWithRequireData("1");

            Visit visit = new Visit();
            visit.setDateProjected(new LocalDate(2015, 8, 17));
            visit.setType(VisitType.BOOST_VACCINATION_DAY);
            visit.setSubject(subject);

            ebodacEnrollmentService.reenrollSubject(visit);

        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldCompleteCampaignWhenVisitAlreadyTookPlace() throws IOException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            Subject subject = createSubjectWithRequireData("1");

            InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
            inputStream.close();

            List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
            for(Enrollment enrollment : enrollmentList) {
                assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            }

            inputStream = getClass().getResourceAsStream("/enrollActualDate.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollActualDate.csv");
            inputStream.close();

            enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");

            for(Enrollment enrollment : enrollmentList) {
                assertEquals(EnrollmentStatus.COMPLETED, enrollment.getStatus());
            }
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldCompleteCampaignWhenSubjectHaveDisconStdDate() throws IOException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            Subject subject = createSubjectWithRequireData("1");

            InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
            inputStream.close();

            List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
            for(Enrollment enrollment : enrollmentList) {
                assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            }

            subject = subjectService.findSubjectBySubjectId("1");
            subject.setDateOfDisconStd(new LocalDate(2015, 8, 8));
            subjectService.createOrUpdateForRave(subject);

            enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
            for(Enrollment enrollment : enrollmentList) {
                assertEquals(EnrollmentStatus.COMPLETED, enrollment.getStatus());
            }
        } finally {
            stopFakingTime();
        }

    }

    @Test
    public void shouldCompleteCampaignWhenSubjectHaveDisconVacDate() throws IOException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            Subject subject = createSubjectWithRequireData("1");

            InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
            inputStream.close();

            List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
            for(Enrollment enrollment : enrollmentList) {
                assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            }

            List<String> campaignList = new ArrayList<>();
            campaignList.add("Boost Vaccination Second Follow-up visit");
            Config config = new Config();
            config.setDisconVacCampaignsList(campaignList);
            configService.updateConfig(config);

            subject = subjectService.findSubjectBySubjectId("1");
            subject.setDateOfDisconVac(new LocalDate(2015, 8, 8));
            subjectService.createOrUpdateForRave(subject);

            enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
            assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(0).getStatus());
            assertEquals(EnrollmentStatus.COMPLETED, enrollmentList.get(1).getStatus());
            assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(2).getStatus());

        } finally {
            stopFakingTime();
        }

    }

    @Test
    public void shouldEnrollSubject() throws IOException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            Subject subject = createSubjectWithRequireData("1");

            InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
            inputStream.close();

            SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
            ebodacEnrollmentService.unenrollSubject(subjectEnrollments.getSubject().getSubjectId());

            subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
            for(Enrollment enrollment : subjectEnrollments.getEnrollments()) {
                assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
            }

            ebodacEnrollmentService.enrollSubject(subject.getSubjectId());

            subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
            assertEquals(3, subjectEnrollments.getEnrollments().size());

            for(Enrollment enrollment : subjectEnrollments.getEnrollments()) {
                assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            }

        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldEnrollSubjectToCampaign() throws IOException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            Subject subject = createSubjectWithRequireData("1");

            InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
            inputStream.close();

            ebodacEnrollmentService.unenrollSubject(subject.getSubjectId());

            List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
            for(Enrollment enrollment : enrollmentList) {
                assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
            }
            ebodacEnrollmentService.enrollSubjectToCampaign(subject.getSubjectId(), VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue());

            enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
            assertEquals(EnrollmentStatus.UNENROLLED, enrollmentList.get(0).getStatus());
            assertEquals(EnrollmentStatus.UNENROLLED, enrollmentList.get(1).getStatus());
            assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(2).getStatus());

        } finally {
            stopFakingTime();
        }

    }

    @Test
    public void shouldEnrollSubjectToCampaignWithNewDate() throws IOException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            Subject subject = createSubjectWithRequireData("1");

            InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
            inputStream.close();

            ebodacEnrollmentService.unenrollSubject(subject.getSubjectId());

            List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
            for(Enrollment enrollment : enrollmentList) {
                assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
                assertEquals(new LocalDate(2015, 10, 10), enrollment.getReferenceDate());
            }
            ebodacEnrollmentService.enrollSubjectToCampaignWithNewDate(subject.getSubjectId(), VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue(), new LocalDate(2015, 12, 1));

            enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
            assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(2).getStatus());
            assertEquals(new LocalDate(2015, 12, 1), enrollmentList.get(2).getReferenceDate());

        } finally {
            stopFakingTime();
        }

    }

    @Test(expected = EbodacEnrollmentException.class)
    public void shouldNotEnrollSubjectWithEnrolledStatus() throws IOException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            Subject subject = createSubjectWithRequireData("1");

            InputStream inputStream = getClass().getResourceAsStream("/enrollBasic.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBasic.csv");
            inputStream.close();

            SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
            assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());

            ebodacEnrollmentService.enrollSubject(subjectEnrollments.getSubject().getSubjectId());
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldUnenrollSubject() throws IOException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            Subject subject = createSubjectWithRequireData("1");

            InputStream inputStream = getClass().getResourceAsStream("/enrollBasic.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBasic.csv");
            inputStream.close();

            List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
            for(Enrollment enrollment : enrollmentList) {
                assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            }

            ebodacEnrollmentService.unenrollSubject(subject.getSubjectId());

            enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
            for(Enrollment enrollment : enrollmentList) {
                assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
            }

        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldUnenrollSubjectFromCampaign() throws IOException {
        fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
        for(Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }

        ebodacEnrollmentService.unenrollSubject(subject.getSubjectId(), VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue());

        enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
        assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(0).getStatus());
        assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(1).getStatus());
        assertEquals(EnrollmentStatus.UNENROLLED, enrollmentList.get(2).getStatus());

    }

    @Test(expected = EbodacEnrollmentException.class)
    public void shouldNotUnenrollSubjectWithUnenrolledStatus() throws IOException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            Subject subject = createSubjectWithRequireData("1");

            InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
            inputStream.close();

            ebodacEnrollmentService.unenrollSubject(subject.getSubjectId());
            SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
            assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments.getStatus());

            ebodacEnrollmentService.unenrollSubject(subject.getSubjectId());
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldReenrollSubject() throws IOException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            Subject subject = createSubjectWithRequireData("1");

            InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
            inputStream.close();

            List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
            for(Enrollment enrollment : enrollmentList) {
                assertEquals(new LocalDate(2015, 10, 10), enrollment.getReferenceDate());
            }

            subject = subjectService.findSubjectBySubjectId("1");
            for(Visit visit : subject.getVisits()) {
                visit.setMotechProjectedDate(new LocalDate(2015, 10, 11));
                ebodacEnrollmentService.reenrollSubject(visit);
            }
            SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId("1");
            for( Enrollment enrollment : subjectEnrollments.getEnrollments()) {
                assertEquals(new LocalDate(2015, 10, 11), enrollment.getReferenceDate());
            }

        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldReenrollSubjectToCampaignWithNewDate() throws IOException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            Subject subject = createSubjectWithRequireData("1");

            InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
            inputStream.close();

            List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
            for(Enrollment enrollment : enrollmentList) {
                assertEquals(new LocalDate(2015, 10, 10), enrollment.getReferenceDate());
            }

            ebodacEnrollmentService.reenrollSubjectWithNewDate(subject.getSubjectId(), VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue(), new LocalDate(2015, 12, 12));
            enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");

            assertEquals(new LocalDate(2015, 10, 10), enrollmentList.get(0).getReferenceDate());
            assertEquals(new LocalDate(2015, 10, 10), enrollmentList.get(1).getReferenceDate());
            assertEquals(new LocalDate(2015, 12, 12), enrollmentList.get(2).getReferenceDate());
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldCorrectlySetSubjectEnrollmentStatus() throws IOException {

        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            Subject subject = createSubjectWithRequireData("1");

            InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
            raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
            inputStream.close();


            SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
            assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());

            ebodacEnrollmentService.unenrollSubject(subject.getSubjectId(), subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue()).getCampaignName());
            subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
            assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());

            ebodacEnrollmentService.unenrollSubject(subject.getSubjectId(), subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_SECOND_FOLLOW_UP_VISIT.getValue()).getCampaignName());
            ebodacEnrollmentService.unenrollSubject(subject.getSubjectId(), subjectEnrollments.findEnrolmentByCampaignName("Boost Vaccination Day Saturday").getCampaignName());

            subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
            assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments.getStatus());

            ebodacEnrollmentService.enrollSubject(subjectEnrollments.getSubject().getSubjectId());

            subject = subjectService.findSubjectBySubjectId("1");
            Visit visit = subject.getVisits().get(0);
            visit.setDate(new LocalDate(2015, 8, 8));
            visitService.createOrUpdate(visit);

            subject = subjectService.findSubjectBySubjectId("1");
            subject.setDateOfDisconStd(new LocalDate(2015, 8, 8));
            subjectService.createOrUpdateForRave(subject);
            subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());

            assertEquals(EnrollmentStatus.COMPLETED, subjectEnrollments.getStatus());
        } finally {
            stopFakingTime();
        }

    }

    @Test
    public void shouldCheckIfSubjectIsEnrolled() throws IOException {
        try {
            fakeNow(newDateTime(2015, 8, 1, 0, 0, 0));

            Subject subject = createSubjectWithRequireData("1");

            Visit visit = new Visit();
            visit.setType(VisitType.BOOST_VACCINATION_DAY);
            visit.setMotechProjectedDate(new LocalDate(2015, 10, 10));
            visit.setSubject(subject);
            assertFalse(ebodacEnrollmentService.isEnrolled(visit));

            visitService.create(visit);
            subject = subjectService.findSubjectBySubjectId("1");
            ebodacEnrollmentService.enrollSubject(subject);

            SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
            assertTrue(ebodacEnrollmentService.isEnrolled(visit));

            ebodacEnrollmentService.unenrollSubject(subject.getSubjectId());
            assertFalse(ebodacEnrollmentService.isEnrolled(visit));

        } finally {
            stopFakingTime();
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

    private Subject createSubjectWithRequireData(String subjectId) {
        Subject subject = new Subject();
        subject.setSubjectId(subjectId);
        subject.setLanguage(Language.English);
        subject.setPhoneNumber("123456789");
        subject.setSiteId("asdas");
        subjectService.create(subject);
        return subject;
    }
}