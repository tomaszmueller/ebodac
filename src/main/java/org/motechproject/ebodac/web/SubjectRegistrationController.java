package org.motechproject.ebodac.web;

import org.motechproject.ebodac.domain.SubjectRegistration;
import org.motechproject.ebodac.service.SubjectRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Web API for Subject Registration
 */
@RequestMapping("/registration")
@Controller
public class SubjectRegistrationController {

    @Autowired
    SubjectRegistrationService subjectRegistrationService;

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> addSubject (@RequestBody SubjectRegistration subjectRegistration) {

        try {
            subjectRegistrationService.add(subjectRegistration);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
