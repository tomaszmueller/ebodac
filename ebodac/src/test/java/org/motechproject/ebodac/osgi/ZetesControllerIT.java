package org.motechproject.ebodac.osgi;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.repository.IvrAndSmsStatisticReportDataService;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.VisitService;
import org.motechproject.ebodac.web.SubmitSubjectRequest;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.utils.TestContext;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ZetesControllerIT extends BasePaxIT {

    @Inject
    private IvrAndSmsStatisticReportDataService ivrAndSmsStatisticReportDataService;

    @Inject
    private VisitService visitService;

    @Inject
    private SubjectDataService subjectDataService;

    @Inject
    private SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    @Inject
    private VisitDataService visitDataService;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @BeforeClass
    public static void beforeClass() throws IOException, InterruptedException {
        createAdminUser();
        login();
        Thread.sleep(EbodacConstants.LOGIN_WAIT_TIME);
    }

    @Before
    public void cleanBefore() {
        ivrAndSmsStatisticReportDataService.deleteAll();
        visitDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        subjectDataService.deleteAll();
    }

    @After
    public void cleanAfter() {
        ivrAndSmsStatisticReportDataService.deleteAll();
        visitDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        subjectDataService.deleteAll();
    }

    @Test
    public void shouldSaveSubjectWithProperData() throws IOException, InterruptedException {
        SubmitSubjectRequest submitSubjectRequest = new SubmitSubjectRequest("123456789", "name", "householdName", "1", "address", "eng", "community", "siteId", "siteName", "headOfHouseHold", "chiefdom", "section", "district");
        String json = OBJECT_MAPPER.writeValueAsString(submitSubjectRequest);
        HttpResponse response = sendJson(json, null);
        assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
        assertNotNull(subjectDataService.findBySubjectId("1"));
    }

    @Test
    public void shouldSaveSubjectWithEmptyData() throws IOException, InterruptedException {
        SubmitSubjectRequest submitSubjectRequest = new SubmitSubjectRequest("", "", "", "", "", "", "", "", "", "", "", "", "");
        String json = OBJECT_MAPPER.writeValueAsString(submitSubjectRequest);
        HttpResponse response = sendJson(json, null);
        assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
        assertEquals(1, subjectDataService.retrieveAll().size());
    }

    @Test
    public void shouldNotSaveSubjectWithNullSiteName() throws IOException, InterruptedException {
        SubmitSubjectRequest submitSubjectRequest = new SubmitSubjectRequest("123456789", "name", "householdName", "1", "address", "English", "community", "siteId", null, "headOfHouseHold", "chiefdom", "section", "district");
        String json = OBJECT_MAPPER.writeValueAsString(submitSubjectRequest);
        HttpResponse response = sendJson(json, HttpServletResponse.SC_BAD_REQUEST);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
        assertEquals(0, subjectDataService.retrieveAll().size());
    }

    @Test
    public void shouldNotSaveSubjectWithNullSubjectId() throws IOException, InterruptedException {
        SubmitSubjectRequest submitSubjectRequest = new SubmitSubjectRequest("123456789", "name", "householdName", null, "address", "English", "community", "siteId", "siteName", "headOfHouseHold", "chiefdom", "section", "district");
        String json = OBJECT_MAPPER.writeValueAsString(submitSubjectRequest);
        HttpResponse response = sendJson(json, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        assertEquals(0, subjectDataService.retrieveAll().size());
    }

    private HttpResponse sendJson(String json, Integer expectedErrorCode) throws IOException, InterruptedException {
        HttpPost post = new HttpPost(String.format("http://localhost:%d/ebodac/registration/submit", TestContext.getJettyPort()));
        StringEntity entity = new StringEntity(json);
        post.setEntity(entity);
        post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        HttpResponse response;
        if (expectedErrorCode == null) {
            response = getHttpClient().execute(post);
        } else {
            response  = getHttpClient().execute(post, expectedErrorCode);
        }
        assertNotNull(response);
        return response;
    }
}
