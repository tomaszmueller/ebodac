package org.motechproject.ebodac.service.impl;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.dto.IvrAndSmsStatisticDto;
import org.motechproject.ebodac.dto.IvrEngagementStatisticDto;
import org.motechproject.ebodac.repository.IvrAndSmsStatisticReportDataService;
import org.motechproject.ebodac.service.StatisticService;
import org.motechproject.mds.query.SqlQueryExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jdo.Query;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("statisticService")
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private IvrAndSmsStatisticReportDataService ivrAndSmsStatisticReportDataService;

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    public List<IvrAndSmsStatisticDto> getStatisticForIvr(final Range<DateTime> dateRange) throws IOException {
        return getStatisticForQuery(dateRange, IOUtils.toString(getClass().getResourceAsStream("/sql/ivr_statistic_query.sql")));
    }

    @Override
    public List<IvrAndSmsStatisticDto> getStatisticForSms(final Range<DateTime> dateRange) throws IOException {
        return getStatisticForQuery(dateRange, IOUtils.toString(getClass().getResourceAsStream("/sql/sms_statistic_query.sql")));
    }

    @Override
    public List<IvrEngagementStatisticDto> getIvrEngagementStatistic() throws IOException {
        final String query = IOUtils.toString(getClass().getResourceAsStream("/sql/ivr_engagement_statistic_query.sql"));

        return ivrAndSmsStatisticReportDataService.executeSQLQuery(new SqlQueryExecution<List<IvrEngagementStatisticDto>>() {

            @SuppressWarnings("unchecked")
            @Override
            public List<IvrEngagementStatisticDto> execute(Query query) {
                query.setResultClass(IvrEngagementStatisticDto.class);

                return (List<IvrEngagementStatisticDto>) query.execute();
            }

            @Override
            public String getSqlQuery() {
                return query;
            }
        });
    }

    private List<IvrAndSmsStatisticDto> executeQueryWithParams(Query query, Range<DateTime> dateRange) {
        query.setResultClass(IvrAndSmsStatisticDto.class);

        Map<String, String> params = new HashMap<>();
        params.put("minDate", dateRange.getMin().toString(DATE_TIME_FORMAT));
        params.put("maxDate", dateRange.getMax().toString(DATE_TIME_FORMAT));

        @SuppressWarnings("unchecked")
        List<IvrAndSmsStatisticDto> list = (List<IvrAndSmsStatisticDto>) query.executeWithMap(params);

        if (list == null || list.isEmpty()) {
            return null;
        }

        return list;
    }

    private List<IvrAndSmsStatisticDto> getStatisticForQuery(final Range<DateTime> dateRange, final String query) {
        if (dateRange == null || dateRange.getMin() == null || dateRange.getMax() == null) {
            return null;
        }

        return ivrAndSmsStatisticReportDataService.executeSQLQuery(new SqlQueryExecution<List<IvrAndSmsStatisticDto>>() {
            @Override
            public List<IvrAndSmsStatisticDto> execute(Query query) {
                return executeQueryWithParams(query, dateRange);
            }

            @Override
            public String getSqlQuery() {
                return query;
            }
        });
    }
}
