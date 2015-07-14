package org.motechproject.ebodac.osgi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.dao.VisitDAO;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.utils.VisitUtils;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.utils.TestContext;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class VisitControllerIT extends BasePaxIT {
    @Inject
    private SubjectDataService subjectDataService;

    @Inject
    private VisitDataService visitDataService;

    private Type typeOfRecords = new TypeToken<Records<VisitDAO>>() {}.getType();

    private Subject firstSubject;

    private Subject secondSubject;

    private ArrayList<Visit> testVisits = new ArrayList<Visit>();

    private DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

    @Before
    public void cleanBefore() throws IOException, InterruptedException {
        visitDataService.deleteAll();
        subjectDataService.deleteAll();
        resetTestFields();
        addTestVisitsToDB();
        login();
    }

    @After
    public void cleanAfter() {
        visitDataService.deleteAll();
        subjectDataService.deleteAll();
    }

    private void resetTestFields() {
        firstSubject = new Subject("1000000161", "Michal", "Abacki", "Cabacki",
                "729402018364", "address", Language.English, "community", "B05-SL10001");

        secondSubject = new Subject("1000000162", "Rafal", "Dabacki", "Ebacki",
                "44443333222", "address1", Language.Susu, "community", "B05-SL10001");

        testVisits.add(VisitUtils.createVisit(firstSubject, VisitType.SCREENING, DateTime.parse("2014-10-17", formatter),
                null, "owner"));

        testVisits.add(VisitUtils.createVisit(firstSubject, VisitType.PRIME_VACCINATION_FOLLOW_UP_VISIT, DateTime.parse("2014-10-19", formatter),
                null, "owner"));

        testVisits.add(VisitUtils.createVisit(secondSubject, VisitType.SCREENING, DateTime.parse("2014-10-19", formatter),
                null, "owner"));

        testVisits.add(VisitUtils.createVisit(secondSubject, VisitType.PRIME_VACCINATION_FOLLOW_UP_VISIT, DateTime.parse("2014-10-21", formatter),
                null, "owner"));

        testVisits.add(VisitUtils.createVisit(firstSubject, VisitType.BOOST_VACCINATION_DAY, DateTime.parse("2014-10-17", formatter),
                null, "owner"));
    }

    private void addTestVisitsToDB() {
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        for(Visit visit : testVisits) {
            visitDataService.create(visit);
        }

        assertEquals(2, subjectDataService.retrieveAll().size());
        assertEquals(5, visitDataService.retrieveAll().size());
    }

    @Test
    public void shouldGetCorrectNumberOfRows() throws IOException, InterruptedException {
        Records<VisitDAO> records = new Gson().fromJson(getVisitsWithoutLookup(1,4), typeOfRecords);
        List<VisitDAO> visitsDao = records.getRows();
        assertEquals(4, visitsDao.size());

        records = new Gson().fromJson(getVisitsWithoutLookup(2,4), typeOfRecords);
        visitsDao = records.getRows();
        assertEquals(1, visitsDao.size());
    }

    @Test
    public void shouldGetVisitsByDate() throws IOException, InterruptedException {
        Records<VisitDAO> records = new Gson().fromJson(getVisitsByLookup("{\"Date\":\"2014-10-21 00:00 +0200\"}", "Find Visit By Date",1, 5), typeOfRecords);
        List<VisitDAO> visitsDao = records.getRows();
        assertEquals(1, visitsDao.size());

        VisitUtils.checkVisitFields(testVisits.get(3), visitsDao.get(0).toVisit());
    }

    @Test
    public void shouldGetVisitsBySubjectAddress() throws IOException, InterruptedException {
        Records<VisitDAO> records = new Gson().fromJson(getVisitsByLookup("{\"Address\":\"address1\"}", "Find Visit By Subject Address", 1, 5), typeOfRecords);
        List<VisitDAO> visitsDao = records.getRows();
        assertEquals(2, visitsDao.size());

        List<Visit> visits = VisitUtils.convertDAOVisitsToVisits(visitsDao);
        assertEquals(2, visits.size());
        assertTrue(testVisits.containsAll(visits));
    }

    @Test
    public void shouldGetVisitsBySubjectName() throws IOException, InterruptedException {
        Records<VisitDAO> records = new Gson().fromJson(getVisitsByLookup("{\"Name\":\"Rafal\"}", "Find Visit By Subject Name", 1, 5), typeOfRecords);
        List<VisitDAO> visitsDao = records.getRows();
        assertEquals(2, visitsDao.size());

        List<Visit> visits = VisitUtils.convertDAOVisitsToVisits(visitsDao);
        assertEquals(2, visits.size());
        assertTrue(testVisits.containsAll(visits));
    }

    @Test
    public void shouldGetVisitsBySubjectId() throws IOException, InterruptedException {
        Records<VisitDAO> records = new Gson().fromJson(getVisitsByLookup("{\"SubjectId\":\"1000000162\"}", "Find Visit By SubjectId", 1, 5), typeOfRecords);
        List<VisitDAO> visitsDao = records.getRows();
        assertEquals(2, visitsDao.size());

        List<Visit> visits = VisitUtils.convertDAOVisitsToVisits(visitsDao);
        assertEquals(2, visits.size());
        assertTrue(testVisits.containsAll(visits));
    }

    @Test
    public void shouldGetVisitsByType() throws IOException, InterruptedException {
        Records<VisitDAO> records = new Gson().fromJson(getVisitsByLookup("{\"Type\":\"Screening\"}", "Find Visit By Type", 1, 5), typeOfRecords);
        List<VisitDAO> visitsDao = records.getRows();
        assertEquals(2, visitsDao.size());

        List<Visit> visits = VisitUtils.convertDAOVisitsToVisits(visitsDao);
        assertEquals(2, visits.size());
        assertTrue(testVisits.containsAll(visits));
    }

    private String getVisitsWithoutLookup(int page, int rows) throws IOException, InterruptedException {
        return getVisitsByLookup("{}","",page,rows);
    }

    private String getVisitsByLookup(String fields, String lookupType, int page, int rows) throws IOException, InterruptedException {
        HttpPost post;
        post = new HttpPost(String.format("http://localhost:%d/ebodac/visitsRecords", TestContext.getJettyPort()));
        ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("fields", fields));
        nvps.add(new BasicNameValuePair("filter", ""));
        nvps.add(new BasicNameValuePair("lookup", lookupType));
        nvps.add(new BasicNameValuePair("page", Integer.toString(page)));
        nvps.add(new BasicNameValuePair("rows", Integer.toString(rows)));
        nvps.add(new BasicNameValuePair("sortColumn", ""));
        nvps.add(new BasicNameValuePair("sortDirection", "asc"));
        post.setEntity(new UrlEncodedFormEntity(nvps, "UTF8"));
        post.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

        String response = getHttpClient().execute(post, new BasicResponseHandler());
        assertNotNull(response);

        return response;
    }
}
