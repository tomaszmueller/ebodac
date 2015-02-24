package org.motechproject.ebodac.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;

import org.motechproject.ebodac.service.EbodacService;

/**
 * Controller for EbodacController message and bundle status.
 */
@Controller
public class EbodacController {

    @Autowired
    private EbodacService ebodacService;

    private static final String OK = "OK";

    @RequestMapping("/web-api/status")
    @ResponseBody
    public String status() {
        return OK;
    }

    @RequestMapping("/sayHello")
    @ResponseBody
    public String sayHello() {
        return String.format("{\"message\":\"%s\"}", ebodacService.sayHello());
    }
}
