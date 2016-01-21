package org.motechproject.bookingapp.web;

import org.motechproject.bookingapp.constants.BookingAppConstants;
import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.bookingapp.service.ClinicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller("/clinics")
public class ClinicController {

    @Autowired
    private ClinicService clinicService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    private List<Clinic> getSites() {
        return clinicService.getClinics();
    }

    @RequestMapping(value = "/clinicFields", method = RequestMethod.GET)
    @ResponseBody
    private List<String> getClinicFields() {
        return BookingAppConstants.CLINIC_FIELDS;
    }
}
