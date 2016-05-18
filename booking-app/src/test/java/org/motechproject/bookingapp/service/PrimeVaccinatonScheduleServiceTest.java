package org.motechproject.bookingapp.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.bookingapp.domain.PrimeVaccinationScheduleDto;
import org.motechproject.bookingapp.domain.SubjectBookingDetails;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.repository.ClinicDataService;
import org.motechproject.bookingapp.repository.ScreeningDataService;
import org.motechproject.bookingapp.repository.UnscheduledVisitDataService;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.bookingapp.service.impl.PrimeVaccinationScheduleServiceImpl;
import org.motechproject.commons.date.model.Time;
import org.motechproject.ebodac.domain.Gender;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PrimeVaccinationScheduleService.class)
public class PrimeVaccinatonScheduleServiceTest {

    @InjectMocks
    private PrimeVaccinationScheduleService primeVaccinationScheduleService = new PrimeVaccinationScheduleServiceImpl();

    @Mock
    private ScreeningDataService screeningDataService;

    @Mock
    private UnscheduledVisitDataService unscheduledVisitDataService;

    @Mock
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Mock
    private SubjectDataService subjectDataService;

    @Mock
    private VisitDataService visitDataService;

    @Mock
    private ClinicDataService clinicDataService;

    private Subject subject;

    private Clinic clinic;

    @Before
    public void setUp() {
        initMocks(this);
        subject = new Subject("1", "name", "householdName", "headOfHousehold", "123456789", "address", Language.English, "community", "siteid", "siteName", "chiefom", "section", "district");
        subject.setGender(Gender.Male);
        clinic = new Clinic();
        clinic.setId(1L);
    }

    @Test
    public void shouldCreatePrimeVaccinationRecord() {
        SubjectBookingDetails subjectBookingDetails = new SubjectBookingDetails(subject);
        subjectBookingDetails.setVisitBookingDetailsList(
                Collections.singletonList(new VisitBookingDetails(new LocalDate(2217, 2, 1), createVisit(subject, 2L, VisitType.SCREENING, new LocalDate(2217, 1, 1), new LocalDate(2216, 1, 1))))
        );
        VisitBookingDetails visitBookingDetails = new VisitBookingDetails(createVisit(subject, 1L, VisitType.PRIME_VACCINATION_DAY, null, new LocalDate(2217, 1, 1)), subjectBookingDetails);
        visitBookingDetails.setBookingPlannedDate(new LocalDate(2217, 1, 28));

        PrimeVaccinationScheduleDto primeVaccinationScheduleDto = new PrimeVaccinationScheduleDto(visitBookingDetails);
        primeVaccinationScheduleDto.setIgnoreDateLimitation(true);
        primeVaccinationScheduleDto.setVisitBookingDetailsId(1L);
        primeVaccinationScheduleDto.setStartTime(new Time(9, 0));

        when(visitBookingDetailsDataService.findById(primeVaccinationScheduleDto.getVisitBookingDetailsId())).thenReturn(visitBookingDetails);

        VisitBookingDetails expectedPrimeDetailsAdded = new VisitBookingDetails(visitBookingDetails.getVisit(), visitBookingDetails.getSubjectBookingDetails());
        expectedPrimeDetailsAdded.setStartTime(new Time(9, 0));
        expectedPrimeDetailsAdded.setEndTime(new Time(10, 0));
        expectedPrimeDetailsAdded.setIgnoreDateLimitation(true);
        expectedPrimeDetailsAdded.getSubjectBookingDetails().setFemaleChildBearingAge(false);
        expectedPrimeDetailsAdded.setBookingPlannedDate(primeVaccinationScheduleDto.getDate());

        when(visitBookingDetailsDataService.update(any(VisitBookingDetails.class))).thenReturn(expectedPrimeDetailsAdded);

        primeVaccinationScheduleService.createOrUpdateWithDto(primeVaccinationScheduleDto, true);

        ArgumentCaptor<VisitBookingDetails> visitBookingDetailsArgumentCaptor = ArgumentCaptor.forClass(VisitBookingDetails.class);
        verify(visitBookingDetailsDataService, times(2)).update(visitBookingDetailsArgumentCaptor.capture());

        VisitBookingDetails primeDetailsAdded = visitBookingDetailsArgumentCaptor.getAllValues().get(1);

        assertEquals(expectedPrimeDetailsAdded.getId(), primeDetailsAdded.getId());
        assertEquals(expectedPrimeDetailsAdded.getBookingPlannedDate(), primeDetailsAdded.getBookingPlannedDate());
        assertEquals(expectedPrimeDetailsAdded.getStartTime(), primeDetailsAdded.getStartTime());
        assertEquals(expectedPrimeDetailsAdded.getEndTime(), primeDetailsAdded.getEndTime());
        assertEquals(expectedPrimeDetailsAdded.getIgnoreDateLimitation(), primeDetailsAdded.getIgnoreDateLimitation());
        assertEquals(expectedPrimeDetailsAdded.getSubjectBookingDetails().getFemaleChildBearingAge(), primeDetailsAdded.getSubjectBookingDetails().getFemaleChildBearingAge());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenDateIsOutOfWindow() {
        SubjectBookingDetails subjectBookingDetails = new SubjectBookingDetails(subject);
        subjectBookingDetails.setVisitBookingDetailsList(
                Collections.singletonList(new VisitBookingDetails(new LocalDate(2217, 2, 1), createVisit(subject, 2L, VisitType.SCREENING, new LocalDate(2217, 1, 1), new LocalDate(2216, 1, 1))))
        );

        VisitBookingDetails visitBookingDetails = new VisitBookingDetails(createVisit(subject, 1L, VisitType.PRIME_VACCINATION_DAY, null, new LocalDate(2217, 1, 1)), subjectBookingDetails);
        visitBookingDetails.setBookingPlannedDate(new LocalDate(2217, 2, 28));

        PrimeVaccinationScheduleDto primeVaccinationScheduleDto = new PrimeVaccinationScheduleDto(visitBookingDetails);
        primeVaccinationScheduleDto.setVisitBookingDetailsId(1L);

        when(visitBookingDetailsDataService.findById(primeVaccinationScheduleDto.getVisitBookingDetailsId())).thenReturn(visitBookingDetails);

        primeVaccinationScheduleService.createOrUpdateWithDto(primeVaccinationScheduleDto, true);
    }

    @Test
    public void shouldGetPrimeVaccinationRecords() {
        SubjectBookingDetails subjectBookingDetails = new SubjectBookingDetails(subject);
        subjectBookingDetails.setVisitBookingDetailsList(new ArrayList<>(
                Collections.singletonList(new VisitBookingDetails(new LocalDate(2217, 2, 1), createVisit(subject, 2L, VisitType.SCREENING, null, new LocalDate(2216, 1, 1))))
        ));
        VisitBookingDetails visitBookingDetails = new VisitBookingDetails(createVisit(subject, 1L, VisitType.PRIME_VACCINATION_DAY, null, new LocalDate(2217, 1, 1)), subjectBookingDetails);
        visitBookingDetails.setId(1L);
        PrimeVaccinationScheduleDto primeVaccinationScheduleDto = new PrimeVaccinationScheduleDto(visitBookingDetails);

        when(visitBookingDetailsDataService.findById(primeVaccinationScheduleDto.getVisitId())).thenReturn(visitBookingDetails);
        when(subjectDataService.findByPrimerVaccinationDate(null)).thenReturn(Collections.singletonList(subject));
        when(visitDataService.findBySubjectIdAndType(subject.getSubjectId(), VisitType.SCREENING)).thenReturn(null);
        when(visitDataService.findBySubjectIdAndType(subject.getSubjectId(), VisitType.PRIME_VACCINATION_DAY)).thenReturn(null);
        when(visitDataService.findBySubjectIdAndType(subject.getSubjectId(), VisitType.PRIME_VACCINATION_FIRST_FOLLOW_UP_VISIT)).thenReturn(null);

        when(visitBookingDetailsDataService.findByParticipantNamePrimeVaccinationDateAndVisitTypeAndBookingPlannedDateEq(".", null, VisitType.PRIME_VACCINATION_DAY, null)).thenReturn(Collections.singletonList(visitBookingDetails));

        List<PrimeVaccinationScheduleDto> resultDtos = primeVaccinationScheduleService.getPrimeVaccinationScheduleRecords();
        verify(visitDataService, times(3)).create(any(Visit.class));

        assertEquals(1, resultDtos.size());
        assertEquals(visitBookingDetails.getId(), resultDtos.get(0).getVisitId());
        assertEquals(visitBookingDetails.getSubject().getSubjectId(), resultDtos.get(0).getParticipantId());

    }

    private Visit createVisit(Subject subject, Long id, VisitType visitType, LocalDate date, LocalDate projectedDate) {
        Visit visit = new Visit();
        visit.setDate(date);
        visit.setMotechProjectedDate(projectedDate);
        visit.setType(visitType);
        visit.setId(id);
        visit.setSubject(subject);
        subject.getVisits().add(visit);
        return visit;
    }

}
