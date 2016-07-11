package org.motechproject.bookingapp.service.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.bookingapp.constants.BookingAppConstants;
import org.motechproject.bookingapp.dto.CapacityInfoDto;
import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.bookingapp.domain.DateFilter;
import org.motechproject.bookingapp.domain.ScreeningStatus;
import org.motechproject.bookingapp.repository.ClinicDataService;
import org.motechproject.bookingapp.repository.ScreeningDataService;
import org.motechproject.bookingapp.repository.UnscheduledVisitDataService;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.bookingapp.service.CapacityInfoService;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.enums.VisitType;
import org.motechproject.ebodac.util.QueryParamsBuilder;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.query.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("capacityInfoService")
public class CapacityInfoServiceImpl implements CapacityInfoService {

    @Autowired
    private ClinicDataService clinicDataService;

    @Autowired
    private ScreeningDataService screeningDataService;

    @Autowired
    private UnscheduledVisitDataService unscheduledVisitDataService;

    @Autowired
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Override
    public Records<CapacityInfoDto> getCapacityInfoRecords(BookingGridSettings settings) {
        QueryParams queryParams = QueryParamsBuilder.buildQueryParams(settings);

        List<CapacityInfoDto> capacityInfoDtos = new ArrayList<>();
        List<Clinic> clinics = clinicDataService.retrieveAll(queryParams);

        Range<LocalDate> dateRange = getDateRangeFromFilter(settings);

        if (dateRange != null) {
            int numberOfDays = Days.daysBetween(dateRange.getMin(), dateRange.getMax()).getDays() + 1;
            numberOfDays = numberOfDays < 0 ? 0 : numberOfDays;

            for (Clinic clinic : clinics) {
                int visitCount = (int) visitBookingDetailsDataService.countFindByClinicIdAndBookingPlannedDateRange(clinic.getId(), dateRange);
                int primeVacCount = (int) visitBookingDetailsDataService.countFindByClinicIdVisitTypeAndBookingPlannedDateRange(clinic.getId(),
                        VisitType.PRIME_VACCINATION_DAY, dateRange);
                int screeningCount = (int) screeningDataService.countFindByClinicIdAndDateRangeAndStatus(clinic.getId(), dateRange, ScreeningStatus.ACTIVE);
                int unscheduledCount = (int) unscheduledVisitDataService.countFindByClinicIdAndDateRange(clinic.getId(), dateRange);

                int allVisitsCount = visitCount + screeningCount + unscheduledCount;
                int maxCapacity = clinic.getMaxCapacityByDay() * numberOfDays;
                int availableCapacity = maxCapacity - allVisitsCount;
                int screeningSlotRemaining = clinic.getMaxScreeningVisits() * numberOfDays - screeningCount;
                int vaccineSlotRemaining = clinic.getMaxPrimeVisits() * numberOfDays - primeVacCount;

                capacityInfoDtos.add(new CapacityInfoDto(clinic.getLocation(), maxCapacity, availableCapacity,
                        screeningSlotRemaining, vaccineSlotRemaining));
            }
        } else {
            for (Clinic clinic : clinics) {
                capacityInfoDtos.add(new CapacityInfoDto(clinic.getLocation(), 0, 0, 0, 0));
            }
        }

        long recordCount;
        int rowCount;

        recordCount = clinicDataService.count();
        rowCount = (int) Math.ceil(recordCount / (double) settings.getRows());

        return new Records<>(settings.getPage(), rowCount, (int) recordCount, capacityInfoDtos);
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
