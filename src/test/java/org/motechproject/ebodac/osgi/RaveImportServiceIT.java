package org.motechproject.ebodac.osgi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.RaveImportService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class RaveImportServiceIT extends BasePaxIT {

    @Inject
    private RaveImportService raveImportService;

    @Inject
    private SubjectDataService subjectDataService;

    @Inject
    private VisitDataService visitDataService;

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
    public void shouldImportCsv() throws Exception {
        List<Subject> subjects = subjectDataService.retrieveAll();
        assertEquals(0, subjects.size());
        List<Visit> visits = visitDataService.retrieveAll();
        assertEquals(0, visits.size());

        InputStream in = getClass().getResourceAsStream("/sample.csv");
        assertNotNull(in);

        raveImportService.importCsv(new InputStreamReader(in));
        in.close();

        subjects = subjectDataService.retrieveAll();
        assertEquals(8, subjects.size());
        visits = visitDataService.retrieveAll();
        assertEquals(27, visits.size());
    }
}
