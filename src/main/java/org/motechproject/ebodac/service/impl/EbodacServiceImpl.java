package org.motechproject.ebodac.service.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.ebodac.client.EbodacFtpsClient;
import org.motechproject.ebodac.client.EbodacHttpClient;
import org.motechproject.ebodac.client.FtpException;
import org.motechproject.ebodac.client.HttpResponse;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.EbodacService;
import org.motechproject.ebodac.service.RaveImportService;
import org.motechproject.ebodac.service.ReportService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.ebodac.util.JsonUtils;
import org.motechproject.mds.ex.csv.CsvImportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Simple implementation of the {@link org.motechproject.ebodac.service.EbodacService} interface.
 */
@Service("ebodacService")
public class EbodacServiceImpl implements EbodacService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EbodacServiceImpl.class);

    private SubjectService subjectService;

    private EbodacHttpClient ebodacHttpClient;

    private RaveImportService raveImportService;

    private ConfigService configService;

    private ReportService reportService;

    @Override
    public void sendUpdatedSubjects(String zetesUrl, String username, String password) {
        LOGGER.info("Sending updated subjects to zetes. Job started at {}", DateTime.now());

        List<Subject> modifiedSubjects = subjectService.findModifiedSubjects();
        for (Subject s : modifiedSubjects) {
            String json = JsonUtils.convertSubjectForZetes(s);
            if (json != null) {
                HttpResponse response = ebodacHttpClient.sendJson(zetesUrl, json, username, password);
                if (response == null) {
                    LOGGER.error("Skipping subject due to HttpClient failure. Subject id: {}", s.getSubjectId());
                } else if (response.getStatus() != HttpStatus.SC_NO_CONTENT) {
                    LOGGER.error("Failed to update the subject with id: {}. Response from Zetes (status {}):\n{}",
                            s.getSubjectId(), response.getStatus(), parseZetesResponse(response));
                } else {
                    // the subject has been updated successfully
                    s.setChanged(false);
                    subjectService.update(s, true);
                    LOGGER.debug("Update to Zetes was successful. Subject id: {}: \n", s.getSubjectId(), s.toString());
                }
            } else {
                LOGGER.error("Skipping subject due to json processing exception. Subject id: {}", s.getSubjectId());
            }
        }
        LOGGER.info("Zetes update job finished at {}", DateTime.now());
    }

    @Override
    public void fetchCSVUpdates() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(EbodacConstants.CSV_DATE_FORMAT);
        Config config = configService.getConfig();
        String lastCsvUpdate = config.getLastCsvUpdate();
        DateTime afterDate;
        if (StringUtils.isNotBlank(lastCsvUpdate)) {
            afterDate = dateTimeFormatter.parseDateTime(config.getLastCsvUpdate());
        } else {
            afterDate = new DateTime(new Date(0));
        }
        String hostname = config.getFtpsHost();
        String username = config.getFtpsUsername();
        String password = config.getFtpsPassword();
        String directory = config.getFtpsDirectory();
        Integer port = Integer.parseInt(config.getFtpsPort());

        LOGGER.info("Started fetching CSV files modified after {} from {}", afterDate, hostname);
        EbodacFtpsClient ftpsClient = new EbodacFtpsClient();
        try {
            ftpsClient.connect(hostname, port, username, password);
        } catch (FtpException e) {
            LOGGER.error("Could not connect to RAVE FTPS: " + e.getMessage(), e);
            return;
        }
        List<String> filenames;
        try {
            filenames = ftpsClient.listFiles(directory);
        } catch (FtpException e) {
            LOGGER.error("Could not list files: " + e.getMessage(), e);
            return;
        }
        DateTime lastUpdated;
        if (StringUtils.isNotBlank(lastCsvUpdate)) {
            lastUpdated = dateTimeFormatter.parseDateTime(config.getLastCsvUpdate());
        } else {
            lastUpdated = new DateTime(new Date(0));
        }
        for (String filename: filenames) {
            Matcher m = EbodacConstants.CSV_FILENAME_PATTERN.matcher(filename);
            if (!m.matches()) {
                LOGGER.error("Skipping " + filename + " because the filename does not match specified format");
            } else {
                try {
                    LOGGER.debug("Parsing file {}", filename);
                    DateTime date = dateTimeFormatter.parseDateTime(m.group(1));
                    if (date.isAfter(afterDate)) {
                        OutputStream outputStream = new ByteArrayOutputStream();
                        ftpsClient.fetchFile(directory + filename, outputStream);
                        raveImportService.importCsv(new StringReader(outputStream.toString()));
                        if (date.isAfter(lastUpdated)) {
                            lastUpdated = date;
                        }
                    }
                } catch (IllegalArgumentException e) {
                    LOGGER.error("Could not parse date: " + e.getMessage(), e);
                } catch (FtpException e) {
                    LOGGER.error("Could not fetch file " + filename + ": " + e.getMessage(), e);
                } catch (CsvImportException e) {
                    LOGGER.error("Could not import CSV " + filename + ": " + e.getMessage(), e);
                }
            }
        }
        config.setLastCsvUpdate(lastUpdated.toString(dateTimeFormatter));
        configService.updateConfig(config);
        LOGGER.info("Finished fetching CSV files from {}", hostname);
    }

    @Override
    public void generateDailyReport() {
        DateTimeFormatter formatter = DateTimeFormat.mediumDate();
        DateTime now = formatter.parseDateTime(DateTime.now().toString(formatter));

        Config config = configService.getConfig();

        String lastReportDateString = config.getLastReportDate();
        DateTime lastReportDate;

        if (StringUtils.isNotBlank(lastReportDateString)) {
            lastReportDate = formatter.parseDateTime(config.getLastReportDate());
        } else {
            lastReportDate = formatter.parseDateTime(EbodacConstants.LAST_REPORT_DEFAULT_DATE);
        }

        for(DateTime date = lastReportDate.plusDays(1); date.isBefore(now); date = date.plusDays(1)) {
            reportService.generatePrimerVaccinationReport(subjectService.findSubjectsPrimerVaccinatedAtDay(date), date);
            reportService.generateBoosterVaccinationReport(subjectService.findSubjectsBoosterVaccinatedAtDay(date), date);
        }


        config.setLastReportDate(DateTime.now().minusDays(1).toString(formatter));
        configService.updateConfig(config);
    }

    private String parseZetesResponse(HttpResponse httpResponse) {
        int status = httpResponse.getStatus();
        if (status == HttpStatus.SC_NOT_FOUND) {
            return "Invalid reverse proxy url";
        } else if (status == HttpStatus.SC_UNAUTHORIZED) {
            return "Bad authentication";
        } else {
            String response = httpResponse.getResponseBody();
            if (StringUtils.isEmpty(response)) {
                return "Empty response body with status different than 204";
            }
            if (httpResponse.getContentType().equals("application/json")) {
                try {
                    JsonElement jsonElement = new JsonParser().parse(response);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.has("message")) {
                        return jsonObject.get("message").getAsString();
                    } else {
                        return jsonObject.getAsString();
                    }
                } catch (JsonSyntaxException e) {
                    LOGGER.error("Could not parse JSON response from Zetes");
                }
            } else if (response.contains("JsonParseException")) {
                return "Invalid JSON syntax";
            }
            return response;
        }
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @Autowired
    public void setEbodacHttpClient(EbodacHttpClient ebodacHttpClient) {
        this.ebodacHttpClient = ebodacHttpClient;
    }

    @Autowired
    public void setRaveImportService(RaveImportService raveImportService) {
        this.raveImportService = raveImportService;
    }

    @Autowired
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    @Autowired
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
}
