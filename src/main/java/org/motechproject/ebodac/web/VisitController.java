package org.motechproject.ebodac.web;

import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.web.domain.GridSettings;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class VisitController {

    @Autowired
    private VisitDataService visitDataService;

    @RequestMapping(value = "/visitsRecords", method = RequestMethod.POST)
    @PreAuthorize(Constants.Roles.HAS_DATA_ACCESS)
    @ResponseBody
    public Records<Visit> getAllCourses(GridSettings settings) {
        Order order = null;
        if (!settings.getSortColumn().isEmpty()) {
            order = new Order(settings.getSortColumn(), settings.getSortDirection());
        }

        QueryParams queryParams = new QueryParams(settings.getPage(), settings.getRows(), order);

        List<Visit> visits = visitDataService.retrieveAll(queryParams);

        long recordCount = visitDataService.count();
        int rowCount = (int) Math.ceil(recordCount / (double) settings.getRows());

        return new Records<>(settings.getPage(), rowCount, (int) recordCount, visits);
    }
}
