package org.motechproject.ebodac.web;

import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.EmailRecipient;
import org.motechproject.ebodac.domain.EmailReport;
import org.motechproject.ebodac.domain.EmailReportDto;
import org.motechproject.ebodac.repository.EmailRecipientDataService;
import org.motechproject.ebodac.service.EmailReportService;
import org.motechproject.scheduler.exception.MotechSchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.jdo.JDODataStoreException;
import java.util.List;

@Controller
@PreAuthorize(EbodacConstants.HAS_EMAIL_REPORTS_TAB_ROLE)
public class EmailReportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailReportController.class);

    @Autowired
    private EmailReportService emailReportService;

    @Autowired
    private EmailRecipientDataService emailRecipientDataService;

    @RequestMapping(value = "/sendEmailReport", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> sendEmailReport(@RequestBody String reportId) {
        try {
            emailReportService.sendEmailReport(Long.valueOf(reportId));
        } catch (IllegalArgumentException e) {
            LOGGER.error("Report id is not a number", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOGGER.error("Fatal error raised during creating reports", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/getEmailReports", method = RequestMethod.GET)
    @ResponseBody
    public List<EmailReport> getEmailReports() {
        return emailReportService.getEmailReports();
    }

    @RequestMapping(value = "/saveReport", method = RequestMethod.POST)
    @ResponseBody
    public EmailReport saveReport(@RequestBody EmailReportDto report) {
        return emailReportService.saveReport(report);
    }

    @RequestMapping(value = "/deleteReport", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> deleteReport(@RequestBody String reportId) {
        try {
            emailReportService.deleteReport(Long.valueOf(reportId));
        } catch (IllegalArgumentException e) {
            LOGGER.error("Report id is not a number", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOGGER.error("Fatal error raised during deleting report", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/getEmailRecipients", method = RequestMethod.GET)
    @ResponseBody
    public List<EmailRecipient> getEmailRecipients() {
        return emailRecipientDataService.retrieveAll();
    }

    @RequestMapping(value = "/addRecipient", method = RequestMethod.POST)
    @ResponseBody
    public EmailRecipient addRecipient(@RequestBody EmailRecipient recipient) {
        return emailRecipientDataService.create(recipient);
    }

    @ExceptionHandler(MotechSchedulerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(MotechSchedulerException e) {
        LOGGER.error(e.getMessage(), e);
        return "Cannot schedule job for email report, check the log for more details";
    }

    @ExceptionHandler(JDODataStoreException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(JDODataStoreException e) {
        LOGGER.error(e.getMessage(), e);
        if (e.getMessage().contains("Duplicate entry")) {
            return "Recipient with this email already exist";
        }
        return e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return e.getMessage();
    }
}
