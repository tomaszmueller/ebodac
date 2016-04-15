package org.motechproject.ebodac.service;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.IvrEngagementStatistic;
import org.motechproject.ebodac.domain.IvrAndSmsStatistic;

import java.io.IOException;
import java.util.List;

public interface StatisticService {

    List<IvrAndSmsStatistic> getStatisticForIvr(Range<DateTime> dateRange) throws IOException;

    List<IvrAndSmsStatistic> getStatisticForSms(Range<DateTime> dateRange) throws IOException;

    List<IvrEngagementStatistic> getIvrEngagementStatistic() throws IOException;
}
