package org.motechproject.ebodac.repository;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.ReportBoosterVaccination;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;

public interface ReportBoosterVaccinationDataService extends MotechDataService<ReportBoosterVaccination> {

    @Lookup
    List<ReportBoosterVaccination> findReportByDate(@LookupField(name = "date") Range<DateTime> dateRange);
}
