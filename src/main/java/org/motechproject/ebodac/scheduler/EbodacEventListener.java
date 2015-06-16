package org.motechproject.ebodac.scheduler;

import org.joda.time.DateTime;
import org.motechproject.ebodac.client.EbodacEmailClient;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.EbodacService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mds.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EbodacEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EbodacEventListener.class);

    @Autowired
    private EbodacService ebodacService;

    @Autowired
    private EbodacEmailClient ebodacEmailClient;

    @Autowired
    private ConfigService configService;

    @MotechListener(subjects = {EbodacConstants.ZETES_UPDATE_EVENT})
    public void zetesUpdate(MotechEvent event) {
        Object zetesUrl = event.getParameters().get(EbodacConstants.ZETES_URL);
        Object username = event.getParameters().get(EbodacConstants.ZETES_USERNAME);
        Object password = event.getParameters().get(EbodacConstants.ZETES_PASSWORD);
        ebodacService.sendUpdatedSubjects(zetesUrl.toString(), username.toString(), password.toString());
    }

    @MotechListener(subjects = {EbodacConstants.EMAIL_CHECK_EVENT})
    public void emailCheck(MotechEvent event) {
        Config config = configService.getConfig();
        String host = config.getEmailHost();
        String user = config.getEmail();
        String password = config.getEmailPassword();

        if (ebodacEmailClient.hasNewJobCompletionMessage(host, user, password)) {
            ebodacService.fetchCSVUpdates();
        }
    }

    @MotechListener(subjects = {EbodacConstants.DAILY_REPORT_EVENT})
    public void generateDailyReport(MotechEvent event) {
        DateTime startDate = (DateTime) event.getParameters().get(EbodacConstants.DAILY_REPORT_EVENT_START_DATE);
        ebodacService.generateDailyReport();
        LOGGER.info("Daily Reports generation completed");
    }

    @MotechListener(subjects = {EbodacConstants.SUBJECT_UPDATED})
    public void updateReportWhenSubjectChanged(MotechEvent event) {
        Long id = (Long) event.getParameters().get(Constants.MDSEvents.OBJECT_ID);
        ebodacService.updateReportsForSubject(id);
        LOGGER.info("Reports updated for Subject with id: {}", id);
    }
}
