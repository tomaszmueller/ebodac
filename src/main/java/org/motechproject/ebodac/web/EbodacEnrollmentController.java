package org.motechproject.ebodac.web;

import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.exception.EbodacEnrollmentException;
import org.motechproject.ebodac.exception.EbodacUnenrollmentException;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.service.VisitService;
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

@Controller
public class EbodacEnrollmentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EbodacEnrollmentController.class);

    @Autowired
    private EbodacEnrollmentService ebodacEnrollmentService;

    @Autowired
    private VisitService visitService;

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
                return new ResponseEntity<>("Cannot find visit in database", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (existingVisit.getDate() != null) {
                return new ResponseEntity<>("Cannot re-enroll subject for that visit, because visit already took place", HttpStatus.BAD_REQUEST);
            } else if (visit.getMotechProjectedDate() == null) {
                return new ResponseEntity<>("Cannot re-enroll subject for that visit, because motech projected date is null", HttpStatus.BAD_REQUEST);
            } else if (visit.getMotechProjectedDate().isEqual(existingVisit.getMotechProjectedDate())) {
                return new ResponseEntity<>("Cannot re-enroll subject for that visit, because motech projected date wasn't changed", HttpStatus.BAD_REQUEST);
            } else if (visit.getMotechProjectedDate().isBeforeNow()) {
                return new ResponseEntity<>("Cannot re-enroll subject for that visit, because motech projected date is in the past", HttpStatus.BAD_REQUEST);
            } else {

                try {
                    ebodacEnrollmentService.reenrollSubject(visit);
                } catch (EbodacEnrollmentException e) {
                    LOGGER.debug(e.getMessage(), e);
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } catch (EbodacUnenrollmentException e) {
                    LOGGER.debug(e.getMessage(), e);
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
