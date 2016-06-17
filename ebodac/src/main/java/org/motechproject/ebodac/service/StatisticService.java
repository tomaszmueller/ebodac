package org.motechproject.ebodac.service;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.dto.IvrEngagementStatisticDto;
import org.motechproject.ebodac.dto.IvrAndSmsStatisticDto;

import java.io.IOException;
import java.util.List;

public interface StatisticService {

    List<IvrAndSmsStatisticDto> getStatisticForIvr(Range<DateTime> dateRange) throws IOException;

    List<IvrAndSmsStatisticDto> getStatisticForSms(Range<DateTime> dateRange) throws IOException;

    List<IvrEngagementStatisticDto> getIvrEngagementStatistic() throws IOException;
}
