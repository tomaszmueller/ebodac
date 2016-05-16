package org.motechproject.ebodac.service.impl;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.IvrAndSmsStatistic;
import org.motechproject.ebodac.domain.IvrEngagementStatistic;
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
    public List<IvrAndSmsStatistic> getStatisticForIvr(final Range<DateTime> dateRange) throws IOException {
        return getStatisticForQuery(dateRange, IOUtils.toString(getClass().getResourceAsStream("/sql/ivr_statistic_query.sql")));
    }

    @Override
    public List<IvrAndSmsStatistic> getStatisticForSms(final Range<DateTime> dateRange) throws IOException {
        return getStatisticForQuery(dateRange, IOUtils.toString(getClass().getResourceAsStream("/sql/sms_statistic_query.sql")));
    }

    @Override
    public List<IvrEngagementStatistic> getIvrEngagementStatistic() throws IOException {
        final String query = IOUtils.toString(getClass().getResourceAsStream("/sql/ivr_engagement_statistic_query.sql"));

        return ivrAndSmsStatisticReportDataService.executeSQLQuery(new SqlQueryExecution<List<IvrEngagementStatistic>>() {

            @SuppressWarnings("unchecked")
            @Override
            public List<IvrEngagementStatistic> execute(Query query) {
                query.setResultClass(IvrEngagementStatistic.class);

                return (List<IvrEngagementStatistic>) query.execute();
            }

            @Override
            public String getSqlQuery() {
                return query;
            }
        });
    }

    private List<IvrAndSmsStatistic> executeQueryWithParams(Query query, Range<DateTime> dateRange) {
        query.setResultClass(IvrAndSmsStatistic.class);

        Map<String, String> params = new HashMap<>();
        params.put("minDate", dateRange.getMin().toString(DATE_TIME_FORMAT));
        params.put("maxDate", dateRange.getMax().toString(DATE_TIME_FORMAT));

        @SuppressWarnings("unchecked")
        List<IvrAndSmsStatistic> list = (List<IvrAndSmsStatistic>) query.executeWithMap(params);

        if (list == null || list.isEmpty()) {
            return null;
        }

        return list;
    }

    private List<IvrAndSmsStatistic> getStatisticForQuery(final Range<DateTime> dateRange, final String query) {
        if (dateRange == null || dateRange.getMin() == null || dateRange.getMax() == null) {
            return null;
        }

        return ivrAndSmsStatisticReportDataService.executeSQLQuery(new SqlQueryExecution<List<IvrAndSmsStatistic>>() {
            @Override
            public List<IvrAndSmsStatistic> execute(Query query) {
                return executeQueryWithParams(query, dateRange);
            }

            @Override
            public String getSqlQuery() {
                return query;
            }
        });
    }
}
