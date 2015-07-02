package org.motechproject.ebodac.web;

import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.service.impl.csv.SubjectCsvImportCustomizer;
import org.motechproject.ebodac.service.impl.csv.VisitCsvExportCustomizer;
import org.motechproject.mds.dto.CsvImportResults;
import org.motechproject.mds.ex.csv.CsvImportException;
import org.motechproject.mds.service.CsvImportExportService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.util.Constants;
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
    public void exportEntityInstances(@PathVariable Long entityId, HttpServletResponse response) throws IOException {
        final String fileName = "Entity_" + entityId + "_instances";

        response.setContentType("text/csv");
        response.setCharacterEncoding(UTF_8);
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=" + fileName + ".csv");

        String className = entityService.getEntity(entityId).getClassName();
        if (className.equals(Visit.class.getName())) {
            csvImportExportService.exportCsv(entityId, response.getWriter(), visitCsvExportCustomizer);
        } else if (className.equals(Subject.class.getName())) {
            csvImportExportService.exportCsv(entityId, response.getWriter());
        }
    }
}
