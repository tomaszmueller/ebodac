package org.motechproject.ebodac.web;

import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.ebodac.validation.ValidationError;
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

import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/**
 * Web API for Subject Registration
 */
@RequestMapping("/registration")
@Controller
public class SubjectController {

    @Autowired
    SubjectService subjectService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SubjectController.class);

    @PreAuthorize("hasAnyRole('manageBundles', 'registrationSubmission')")
    @RequestMapping(value = "/submit", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<List<String>> submitSubjectRequest (@RequestBody SubmitSubjectRequest submitSubjectRequest) {

        List<ValidationError> errorList;

        errorList = submitSubjectRequest.validate();

        if (errorList.isEmpty()) {
            Subject subject = new Subject(submitSubjectRequest.getSubjectId(), submitSubjectRequest.getName(),
                    submitSubjectRequest.getHouseholdName(), submitSubjectRequest.getHeadOfHousehold(),
                    submitSubjectRequest.getPhoneNumber(), submitSubjectRequest.getAddress(),
                    Language.getByCode(submitSubjectRequest.getLanguage()), submitSubjectRequest.getCommunity(),
                    submitSubjectRequest.getSiteId());
            try {
                subjectService.createOrUpdate(subject);
            } catch (Exception ex) {
                LOGGER.error("Error raised during creating subject: " + ex.getMessage(), ex);
                return new ResponseEntity<>(Arrays.asList(ex.getMessage()),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            List<String> validationMessages = extract(errorList, on(ValidationError.class).getMessage());
            LOGGER.info(validationMessages.toString());
            return new ResponseEntity<>(validationMessages, HttpStatus.BAD_REQUEST);
        }
    }
}
