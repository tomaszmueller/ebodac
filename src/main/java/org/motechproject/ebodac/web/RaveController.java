package org.motechproject.ebodac.web;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.mds.util.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;

import org.motechproject.ebodac.service.EbodacService;

import java.io.IOException;
import java.util.Date;

@Controller
public class RaveController {

    @Autowired
    private EbodacService ebodacService;

    @Autowired
    private ConfigService configService;

    @RequestMapping(value = "/fetch-csv", method = RequestMethod.POST)
    @PreAuthorize(Constants.Roles.HAS_DATA_ACCESS)
    @ResponseBody
    public ResponseEntity<String> fetchCsv() throws IOException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(EbodacConstants.CSV_DATE_FORMAT);
        Config config = configService.getConfig();
        String lastCsvUpdate = config.getLastCsvUpdate();
        DateTime afterDate;
        if (StringUtils.isNotBlank(lastCsvUpdate)) {
            afterDate = dateTimeFormatter.parseDateTime(config.getLastCsvUpdate());
        } else {
            afterDate = new DateTime(new Date(0));
        }
        Integer port = Integer.parseInt(config.getSftpPort());
        ebodacService.fetchCSVUpdates(config.getSftpHost(), port, config.getSftpUsername(),
                config.getSftpPassword(), config.getSftpDirectory(), afterDate);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
