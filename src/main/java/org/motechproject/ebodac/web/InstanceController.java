package org.motechproject.ebodac.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.service.impl.csv.SubjectCsvImportCustomizer;
import org.motechproject.ebodac.service.impl.csv.VisitCsvExportCustomizer;
import org.motechproject.ebodac.web.domain.GridSettings;
import org.motechproject.mds.dto.CsvImportResults;
import org.motechproject.mds.ex.csv.CsvImportException;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.CsvImportExportService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.CharEncoding.UTF_8;

@Controller
public class InstanceController {

    @Autowired
    private CsvImportExportService csvImportExportService;

    @Autowired
    private SubjectCsvImportCustomizer subjectCsvImportCustomizer;

    @Autowired
    private VisitCsvExportCustomizer visitCsvExportCustomizer;

    @Autowired
    private EntityService entityService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(value = "/instances/{entityId}/csvimport", method = RequestMethod.POST)
    @PreAuthorize(Constants.Roles.HAS_DATA_ACCESS)
    @ResponseBody
    public long importCsv(@PathVariable long entityId, @RequestParam(required = true) MultipartFile csvFile) {
        try {
            try (InputStream in = csvFile.getInputStream()) {
                Reader reader = new InputStreamReader(in);
                CsvImportResults results = csvImportExportService.importCsv(entityId, reader,
                        csvFile.getOriginalFilename(), subjectCsvImportCustomizer);
                return results.totalNumberOfImportedInstances();
            }
        } catch (IOException e) {
            throw new CsvImportException("Unable to open uploaded file", e);
        }
    }

    @RequestMapping(value = "/entities/{entityId}/exportInstances", method = RequestMethod.GET)
    public void exportEntityInstances(@PathVariable Long entityId, GridSettings settings,
                                      @RequestParam String range,
                                      @RequestParam String outputFormat,
                                      HttpServletResponse response) throws IOException {
        if (!Constants.ExportFormat.isValidFormat(outputFormat)) {
            throw new IllegalArgumentException("Invalid export format: " + outputFormat);
        }

        final String fileName = "Entity_" + entityId + "_instances";

        if (Constants.ExportFormat.PDF.equals(outputFormat)) {
            response.setContentType("application/pdf");
        } else {
            response.setContentType("text/csv");
        }
        response.setCharacterEncoding(UTF_8);
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=" + fileName + "." + outputFormat.toLowerCase());

        String className = entityService.getEntity(entityId).getClassName();
        if ("table".equalsIgnoreCase(range)) {
            Order order = null;
            if (!settings.getSortColumn().isEmpty()) {
                order = new Order(settings.getSortColumn(), settings.getSortDirection());
            }

            QueryParams queryParams = new QueryParams(settings.getPage(), settings.getRows(), order);
            String lookup = settings.getLookup();

            if (Constants.ExportFormat.PDF.equals(outputFormat)) {
                if (className.equals(Visit.class.getName())) { // if className is Visit, format is PDF and range is table
                    csvImportExportService.exportPdf(entityId, response.getOutputStream(), lookup, queryParams,
                            settings.getSelectedFields(), getFields(settings), visitCsvExportCustomizer);
                } else {
                    csvImportExportService.exportPdf(entityId, response.getOutputStream(), lookup, queryParams,
                            settings.getSelectedFields(), getFields(settings));
                }
            } else {
                if (className.equals(Visit.class.getName())) { // if className is Visit, format is CSV and range is table
                    csvImportExportService.exportCsv(entityId, response.getWriter(), lookup, queryParams,
                            settings.getSelectedFields(), getFields(settings), visitCsvExportCustomizer);
                } else {
                    csvImportExportService.exportCsv(entityId, response.getWriter(), lookup, queryParams,
                            settings.getSelectedFields(), getFields(settings));
                }
            }
        } else if ("all".equalsIgnoreCase(range)) {
            if (Constants.ExportFormat.PDF.equals(outputFormat)) {
                if (className.equals(Visit.class.getName())) { // if className is Visit, format is PDF and range is all
                    csvImportExportService.exportPdf(entityId, response.getOutputStream(), visitCsvExportCustomizer);
                } else {
                    csvImportExportService.exportPdf(entityId, response.getOutputStream());
                }
            } else {
                if (className.equals(Visit.class.getName())) { // if className is Visit, format is CSV and range is all
                    csvImportExportService.exportCsv(entityId, response.getWriter(), visitCsvExportCustomizer);
                } else {
                    csvImportExportService.exportCsv(entityId, response.getWriter());
                }
            }
        }
    }

    private Map<String, Object> getFields(GridSettings gridSettings) throws IOException {
        return objectMapper.readValue(gridSettings.getFields(), new TypeReference<HashMap>() {
        });
    }
}
