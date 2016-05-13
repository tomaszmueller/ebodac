package org.motechproject.ebodac.web;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.DateFilter;
import org.motechproject.ebodac.domain.IvrAndSmsStatisticGraphsDto;
import org.motechproject.ebodac.domain.IvrAndSmsStatisticTablesDto;
import org.motechproject.ebodac.domain.IvrEngagementStatistic;
import org.motechproject.ebodac.exception.EbodacExportException;
import org.motechproject.ebodac.exception.EbodacLookupException;
import org.motechproject.ebodac.service.ExportService;
import org.motechproject.ebodac.service.StatisticService;
import org.motechproject.ebodac.template.PdfReportCTemplate;
import org.motechproject.ebodac.template.XlsReportCTemplate;
import org.motechproject.ebodac.util.ExcelTableWriter;
import org.motechproject.ebodac.util.PdfTableWriter;
import org.motechproject.mds.service.impl.csv.writer.CsvTableWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.CharEncoding.UTF_8;

@Controller
public class StatisticController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticController.class);
    private static final LocalTime END_OF_DAY_TIME = new LocalTime(23, 59, 59);

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private ExportService exportService;

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

    @RequestMapping(value = "/statistic/export/{entityName}", method = RequestMethod.GET)
    public void exportStatisticEntities(@PathVariable String entityName, String outputFormat, DateFilter dateFilter,
                                        String startDate, String endDate, HttpServletResponse response) throws IOException {
        Range<DateTime> range = getDateRangeFromFilter(dateFilter, startDate, endDate);

        if (EbodacConstants.IVR_KPI_NAME.equals(entityName)) {
            exportEntity(outputFormat, response, EbodacConstants.IVR_KPI_NAME, statisticService.getStatisticForIvr(range), EbodacConstants.IVR_KPI_MAP);
        } else if (EbodacConstants.SMS_KPI_NAME.equals(entityName)) {
            exportEntity(outputFormat, response, EbodacConstants.SMS_KPI_NAME, statisticService.getStatisticForSms(range), EbodacConstants.SMS_KPI_MAP);
        } else {
            exportEntity(outputFormat, response, EbodacConstants.IVR_ENGAGEMENT_NAME, statisticService.getIvrEngagementStatistic(), EbodacConstants.IVR_ENGAGEMENT_MAP);
        }
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return e.getMessage();
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

    private void exportEntity(String outputFormat, HttpServletResponse response, String fileNameBeginning,
                              List entities, Map<String, String> headerMap) throws IOException {

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        final String fileName = fileNameBeginning + "_" + DateTime.now().toString(dateTimeFormatter);
        List entityList = new ArrayList();

        if (EbodacConstants.PDF_EXPORT_FORMAT.equals(outputFormat)) {
            response.setContentType("application/pdf");
        } else if (EbodacConstants.CSV_EXPORT_FORMAT.equals(outputFormat)) {
            response.setContentType("text/csv");
        } else if (EbodacConstants.XLS_EXPORT_FORMAT.equals(outputFormat)) {
            response.setContentType("application/vnd.ms-excel");
        } else {
            throw new IllegalArgumentException("Invalid export format: " + outputFormat);
        }
        response.setCharacterEncoding(UTF_8);
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=" + fileName + "." + outputFormat.toLowerCase());

        if (entities != null) {
            entityList = entities;
        }

        try {
            if (EbodacConstants.PDF_EXPORT_FORMAT.equals(outputFormat)) {
                exportService.exportEntity(entityList, headerMap, new PdfTableWriter(new PdfReportCTemplate(response.getOutputStream())));
            } else if (EbodacConstants.CSV_EXPORT_FORMAT.equals(outputFormat)) {
                exportService.exportEntity(entityList, headerMap, new CsvTableWriter(response.getWriter()));
            } else if (EbodacConstants.XLS_EXPORT_FORMAT.equals(outputFormat)) {
                exportService.exportEntity(entityList, headerMap, new ExcelTableWriter(new XlsReportCTemplate(response.getOutputStream())));
            }
        } catch (IOException | EbodacLookupException | EbodacExportException e) {
            LOGGER.debug(e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
