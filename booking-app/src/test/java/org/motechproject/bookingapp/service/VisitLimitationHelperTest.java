package org.motechproject.bookingapp.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.bookingapp.domain.ScreeningStatus;
import org.motechproject.bookingapp.exception.LimitationExceededException;
import org.motechproject.bookingapp.helper.VisitLimitationHelper;
import org.motechproject.bookingapp.repository.ClinicDataService;
import org.motechproject.bookingapp.repository.ScreeningDataService;
import org.motechproject.bookingapp.repository.UnscheduledVisitDataService;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.ebodac.domain.enums.VisitType;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class VisitLimitationHelperTest {

    @InjectMocks
    private VisitLimitationHelper visitLimitationHelper = new VisitLimitationHelper();

    @Mock
    private ScreeningDataService screeningDataService;

    @Mock
    private UnscheduledVisitDataService unscheduledVisitDataService;

    @Mock
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Mock
    private ClinicDataService clinicDataService;

    @Rule
    public ExpectedException thrown = ExpectedException.none(); //NO CHECKSTYLE VisibilityModifier

    private Clinic clinic;

    @Before
    public void setUp() {
        initMocks(this);
        clinic = new Clinic("siteId", "location", 1, 5, 1, 9, 8, 7, 10, 10, 10, 10, 10, 10, 10);
        clinic.setId(1L);

    }

    @Test(expected = LimitationExceededException.class)
    public void shouldThrowLimitationExceededExceptionWhenCapacityLimitIsReached() {
        when(screeningDataService.countFindByClinicIdDateAndScreeningIdAndStatus(new LocalDate(2217, 1, 1), clinic.getId(), 1L, ScreeningStatus.ACTIVE)).thenReturn(2L);
        when(unscheduledVisitDataService.countFindByClinicIdAndDateAndVisitId(new LocalDate(2217, 1, 1), clinic.getId(), null)).thenReturn(1L);
        when(visitBookingDetailsDataService.countFindByBookingPlannedDateAndClinicIdAndVisitId(new LocalDate(2217, 1, 1), clinic.getId(), null)).thenReturn(4L);

        visitLimitationHelper.checkCapacityForScreening(new LocalDate(2217, 1, 1), clinic, 1L);
    }

    @Test
    public void shouldNotThrowLimitationExceededExceptionWhenCapacityLimitIsNotReached() {
        when(screeningDataService.countFindByClinicIdDateAndScreeningIdAndStatus(new LocalDate(2217, 1, 1), clinic.getId(), 1L, ScreeningStatus.ACTIVE)).thenReturn(2L);
        when(unscheduledVisitDataService.countFindByClinicIdAndDateAndVisitId(new LocalDate(2217, 1, 1), clinic.getId(), null)).thenReturn(1L);
        when(visitBookingDetailsDataService.countFindByBookingPlannedDateAndClinicIdAndVisitId(new LocalDate(2217, 1, 1), clinic.getId(), null)).thenReturn(1L);

        visitLimitationHelper.checkCapacityForScreening(new LocalDate(2217, 1, 1), clinic, 1L);
    }

    @Test
    public void shouldReturnMaxVisitCountForVisitType() {
        assertEquals(visitLimitationHelper.getMaxVisitCountForVisitType(VisitType.PRIME_VACCINATION_DAY, clinic), 9);
        assertEquals(visitLimitationHelper.getMaxVisitCountForVisitType(VisitType.PRIME_VACCINATION_FIRST_FOLLOW_UP_VISIT, clinic), 8);
        assertEquals(visitLimitationHelper.getMaxVisitCountForVisitType(VisitType.PRIME_VACCINATION_SECOND_FOLLOW_UP_VISIT, clinic), 7);

        thrown.expect(IllegalArgumentException.class);
        visitLimitationHelper.getMaxVisitCountForVisitType(VisitType.SCREENING, clinic);
    }
}
