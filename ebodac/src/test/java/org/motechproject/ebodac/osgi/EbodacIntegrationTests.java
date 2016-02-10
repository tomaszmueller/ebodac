package org.motechproject.ebodac.osgi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Ebodac bundle integration tests suite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        EbodacConfigServiceIT.class,
        EbodacEnrollmentServiceIT.class,
        EbodacServiceIT.class,
        EbodacWebIT.class,
        EnrollmentControllerIT.class,
        HistoryServiceIT.class,
        LookupServiceIT.class,
        RaveImportServiceIT.class,
        ReportControllerIT.class,
        SubjectCsvImportCustomizerIT.class,
        SubjectServiceIT.class,
        VisitServiceIT.class
})
public class EbodacIntegrationTests {
}
