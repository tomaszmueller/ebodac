package org.motechproject.ebodac.web;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.DateFilter;
import org.motechproject.ebodac.domain.IvrAndSmsStatisticGraphsDto;
import org.motechproject.ebodac.domain.IvrAndSmsStatisticTablesDto;
import org.motechproject.ebodac.domain.IvrEngagementStatistic;
import org.motechproject.ebodac.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
public class StatisticController {

    private static final LocalTime END_OF_DAY_TIME = new LocalTime(23, 59, 59);

    @Autowired
    private StatisticService statisticService;

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/statistic/table/ivr", method = RequestMethod.POST)
    @ResponseBody
    public IvrAndSmsStatisticTablesDto getStatisticForIvrTable(DateFilter dateFilter, String startDate, String endDate) throws IOException {
        return new IvrAndSmsStatisticTablesDto(statisticService.getStatisticForIvr(getDateRangeFromFilter(dateFilter, startDate, endDate)));
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/statistic/table/sms", method = RequestMethod.POST)
    @ResponseBody
    public IvrAndSmsStatisticTablesDto getStatisticForSmsTable(DateFilter dateFilter, String startDate, String endDate) throws IOException {
        return new IvrAndSmsStatisticTablesDto(statisticService.getStatisticForSms(getDateRangeFromFilter(dateFilter, startDate, endDate)));
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/statistic/graphs/ivr", method = RequestMethod.POST)
    @ResponseBody
    public IvrAndSmsStatisticGraphsDto getStatisticForIvrGraphs(DateFilter dateFilter, String startDate, String endDate) throws IOException {
        return new IvrAndSmsStatisticGraphsDto(statisticService.getStatisticForIvr(getDateRangeFromFilter(dateFilter, startDate, endDate)));
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/statistic/graphs/sms", method = RequestMethod.POST)
    @ResponseBody
    public IvrAndSmsStatisticGraphsDto getStatisticForSmsGraphs(DateFilter dateFilter, String startDate, String endDate) throws IOException {
        return new IvrAndSmsStatisticGraphsDto(statisticService.getStatisticForSms(getDateRangeFromFilter(dateFilter, startDate, endDate)));
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/getIvrEngagementStatistic", method = RequestMethod.POST)
    @ResponseBody
    public List<IvrEngagementStatistic> getIvrEngagementStatistic() throws IOException {
        return statisticService.getIvrEngagementStatistic();
    }

    private Range<DateTime> getDateRangeFromFilter(DateFilter dateFilter, String startDate, String endDate) {
        if (dateFilter == null) {
            return null;
        }

        if (!DateFilter.DATE_RANGE.equals(dateFilter)) {
            return new Range<>(dateFilter.getRange().getMin().toDateTimeAtStartOfDay(),
                    dateFilter.getRange().getMax().toDateTime(END_OF_DAY_TIME));
        }

        LocalDate minDate = null;
        LocalDate maxDate = null;

        if (StringUtils.isNotBlank(startDate)) {
            minDate = LocalDate.parse(startDate, DateTimeFormat.forPattern(EbodacConstants.DATE_PICKER_DATE_FORMAT));
        }
        if (StringUtils.isNotBlank(endDate)) {
            maxDate = LocalDate.parse(endDate, DateTimeFormat.forPattern(EbodacConstants.DATE_PICKER_DATE_FORMAT));
        }

        return new Range<>(minDate != null ? minDate.toDateTimeAtStartOfDay() : null,
                maxDate != null ? maxDate.toDateTime(END_OF_DAY_TIME) : null);
    }
}
