package org.motechproject.bookingapp.web;

import org.motechproject.bookingapp.domain.Config;
import org.motechproject.bookingapp.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@Controller
public class ConfigController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigController.class);

    private ConfigService configService;

    @Autowired
    public ConfigController(@Qualifier("bookingAppConfigService") ConfigService configService) {
        this.configService = configService;
    }

    @RequestMapping(value = "/booking-app-config", method = RequestMethod.GET)
    @ResponseBody
    public Config getConfig() {
        return configService.getConfig();
    }

    @RequestMapping(value = "/booking-app-config", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Config updateConfig(@RequestBody Config config) {
        configService.updateConfig(config);
        return configService.getConfig();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        LOGGER.error("Error while updating configs", e);
        return e.getMessage();
    }
}
