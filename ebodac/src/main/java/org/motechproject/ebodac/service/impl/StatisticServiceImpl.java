package org.motechproject.ebodac.service.impl;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.IvrEngagementStatistic;
import org.motechproject.ebodac.domain.IvrAndSmsStatistic;
import org.motechproject.ebodac.repository.IvrAndSmsStatisticReportDataService;
import org.motechproject.ebodac.service.StatisticService;
import org.motechproject.mds.query.SqlQueryExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jdo.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("statisticService")
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private IvrAndSmsStatisticReportDataService ivrAndSmsStatisticReportDataService;

    private static final String IVR_STATISTIC_QUERY = "SELECT count(EBODAC_MODULE_IVRANDSMSSTATISTICREPORT.id) as 'totalAmount', 0 as 'totalPending', " +
            "SUM(if(receivedDate is null, 1, 0)) as 'totalFailed', SUM(if(receivedDate is not null, 1, 0)) as 'totalSucceed', " +
            "SUM(if(gender = 'Male', 1, 0)) as 'sendToMen', SUM(if(gender = 'Female', 1, 0)) as 'sendToWomen' " +
            "FROM EBODAC_MODULE_IVRANDSMSSTATISTICREPORT left join EBODAC_MODULE_SUBJECT on EBODAC_MODULE_IVRANDSMSSTATISTICREPORT.subject_id_OID = EBODAC_MODULE_SUBJECT.id " +
            "WHERE sendDate >= :minDate AND sendDate <= :maxDate";

    private static final String SMS_STATISTIC_QUERY = "SELECT count(EBODAC_MODULE_IVRANDSMSSTATISTICREPORT.id) as 'totalAmount', " +
            "SUM(if(smsStatus = 'YES' && smsReceivedDate is null, 1, 0)) as 'totalPending', SUM(if(smsStatus = 'FAIL', 1, 0)) as 'totalFailed', " +
            "SUM(if(smsStatus = 'YES' && smsReceivedDate is not null, 1, 0)) as 'totalSucceed', SUM(if(gender = 'Male', 1, 0)) as 'sendToMen', " +
            "SUM(if(gender = 'Female', 1, 0)) as 'sendToWomen' FROM EBODAC_MODULE_IVRANDSMSSTATISTICREPORT " +
            "left join EBODAC_MODULE_SUBJECT on EBODAC_MODULE_IVRANDSMSSTATISTICREPORT.subject_id_OID = EBODAC_MODULE_SUBJECT.id " +
            "WHERE smsStatus != 'NO' AND sendDate >= :minDate AND sendDate <= :maxDate";

    private static final String IVR_ENGAGEMENT_STATISTIC_QUERY = "SELECT subjectId, count(subjectId) as 'callsExpected', " +
            "count(subjectId) as 'pushedSuccessfully', SUM(if(receivedDate is not null, 1, 0)) as 'received', " +
            "SUM(if(messagePercentListened >= 50, 1, 0)) as 'activelyListened', SUM(if(receivedDate is null, 1, 0)) as 'failed' " +
            "FROM EBODAC_MODULE_IVRANDSMSSTATISTICREPORT left join EBODAC_MODULE_SUBJECT on EBODAC_MODULE_IVRANDSMSSTATISTICREPORT.subject_id_OID = EBODAC_MODULE_SUBJECT.id " +
            "GROUP BY subjectId";

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    public IvrAndSmsStatistic getStatisticForIvr(final Range<DateTime> dateRange) {
        if (dateRange == null || dateRange.getMin() == null || dateRange.getMax() == null) {
            return null;
        }

        return ivrAndSmsStatisticReportDataService.executeSQLQuery(new SqlQueryExecution<IvrAndSmsStatistic>() {

            @Override
            public IvrAndSmsStatistic execute(Query query) {
                query.setResultClass(IvrAndSmsStatistic.class);

                Map<String, String> params = new HashMap<>();
                params.put("minDate", dateRange.getMin().toString(DATE_TIME_FORMAT));
                params.put("maxDate", dateRange.getMax().toString(DATE_TIME_FORMAT));

                List<IvrAndSmsStatistic> list = (List<IvrAndSmsStatistic>) query.executeWithMap(params);
                if (list == null || list.isEmpty()) {
                    return null;
                }
                return list.get(0);
            }

            @Override
            public String getSqlQuery() {
                return IVR_STATISTIC_QUERY;
            }
        });
    }

    @Override
    public IvrAndSmsStatistic getStatisticForSms(final Range<DateTime> dateRange) {
        if (dateRange == null || dateRange.getMin() == null || dateRange.getMax() == null) {
            return null;
        }

        return ivrAndSmsStatisticReportDataService.executeSQLQuery(new SqlQueryExecution<IvrAndSmsStatistic>() {

            @Override
            public IvrAndSmsStatistic execute(Query query) {
                query.setResultClass(IvrAndSmsStatistic.class);

                Map<String, String> params = new HashMap<>();
                params.put("minDate", dateRange.getMin().toString(DATE_TIME_FORMAT));
                params.put("maxDate", dateRange.getMax().toString(DATE_TIME_FORMAT));

                List<IvrAndSmsStatistic> list = (List<IvrAndSmsStatistic>) query.executeWithMap(params);
                if (list == null || list.isEmpty()) {
                    return null;
                }
                return list.get(0);
            }

            @Override
            public String getSqlQuery() {
                return SMS_STATISTIC_QUERY;
            }
        });
    }

    @Override
    public List<IvrEngagementStatistic> getIvrEngagementStatistic() {
        return ivrAndSmsStatisticReportDataService.executeSQLQuery(new SqlQueryExecution<List<IvrEngagementStatistic>>() {

            @Override
            public List<IvrEngagementStatistic> execute(Query query) {
                query.setResultClass(IvrEngagementStatistic.class);

                return (List<IvrEngagementStatistic>) query.execute();
            }

            @Override
            public String getSqlQuery() {
                return IVR_ENGAGEMENT_STATISTIC_QUERY;
            }
        });
    }
}
