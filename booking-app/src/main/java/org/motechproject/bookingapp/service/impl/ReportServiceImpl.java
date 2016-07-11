package org.motechproject.bookingapp.service.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.bookingapp.constants.BookingAppConstants;
import org.motechproject.bookingapp.dto.CapacityReportDto;
import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.bookingapp.domain.DateFilter;
import org.motechproject.bookingapp.domain.ScreeningStatus;
import org.motechproject.bookingapp.repository.ClinicDataService;
import org.motechproject.bookingapp.repository.ScreeningDataService;
import org.motechproject.bookingapp.repository.UnscheduledVisitDataService;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.bookingapp.service.ReportService;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.enums.VisitType;
import org.motechproject.ebodac.service.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("reportService")
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ClinicDataService clinicDataService;

    @Autowired
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Autowired
    private ScreeningDataService screeningDataService;

    @Autowired
    private UnscheduledVisitDataService unscheduledVisitDataService;

    @Autowired
    private LookupService lookupService;

    @Override
    public List<CapacityReportDto> generateCapacityReports(BookingGridSettings settings) {
        List<CapacityReportDto> reports = new ArrayList<>();

        List<Clinic> clinics = lookupService.getEntities(Clinic.class, settings, null);

        Range<LocalDate> dateRange = getDateRangeFromFilter(settings);

        if (dateRange != null) {
            for (LocalDate date = dateRange.getMin(); !date.isAfter(dateRange.getMax()); date = date.plusDays(1)) {
                for (Clinic clinic : clinics) {
                    int visitCount = (int) visitBookingDetailsDataService.countFindByClinicIdAndBookingPlannedDate(clinic.getId(), date);
                    int primeVacCount = (int) visitBookingDetailsDataService.countFindByClinicIdVisitTypeAndBookingPlannedDate(clinic.getId(),
                            VisitType.PRIME_VACCINATION_DAY, date);
                    int screeningCount = (int) screeningDataService.countFindByClinicIdAndDateAndStatus(clinic.getId(), date, ScreeningStatus.ACTIVE);
                    int unscheduledCount = (int) unscheduledVisitDataService.countFindByClinicIdAndDate(clinic.getId(), date);

                    int allVisitsCount = visitCount + screeningCount + unscheduledCount;
                    int maxCapacity = clinic.getMaxCapacityByDay();
                    int availableCapacity = maxCapacity - allVisitsCount;
                    int screeningSlotRemaining = clinic.getMaxScreeningVisits() - screeningCount;
                    int vaccineSlotRemaining = clinic.getMaxPrimeVisits() - primeVacCount;

                    reports.add(new CapacityReportDto(date.toString(BookingAppConstants.SIMPLE_DATE_FORMAT),
                            clinic.getLocation(), maxCapacity, availableCapacity, screeningSlotRemaining, vaccineSlotRemaining));
                }
            }
        }
        return reports;
    }

    private Range<LocalDate> getDateRangeFromFilter(BookingGridSettings settings) {
        DateFilter filter = settings.getDateFilter();

        if (filter == null) {
            return null;
        }

        if (DateFilter.DATE_RANGE.equals(filter)) {
            if (StringUtils.isNotBlank(settings.getStartDate()) && StringUtils.isNotBlank(settings.getEndDate())) {
                return new Range<>(LocalDate.parse(settings.getStartDate(), DateTimeFormat.forPattern(BookingAppConstants.SIMPLE_DATE_FORMAT)),
                        LocalDate.parse(settings.getEndDate(), DateTimeFormat.forPattern(BookingAppConstants.SIMPLE_DATE_FORMAT)));
            } else {
                return null;
            }
        } else {
            return filter.getRange();
        }
    }
}
