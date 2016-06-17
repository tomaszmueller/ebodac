package org.motechproject.bookingapp.web;


import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.enums.VisitType;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping(value = "participants")
public class RepositoryController {

    @Autowired
    private SubjectDataService subjectDataService;

    @RequestMapping(value = "/screened", method = RequestMethod.GET)
    @ResponseBody
    public List<Subject> getScreenedParticipants() throws IOException {
        return subjectDataService.findByVisitTypeAndActualDate(VisitType.SCREENING, null);
    }

}
