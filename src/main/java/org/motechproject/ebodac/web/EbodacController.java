package org.motechproject.ebodac.web;

import org.apache.commons.io.IOUtils;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;

import org.motechproject.ebodac.service.EbodacService;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

/**
 * Controller for EbodacController message and bundle status.
 */
@Controller
public class EbodacController {

    @Autowired
    private EbodacService ebodacService;

    @Autowired
    @Qualifier("ebodacSettings")
    private SettingsFacade settingsFacade;

    private static final String OK = "OK";

    @RequestMapping("/web-api/status")
    @ResponseBody
    public String status() {
        return OK;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/mds-databrowser-config", method = RequestMethod.GET)
    @ResponseBody
    public String getCustomUISettings() throws IOException {
        return IOUtils.toString(settingsFacade.getRawConfig(EbodacConstants.UI_CONFIG));
    }
}
