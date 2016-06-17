package org.motechproject.bookingapp.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.bookingapp.dto.CapacityInfoDto;
import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.bookingapp.domain.DateFilter;
import org.motechproject.bookingapp.domain.ScreeningStatus;
import org.motechproject.bookingapp.repository.ClinicDataService;
import org.motechproject.bookingapp.repository.ScreeningDataService;
import org.motechproject.bookingapp.repository.UnscheduledVisitDataService;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.bookingapp.service.impl.CapacityInfoServiceImpl;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.enums.VisitType;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.query.QueryParams;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CapacityInfoService.class)
public class CapacityInfoServiceTest {

    @InjectMocks
    private CapacityInfoService capacityInfoService = new CapacityInfoServiceImpl();

    @Mock
    private ScreeningDataService screeningDataService;

    @Mock
    private UnscheduledVisitDataService unscheduledVisitDataService;

    @Mock
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Mock
    private ClinicDataService clinicDataService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldCalculateClinicCapacity() throws Exception {

        BookingGridSettings bookingGridSettings = createBookingGridSettings(1, 10, DateFilter.DATE_RANGE, "2017-1-1", "2017-1-2");

        List<Clinic> clinics = new ArrayList<>(Arrays.asList(new Clinic("siteId", "first", 20, 5, 4, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                new Clinic("siteId", "second", 20, 10, 2, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0)));
        clinics.get(0).setId(1L);
        clinics.get(1).setId(2L);

        when(clinicDataService.retrieveAll(Mockito.any(QueryParams.class))).thenReturn(clinics);

        Range<LocalDate> dateRange = new Range<>(new LocalDate(2017, 1, 1), new LocalDate(2017, 1, 2));

        when(visitBookingDetailsDataService.countFindByClinicIdAndBookingPlannedDateRange(1L, dateRange)).thenReturn(8L);
        when(visitBookingDetailsDataService.countFindByClinicIdVisitTypeAndBookingPlannedDateRange(1L, VisitType.PRIME_VACCINATION_DAY, dateRange)).thenReturn(7L);
        when(screeningDataService.countFindByClinicIdAndDateRangeAndStatus(1L, dateRange, ScreeningStatus.ACTIVE)).thenReturn(4L);
        when(unscheduledVisitDataService.countFindByClinicIdAndDateRange(1L, dateRange)).thenReturn(3L);

        when(visitBookingDetailsDataService.countFindByClinicIdAndBookingPlannedDateRange(2L, dateRange)).thenReturn(4L);
        when(visitBookingDetailsDataService.countFindByClinicIdVisitTypeAndBookingPlannedDateRange(2L, VisitType.PRIME_VACCINATION_DAY, dateRange)).thenReturn(2L);
        when(screeningDataService.countFindByClinicIdAndDateRangeAndStatus(2L, dateRange, ScreeningStatus.ACTIVE)).thenReturn(6L);
        when(unscheduledVisitDataService.countFindByClinicIdAndDateRange(2L, dateRange)).thenReturn(1L);

        when(clinicDataService.count()).thenReturn(2L);

        List<CapacityInfoDto> capacityInfoDtos = new ArrayList<>(Arrays.asList(new CapacityInfoDto("first", 10, -5, 4, -1), new CapacityInfoDto("second", 20, 9, -2, 16)));
        Records<CapacityInfoDto> result = capacityInfoService.getCapacityInfoRecords(bookingGridSettings);
        for (int i = 0; i < capacityInfoDtos.size(); i++) {
            checkIfCapacityDtoAreSame(capacityInfoDtos.get(i), result.getRows().get(i));
        }
    }

    @Test
    public void shouldReturnZerosForEmptyDateRange() throws Exception {
        BookingGridSettings bookingGridSettings = createBookingGridSettings(1, 10, null, "", "");
        List<Clinic> clinics = new ArrayList<>(Arrays.asList(new Clinic("siteId", "first", 20, 5, 4, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                new Clinic("siteId", "second", 20, 10, 2, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0)));

        when(clinicDataService.retrieveAll(Mockito.any(QueryParams.class))).thenReturn(clinics);
        when(clinicDataService.count()).thenReturn(2L);

        List<CapacityInfoDto> capacityInfoDtos = new ArrayList<>(Arrays.asList(new CapacityInfoDto("first", 0, 0, 0, 0), new CapacityInfoDto("second", 0, 0, 0, 0)));
        Records<CapacityInfoDto> result = capacityInfoService.getCapacityInfoRecords(bookingGridSettings);
        for (int i = 0; i < capacityInfoDtos.size(); i++) {
            checkIfCapacityDtoAreSame(capacityInfoDtos.get(i), result.getRows().get(i));
        }
    }

    private BookingGridSettings createBookingGridSettings(int page, int rows, DateFilter dateFilter, String startDate, String endDate) {
        BookingGridSettings bookingGridSettings = new BookingGridSettings();
        bookingGridSettings.setPage(page);
        bookingGridSettings.setRows(rows);
        bookingGridSettings.setDateFilter(dateFilter);
        bookingGridSettings.setStartDate(startDate);
        bookingGridSettings.setEndDate(endDate);
        return bookingGridSettings;
    }

    private void checkIfCapacityDtoAreSame(CapacityInfoDto expected, CapacityInfoDto result) {
        assertEquals(expected.getClinic(), result.getClinic());
        assertEquals(expected.getMaxCapacity(), result.getMaxCapacity());
        assertEquals(expected.getAvailableCapacity(), result.getAvailableCapacity());
        assertEquals(expected.getScreeningSlotRemaining(), result.getScreeningSlotRemaining());
        assertEquals(expected.getVaccineSlotRemaining(), result.getVaccineSlotRemaining());
    }
}
