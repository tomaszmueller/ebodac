package org.motechproject.ebodac.osgi;

import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.service.SubjectService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Verify that SubjectService present, functional.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class SubjectServiceIT extends BasePaxIT {

    @Inject
    private SubjectService subjectService;

    @Test
    public void testSubjectService() throws Exception {
        Subject testRecord = new Subject("123", "test 1st name", "test household name",
                "ASD-22", "entity-facility-id", "Sesame Street", Language.English, "Jason Bourne",
                EbodacConstants.SITE_ID_FOR_STAGE_I);
        subjectService.createOrUpdate(testRecord);

        Subject record = subjectService.findSubjectByName(testRecord.getName());
        assertEquals(testRecord, record);

        List<Subject> records = subjectService.getAll();
        assertTrue(records.contains(testRecord));

        subjectService.delete(testRecord);
        record = subjectService.findSubjectByName(testRecord.getName());
        assertNull(record);
    }
}
