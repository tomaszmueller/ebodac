package org.motechproject.ebodac.web;

import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.web.domain.GridSettings;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Controller
public class VisitController {
    private static final Logger LOGGER = LoggerFactory.getLogger(VisitController.class);

    @Autowired
    private LookupService lookupService;

    @Autowired
    private VisitDataService visitDataService;

    @RequestMapping(value = "/visitsRecords", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('mdsDataAccess', 'manageEbodac')")
    @ResponseBody
    public Records<?> getVisits(GridSettings settings) {
        Order order = null;
        if (!settings.getSortColumn().isEmpty()) {
            order = new Order(settings.getSortColumn(), settings.getSortDirection());
        }

        QueryParams queryParams = new QueryParams(settings.getPage(), settings.getRows(), order);

        Records<?> visits;
        try {
            return lookupService.getEntities(visitDataService, settings.getLookup(), settings.getFields(), queryParams);
        } catch (IOException e) {
            LOGGER.debug(e.getMessage(), e);
            return null;
        } catch (NoSuchMethodException e) {
            LOGGER.debug(e.getMessage(), e);
            return null;
        } catch (InvocationTargetException e) {
            LOGGER.debug(e.getMessage(), e);
            return null;
        } catch (IllegalAccessException e) {
            LOGGER.debug(e.getMessage(), e);
            return null;
        }
    }
}
