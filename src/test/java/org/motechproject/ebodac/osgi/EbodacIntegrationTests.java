package org.motechproject.ebodac.osgi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Ebodac bundle integration tests suite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        EbodacServiceIT.class,
        EbodacWebIT.class,
        SubjectServiceIT.class,
        EbodacConfigServiceIT.class,
        RaveImportServiceIT.class,
        ReportServiceIT.class
})
public class EbodacIntegrationTests {
}
