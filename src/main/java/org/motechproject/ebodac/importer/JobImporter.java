package org.motechproject.ebodac.importer;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.RepeatingPeriodSchedulableJob;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JobImporter implements OsgiServiceLifecycleListener {

    private MotechSchedulerService motechSchedulerService;

    @Override
    public void bind(Object o, Map map) throws Exception {
        this.motechSchedulerService = (MotechSchedulerService) o;
        importDailyReportJob();
    }

    @Override
    public void unbind(Object o, Map map) throws Exception {
        this.motechSchedulerService = null;
    }

    private void importDailyReportJob() {
        Period period = Period.days(1);
        DateTime startDate = DateTime.parse(DateTime.now().plusDays(1).toString(DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT))
                + EbodacConstants.DAILY_REPORT_EVENT_START_HOUR, DateTimeFormat.forPattern(EbodacConstants.REPORT_START_DATE_FORMAT));

        Map<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(EbodacConstants.DAILY_REPORT_EVENT_START_DATE, startDate);

        MotechEvent event = new MotechEvent(EbodacConstants.DAILY_REPORT_EVENT, eventParameters);

        RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob(event, startDate.toDate(), null, period, true);
        motechSchedulerService.safeScheduleRepeatingPeriodJob(job);
    }
}
