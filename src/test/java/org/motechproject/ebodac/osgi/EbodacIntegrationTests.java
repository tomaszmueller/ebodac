package org.motechproject.ebodac.osgi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Ebodac bundle integration tests suite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        EbodacConfigServiceIT.class,
        EbodacServiceIT.class,
        EbodacWebIT.class,
        HistoryServiceIT.class,
        JobImporterIT.class,
        MessageCampaignImporterIT.class,
        RaveImportServiceIT.class,
        ReportControllerIT.class,
        ReportServiceIT.class,
        SubjectServiceIT.class,
        TaskImporterIT.class,
        VisitControllerIT.class,
        VisitServiceIT.class
})
public class EbodacIntegrationTests {
}
