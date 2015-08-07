package org.motechproject.ebodac.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.LocalDate;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.SubjectEnrollments;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.exception.EbodacEnrollmentException;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.service.VisitService;
import org.motechproject.ebodac.web.domain.GridSettings;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
public class EbodacEnrollmentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EbodacEnrollmentController.class);

    @Autowired
    private EbodacEnrollmentService ebodacEnrollmentService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/reenrollSubject", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> reenrollVisit(@RequestBody Visit visit) {

        if (visit == null) {
            return new ResponseEntity<>("Visit cannot be null", HttpStatus.BAD_REQUEST);
        } else if(visit.getSubject() == null) {
            return new ResponseEntity<>("Visit must have Subject to be enrolled", HttpStatus.BAD_REQUEST);
        } else {
            Visit existingVisit = visitService.findVisitBySubjectIdAndVisitType(visit.getSubject().getSubjectId(), visit.getType());

            if (existingVisit == null) {
                return new ResponseEntity<>("Cannot find visit in the database", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (existingVisit.getDate() != null) {
                return new ResponseEntity<>("Cannot re-enroll Subject for that Visit, because visit already took place", HttpStatus.BAD_REQUEST);
            } else if (visit.getMotechProjectedDate() == null) {
                return new ResponseEntity<>("Cannot re-enroll Subject for that Visit, because motech projected date is null", HttpStatus.BAD_REQUEST);
            } else if (visit.getMotechProjectedDate().equals(existingVisit.getMotechProjectedDate())) {
                return new ResponseEntity<>("Cannot re-enroll Subject for that Visit, because motech projected date wasn't changed", HttpStatus.BAD_REQUEST);
            } else if (visit.getMotechProjectedDate().isBefore(LocalDate.now())) {
                return new ResponseEntity<>("Cannot re-enroll Subject for that Visit, because motech projected date is in the past", HttpStatus.BAD_REQUEST);
            } else {

                try {
                    ebodacEnrollmentService.reenrollSubject(visit);
                } catch (EbodacEnrollmentException e) {
                    LOGGER.debug(e.getMessage(), e);
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/availableCampaigns", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getAvailableCampaigns() {
        return EbodacConstants.AVAILABLE_CAMPAIGNS;
    }

    @PreAuthorize("hasAnyRole('mdsDataAccess', 'manageEbodac')")
    @RequestMapping(value = "/getEnrollments", method = RequestMethod.POST)
    @ResponseBody
    public Records<?> getEnrollments(GridSettings settings) throws IOException {
        Order order = null;
        if (!settings.getSortColumn().isEmpty()) {
            order = new Order(settings.getSortColumn(), settings.getSortDirection());
        }

        QueryParams queryParams = new QueryParams(settings.getPage(), settings.getRows(), order);

        long recordCount;
        int rowCount;

        recordCount = subjectEnrollmentsDataService.count();
        rowCount = (int) Math.ceil(recordCount / (double) settings.getRows());

        List<SubjectEnrollments> enrollments = subjectEnrollmentsDataService.retrieveAll(queryParams);

        return new Records<>(settings.getPage(), rowCount, (int) recordCount, enrollments);
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/enrollSubject", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> enrollSubject(@RequestBody String subjectId) {
        if (subjectId == null) {
            return new ResponseEntity<>("Subject id cannot be empty", HttpStatus.BAD_REQUEST);
        }

        try {
            ebodacEnrollmentService.enrollSubject(subjectId);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/unenrollSubject", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> unenrollSubject(@RequestBody String subjectId) {
        if (subjectId == null) {
            return new ResponseEntity<>("Subject id cannot be empty", HttpStatus.BAD_REQUEST);
        }

        try {
            ebodacEnrollmentService.unenrollSubject(subjectId);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
