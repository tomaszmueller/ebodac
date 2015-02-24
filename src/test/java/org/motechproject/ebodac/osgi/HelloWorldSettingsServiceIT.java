package org.motechproject.ebodac.osgi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.motechproject.testing.osgi.BasePaxIT;

import javax.inject.Inject;

import org.motechproject.ebodac.service.HelloWorldSettingsService;
import org.osgi.framework.ServiceReference;

import static org.junit.Assert.assertNotNull;

/**
 * Verify that HelloWorldSettingsService is present, functional.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class HelloWorldSettingsServiceIT extends BasePaxIT {

    @Inject
    private HelloWorldSettingsService helloSettingsService;

    @Test
    public void testHelloWorldServicePresent() throws Exception {
        assertNotNull(helloSettingsService.getSettingsValue("org.motechproject.ebodac.sample.setting"));
        assertNotNull(helloSettingsService.getSettingsValue("org.motechproject.ebodac.bundle.name"));
        helloSettingsService.logInfoWithModuleSettings("test info message");
    }
}
