package org.motechproject.ebodac.osgi;

import org.motechproject.ebodac.domain.SubjectRegistration;
import org.motechproject.ebodac.service.SubjectRegistrationService;
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
 * Verify that SubjectRegistrationService present, functional.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class SubjectRegistrationServiceIT extends BasePaxIT {

    @Inject
    private SubjectRegistrationService subcjectRegistrationService;

    @Test
    public void testHelloWorldRecordService() throws Exception {
        SubjectRegistration testRecord = new SubjectRegistration("testName", "test message");
        subcjectRegistrationService.add(testRecord);

        SubjectRegistration record = subcjectRegistrationService.findRecordByName(testRecord.getFirstName());
        assertEquals(testRecord, record);

        List<SubjectRegistration> records = subcjectRegistrationService.getRecords();
        assertTrue(records.contains(testRecord));

        subcjectRegistrationService.delete(testRecord);
        record = subcjectRegistrationService.findRecordByName(testRecord.getFirstName());
        assertNull(record);
    }
}