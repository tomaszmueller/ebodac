package org.motechproject.ebodac.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.ebodac.constants.EbodacConstants.MANAGE_EBODAC_PERMISSION;
import static org.motechproject.ebodac.constants.EbodacConstants.EMAIL_REPORTS_TAB_PERMISSION;

@Controller
public class TabAccessController {

    @RequestMapping(value = "/available/ebodacTabs", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getAvailableTabs() {
        List<String> availableTabs = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority(MANAGE_EBODAC_PERMISSION))) {
            availableTabs.add("subjects");
            availableTabs.add("visits");
            availableTabs.add("reports");
            availableTabs.add("enrollment");
            availableTabs.add("statistics");
        }

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority(EMAIL_REPORTS_TAB_PERMISSION))) {
            availableTabs.add("emailReports");
        }

        return availableTabs;
    }
}
