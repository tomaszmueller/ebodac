package org.motechproject.ebodac.osgi;

import org.motechproject.ebodac.service.EbodacService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

/**
 * Verify that EbodacService present, functional.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class EbodacServiceIT extends BasePaxIT {

    @Inject
    private EbodacService ebodacService;

    @Test
    public void testEbodacServicePresent() throws Exception {
        assertNotNull(ebodacService);
    }
}
