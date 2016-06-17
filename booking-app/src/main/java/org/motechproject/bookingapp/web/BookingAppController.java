package org.motechproject.bookingapp.web;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.bookingapp.domain.SubjectBookingDetails;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.repository.ClinicDataService;
import org.motechproject.bookingapp.repository.SubjectBookingDetailsDataService;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.List;

@Controller
public class BookingAppController {

    private static final String UI_CONFIG = "custom-ui.js";

    @Autowired
    @Qualifier("bookingAppSettings")
    private SettingsFacade settingsFacade;

    @Autowired
    private VisitDataService visitDataService;

    @Autowired
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Autowired
    private SubjectBookingDetailsDataService subjectBookingDetailsDataService;

    @Autowired
    private ClinicDataService clinicDataService;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/mds-databrowser-config", method = RequestMethod.GET)
    @ResponseBody
    public String getCustomUISettings() throws IOException {
        return IOUtils.toString(settingsFacade.getRawConfig(UI_CONFIG));
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/createMissingVisitBookingDetails", method = RequestMethod.POST)
    @ResponseBody
    public void createMissingVisitBookingDetails() throws IOException {
        List<Visit> visits = visitDataService.retrieveAll();

        for (Visit visit : visits) {
            VisitBookingDetails visitBookingDetails = visitBookingDetailsDataService.findByVisitId(visit.getId());

            if (visitBookingDetails == null) {
                Subject subject = visit.getSubject();
                SubjectBookingDetails subjectBookingDetails = subjectBookingDetailsDataService.findBySubjectId(subject.getSubjectId());

                if (subjectBookingDetails == null) {
                    subjectBookingDetails = new SubjectBookingDetails(subject);
                }

                visitBookingDetails = new VisitBookingDetails(visit, subjectBookingDetails);

                if (StringUtils.isNotBlank(subject.getSiteId())) {
                    Clinic clinic = clinicDataService.findByExactSiteId(subject.getSiteId());
                    visitBookingDetails.setClinic(clinic);
                }

                visitBookingDetailsDataService.create(visitBookingDetails);
            }
        }
    }
}
