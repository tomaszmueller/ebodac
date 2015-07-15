package org.motechproject.ebodac.osgi;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.RaveImportService;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.util.Order;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.junit.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static org.junit.Assert.assertEquals;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class HistoryServiceIT extends BasePaxIT {

    @Inject
    private RaveImportService raveImportService;

    @Inject
    private SubjectDataService subjectDataService;

    @Inject
    private VisitDataService visitDataService;

    @Inject
    private HistoryService historyService;

    @Before
    public void cleanBefore() {
        visitDataService.deleteAll();
        subjectDataService.deleteAll();
    }

    @After
    public void cleanAfter() {
        visitDataService.deleteAll();
        subjectDataService.deleteAll();

    }

    @Test
    public void shouldCreateHistoryRecordsForSubject() throws IOException {

        InputStream in = getClass().getResourceAsStream("/history.csv");
        raveImportService.importCsv(new InputStreamReader(in));
        in.close();

        QueryParams qp = new QueryParams(new Order("subjectId", Order.Direction.ASC));

        List<List<Subject>> subjectsHistoryRecords = new ArrayList<List<Subject>>();

        for (Subject subject : subjectDataService.retrieveAll())
        {
            subjectsHistoryRecords.add(historyService.getHistoryForInstance(subject, qp));
        }
        assertEquals(3, subjectsHistoryRecords.get(0).size());
        assertEquals(1, subjectsHistoryRecords.get(1).size());

        in = getClass().getResourceAsStream("/history2.csv");
        raveImportService.importCsv(new InputStreamReader(in));
        in.close();

        subjectsHistoryRecords.clear();
        for(Subject subject : subjectDataService.retrieveAll()) {
            subjectsHistoryRecords.add(historyService.getHistoryForInstance(subject, qp));
        }
        assertEquals(4, subjectsHistoryRecords.get(0).size());
        assertEquals(1, subjectsHistoryRecords.get(1).size());
        assertEquals(2, subjectsHistoryRecords.get(2).size());
    }

    @Test
    public void shouldCreateHistoryRecordsForVisit() throws IOException {

        InputStream in = getClass().getResourceAsStream("/history3.csv");
        raveImportService.importCsv(new InputStreamReader(in));
        in.close();

        List<Visit> visitList = visitDataService.findVisitByDate(new DateTime(2015, 7, 13, 0, 0, 0));
        assertEquals(1,visitList.size());
        assertEquals(3, historyService.countHistoryRecords(visitList.get(0)));

        visitList = visitDataService.findVisitByDate(new DateTime(2015, 6, 10, 0, 0, 0));
        assertEquals(1,visitList.size());
        assertEquals(3, historyService.countHistoryRecords(visitList.get(0)));

        visitList = visitDataService.findVisitByDate(new DateTime(2015, 6, 11, 0, 0, 0));
        assertEquals(1, visitList.size());
        assertEquals(2, historyService.countHistoryRecords(visitList.get(0)));

        visitList = visitDataService.findVisitByDate(new DateTime(2015, 6, 12, 0, 0, 0));
        assertEquals(1,visitList.size());
        assertEquals(1, historyService.countHistoryRecords(visitList.get(0)));

    }

    @Test
    public void newRecordsForSubjectWhenAddingNewVisit() throws IOException {
        InputStream in = getClass().getResourceAsStream("/history3.csv");
        raveImportService.importCsv(new InputStreamReader(in));
        in.close();

        QueryParams qp = new QueryParams(new Order("subjectId", Order.Direction.ASC));

        Subject subject = subjectDataService.findSubjectBySubjectId("2");
        List<Subject> subjectHistoryRecords = new ArrayList<Subject>();
        subjectHistoryRecords = historyService.getHistoryForInstance(subject, qp);

        assertEquals(4, subjectHistoryRecords.size());
    }

    @Ignore
    @Test
    public void shouldNotCreateNewRecordsForSubjectWhenUpdatingVisit() throws IOException {
        InputStream in = getClass().getResourceAsStream("/history3.csv");
        raveImportService.importCsv(new InputStreamReader(in));
        in.close();

        QueryParams qp = new QueryParams(new Order("subjectId", Order.Direction.ASC));

        Subject subject = subjectDataService.findSubjectBySubjectId("1");
        List<Subject> subjectHistoryRecords = new ArrayList<Subject>();
        subjectHistoryRecords = historyService.getHistoryForInstance(subject, qp);

        assertEquals(1, subjectHistoryRecords.size());
    }
}
