package org.motechproject.bookingapp.web;

import org.motechproject.bookingapp.constants.BookingAppConstants;
import org.motechproject.bookingapp.dto.CapacityInfoDto;
import org.motechproject.bookingapp.service.CapacityInfoService;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.ebodac.web.domain.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "/capacity")
@PreAuthorize(BookingAppConstants.HAS_CAPACITY_INFO_TAB_ROLE)
public class CapacityInfoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CapacityInfoController.class);

    @Autowired
    private CapacityInfoService capacityInfoService;

    @RequestMapping(value = "/getCapacityInfo", method = RequestMethod.GET)
    @ResponseBody
    public Records<CapacityInfoDto> getCapacityInfo(BookingGridSettings settings) {
        return capacityInfoService.getCapacityInfoRecords(settings);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return e.getMessage();
    }
}
