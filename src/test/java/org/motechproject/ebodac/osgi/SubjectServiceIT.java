package org.motechproject.ebodac.osgi;

import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.PhoneType;
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
        Subject testRecord = new Subject("123", "test 1st name",
                "test last name", 22, "Gdynia", Language.ENGLISH, PhoneType.PERSONAL);
        subjectService.add(testRecord);

        Subject record = subjectService.findRegistrationByFirstName(testRecord.getFirstName());
        assertEquals(testRecord, record);

        List<Subject> records = subjectService.getAll();
        assertTrue(records.contains(testRecord));

        subjectService.delete(testRecord);
        record = subjectService.findRegistrationByFirstName(testRecord.getFirstName());
        assertNull(record);
    }
}
