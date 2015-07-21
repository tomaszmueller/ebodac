package org.motechproject.ebodac.osgi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Enrollment;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.repository.EnrollmentDataService;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.utils.VisitUtils;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.utils.TestContext;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.NameMatcher;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class EnrollmentControllerIT extends BasePaxIT {
    @Inject
    private SubjectDataService subjectDataService;

    @Inject
    private VisitDataService visitDataService;

    @Inject
    private SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    @Inject
    private EnrollmentDataService enrollmentDataService;

    @Inject
    private EbodacEnrollmentService ebodacEnrollmentService;

    @Inject
    private BundleContext bundleContext;

    private Scheduler scheduler;

    private Subject firstSubject;

    private Subject secondSubject;

    private ArrayList<Visit> testVisits = new ArrayList<Visit>();

    private DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

    private ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
        @Override
        public String handleResponse(final HttpResponse response) throws IOException {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new ClientProtocolException("Response contains no content");
            }
            ContentType contentType = ContentType.getOrDefault(entity);
            Charset charset = contentType.getCharset();
            return Integer.toString(response.getStatusLine().getStatusCode()) + "__" + EntityUtils.toString(entity, charset);
        }
    };

    @BeforeClass
    public static void beforeClass() throws IOException, InterruptedException {
        createAdminUser();
        login();
        Thread.sleep(EbodacConstants.LOGIN_WAIT_TIME);
    }

    @Before
    public void cleanBefore() throws SchedulerException {
        visitDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        enrollmentDataService.deleteAll();
        subjectDataService.deleteAll();
        clearJobs();
        resetTestFields();
        addTestVisitsToDB();
    }

    @After
    public void cleanAfter() throws SchedulerException {
        visitDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        enrollmentDataService.deleteAll();
        subjectDataService.deleteAll();
        clearJobs();
    }

    private void resetTestFields() {
        firstSubject = new Subject("1000000161", "Michal", "Abacki", "Cabacki",
                "729402018364", "address", Language.English, "community", "B05-SL10001", "chiefdom", "section", "district");

        secondSubject = new Subject("1000000162", "Rafal", "Dabacki", "Ebacki",
                "44443333222", "address1", Language.Susu, "community", "B05-SL10001", "chiefdom", "section", "district");

        testVisits.add(VisitUtils.createVisit(null, VisitType.SCREENING, LocalDate.parse("2014-10-17", formatter),
                null, "owner"));

        testVisits.add(VisitUtils.createVisit(firstSubject, VisitType.SCREENING, LocalDate.parse("2014-10-17", formatter),
                null, "owner"));

        testVisits.add(VisitUtils.createVisit(firstSubject, VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT, null,
                LocalDate.parse("2015-10-19", formatter), "owner"));

        testVisits.add(VisitUtils.createVisit(secondSubject, VisitType.PRIME_VACCINATION_DAY, null,
                LocalDate.parse("2015-10-19", formatter), "owner"));

        Visit visit = testVisits.get(2);
        visit.setMotechProjectedDate(LocalDate.parse("2015-10-10", formatter));
    }

    private void addTestVisitsToDB() {
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        for(Visit visit : testVisits) {
            visitDataService.create(visit);
        }

        assertEquals(2, subjectDataService.retrieveAll().size());
        assertEquals(4, visitDataService.retrieveAll().size());
    }

    private void clearJobs() throws SchedulerException {
        scheduler = (Scheduler) getQuartzScheduler(bundleContext);
        NameMatcher<JobKey> nameMatcher = NameMatcher.jobNameStartsWith("org.motechproject.messagecampaign");
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupStartsWith("default"));
        for(JobKey jobKey : jobKeys) {
            if(nameMatcher.isMatch(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        }
    }

    @Test
    public void shouldNotReenrollVisit() throws IOException, InterruptedException {
        try {
            fakeNow(newDateTime(2015, 7, 31, 10, 0, 0));
            checkResponse(400, "ebodac.enrollment.error.nullVisit", reenrollVisit(null, 400));

            checkResponse(400, "ebodac.enrollment.error.noSubject", reenrollVisit(testVisits.get(0), 400));

            Visit visit = testVisits.get(1);
            visit.setType(VisitType.THIRD_LONG_TERM_FOLLOW_UP_VISIT);
            checkResponse(500, "ebodac.enrollment.error.noVisitInDB", reenrollVisit(visit, 500));
            visit.setType(VisitType.SCREENING);

            checkResponse(400, "ebodac.enrollment.error.visitCompleted",
                    reenrollVisit(testVisits.get(1), 400));

            visit = testVisits.get(2);
            visit.setMotechProjectedDate(null);
            checkResponse(400, "ebodac.enrollment.error.EmptyPlannedDate",
                    reenrollVisit(visit, 400));
            visit.setMotechProjectedDate(LocalDate.parse("2015-10-10", formatter));

            checkResponse(400, "ebodac.enrollment.error.plannedDateNotChanged",
                    reenrollVisit(testVisits.get(2), 400));

            visit = testVisits.get(2);
            visit.setMotechProjectedDate(LocalDate.parse("2014-10-17", formatter));
            checkResponse(400, "ebodac.enrollment.error.plannedDateInPast",
                    reenrollVisit(visit, 400));
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldReenrollVisit() throws IOException, InterruptedException {
        try {
            fakeNow(newDateTime(2015, 7, 31, 10, 0, 0));
            assertEquals(0, enrollmentDataService.retrieveAll().size());
            Visit visit = testVisits.get(3);

            ebodacEnrollmentService.enrollSubject(secondSubject);
            visit.setMotechProjectedDate(LocalDate.parse("2015-10-11", formatter));
            checkResponse(200, "", reenrollVisit(visit, 200));

            List<Enrollment> enrollments = enrollmentDataService.retrieveAll();
            assertEquals(2, enrollments.size());

            checkCampaignEnrollment("1000000162", VisitType.PRIME_VACCINATION_DAY.getValue(),
                    LocalDate.parse("2015-10-11", formatter), enrollments.get(0));

            checkCampaignEnrollment("1000000162", EbodacConstants.BOOSTER_RELATED_MESSAGES,
                    LocalDate.parse("2015-10-11", formatter), enrollments.get(1));
        } finally {
            stopFakingTime();
        }
    }

    private String reenrollVisit(Visit visit, int errorCode) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(visit);
        HttpPost post = new HttpPost(String.format("http://localhost:%d/ebodac/reenrollSubject", TestContext.getJettyPort()));
        StringEntity jsonEntity;
        jsonEntity = new StringEntity(json);
        post.setEntity(jsonEntity);
        post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");

        String response = getHttpClient().execute(post, responseHandler, errorCode);
        assertNotNull(response);

        return response;
    }

    private void checkResponse(int code, String message, String response) {
        String[] codeAndMessage = response.split("__");
        if(code != 200) {
            assertEquals(2, codeAndMessage.length);
            assertEquals(code, Integer.parseInt(codeAndMessage[0]));
            assertEquals(message, codeAndMessage[1]);
        } else {
            assertEquals(1, codeAndMessage.length);
            assertEquals(code, Integer.parseInt(codeAndMessage[0]));
        }
    }

    private void checkCampaignEnrollment(String externalId, String campaignName,
                                         LocalDate referenceDate, Enrollment enrollment) {
        assertEquals(externalId, enrollment.getExternalId());
        assertEquals(campaignName, enrollment.getCampaignName());
        assertEquals(referenceDate, enrollment.getReferenceDate());
    }

}
