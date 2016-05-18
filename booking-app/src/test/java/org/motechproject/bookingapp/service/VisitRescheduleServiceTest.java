package org.motechproject.bookingapp.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.domain.VisitRescheduleDto;
import org.motechproject.bookingapp.domain.VisitScheduleOffset;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.bookingapp.service.impl.VisitRescheduleServiceImpl;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.model.Time;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.query.QueryParams;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(VisitRescheduleServiceImpl.class)
public class VisitRescheduleServiceTest {


    @InjectMocks
    private VisitRescheduleService visitRescheduleService = new VisitRescheduleServiceImpl();

    @Mock
    private ConfigService configService;

    @Mock
    private VisitScheduleOffsetService visitScheduleOffsetService;

    @Mock
    private VisitDataService visitDataService;

    @Mock
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Mock
    private EbodacEnrollmentService ebodacEnrollmentService;

    @Mock
    private LookupService lookupService;

    private Subject subject1;

    private Subject subject2;

    @Before
    public void setUp() {
        initMocks(this);
        subject1 = new Subject("1", "asd", "asd", "asd", "123", "asd", Language.English, "asd", "asd", "asd", "asd", "asd", "asd");
        subject1.setPrimerVaccinationDate(new LocalDate(2217, 2, 1));
        subject1.setBoosterVaccinationDate(new LocalDate(2217, 3, 1));
        subject2 = new Subject("2", "asd", "asd", "asd", "123", "asd", Language.English, "asd", "asd", "asd", "asd", "asd", "asd");
    }

    @Test
    public void shouldGetVisitBookingDetailsRecords() throws IOException {
        BookingGridSettings bookingGridSettings = new BookingGridSettings();
        bookingGridSettings.setPage(1);
        bookingGridSettings.setRows(10);

        List<VisitBookingDetails> visitBookingDetailses = new ArrayList<>(Arrays.asList(
                new VisitBookingDetails(new LocalDate(2217, 4, 1), createVisit(1L, VisitType.PRIME_VACCINATION_FIRST_FOLLOW_UP_VISIT, null, new LocalDate(2217, 4, 1), subject1)),
                new VisitBookingDetails(new LocalDate(2217, 4, 1), createVisit(2L, VisitType.BOOST_VACCINATION_FIRST_FOLLOW_UP_VISIT, null, new LocalDate(2217, 4, 1), subject1)),
                new VisitBookingDetails(new LocalDate(2217, 4, 1), createVisit(1L, VisitType.FIRST_LONG_TERM_FOLLOW_UP_VISIT, null, new LocalDate(2217, 4, 1), subject2))
        ));

        Records<VisitBookingDetails> records = new Records<>(1, 10, 3, visitBookingDetailses);

        when(lookupService.getEntities(eq(VisitBookingDetails.class), anyString(), anyString(), any(QueryParams.class))).thenReturn(records);

        List<VisitScheduleOffset> visitScheduleOffsets = new ArrayList<>(Arrays.asList(
                createVistScheduleOffset(VisitType.PRIME_VACCINATION_FIRST_FOLLOW_UP_VISIT, 2L, 10, 5, 12),
                createVistScheduleOffset(VisitType.BOOST_VACCINATION_FIRST_FOLLOW_UP_VISIT, 2L, 20, 15, 24),
                createVistScheduleOffset(VisitType.FIRST_LONG_TERM_FOLLOW_UP_VISIT, 2L, 30, 25, 27)
        ));
        Map<VisitType, VisitScheduleOffset> offsetMapForStageId = new LinkedHashMap<>();
        offsetMapForStageId.put(VisitType.PRIME_VACCINATION_FIRST_FOLLOW_UP_VISIT, visitScheduleOffsets.get(0));
        offsetMapForStageId.put(VisitType.BOOST_VACCINATION_FIRST_FOLLOW_UP_VISIT, visitScheduleOffsets.get(1));
        offsetMapForStageId.put(VisitType.FIRST_LONG_TERM_FOLLOW_UP_VISIT, visitScheduleOffsets.get(2));
        Map<Long, Map<VisitType, VisitScheduleOffset>> offsetMap = new LinkedHashMap<>();
        offsetMap.put(2L, offsetMapForStageId);

        when(visitScheduleOffsetService.getAllAsMap()).thenReturn(offsetMap);

        Config config = new Config();
        List<String> boosterRelatedMessage = new ArrayList<>(Arrays.asList("Boost Vaccination First Follow-up visit - stage 2"));
        config.setActiveStageId(2L);
        config.setBoosterRelatedMessages(boosterRelatedMessage);

        when(configService.getConfig()).thenReturn(config);

        List<VisitRescheduleDto> expectedDtos = new ArrayList<>(Arrays.asList(
                new VisitRescheduleDto(visitBookingDetailses.get(0), new Range<>(new LocalDate(2217, 2, 6), new LocalDate(2217, 2, 13))),
                new VisitRescheduleDto(visitBookingDetailses.get(1), new Range<>(new LocalDate(2217, 3, 16), new LocalDate(2217, 3, 25))),
                new VisitRescheduleDto(visitBookingDetailses.get(2), null)
        ));

        List<VisitRescheduleDto> resultDtos = visitRescheduleService.getVisitsRecords(bookingGridSettings).getRows();

        for (int i = 0; i < expectedDtos.size(); i++) {
            assertEquals(expectedDtos.get(i).getEarliestDate(), resultDtos.get(i).getEarliestDate());
            assertEquals(expectedDtos.get(i).getLatestDate(), resultDtos.get(i).getLatestDate());
            assertEquals(expectedDtos.get(i).getParticipantId(), resultDtos.get(i).getParticipantId());
            assertEquals(expectedDtos.get(i).getIgnoreDateLimitation(), resultDtos.get(i).getIgnoreDateLimitation());
        }
    }

    @Test
    public void shouldSaveRescheduledVisit() {
        VisitBookingDetails visitBookingDetails = new VisitBookingDetails(null, createVisit(1L, VisitType.BOOST_VACCINATION_FIRST_FOLLOW_UP_VISIT, null, new LocalDate(2217, 1, 1), subject1));

        VisitRescheduleDto visitRescheduleDto = new VisitRescheduleDto(visitBookingDetails, new Range<LocalDate>(new LocalDate(2217, 2, 1), new LocalDate(2217, 3, 1)));
        Boolean ignoreLimitation = true;
        visitRescheduleDto.setStartTime(new Time(9, 0));
        visitRescheduleDto.setIgnoreDateLimitation(ignoreLimitation);
        visitRescheduleDto.setVisitBookingDetailsId(1L);

        when(visitBookingDetailsDataService.findById(1L)).thenReturn(visitBookingDetails);
        when(ebodacEnrollmentService.checkIfEnrolledAndUpdateEnrollment(visitBookingDetails.getVisit())).thenReturn(true);
        when(visitDataService.update(visitBookingDetails.getVisit())).thenReturn(visitBookingDetails.getVisit());
        when(visitBookingDetailsDataService.update(visitBookingDetails)).thenReturn(visitBookingDetails);

        visitRescheduleService.saveVisitReschedule(visitRescheduleDto, ignoreLimitation);

        verify(visitBookingDetailsDataService).update(any(VisitBookingDetails.class));
        verify(visitDataService).update(any(Visit.class));
        verify(ebodacEnrollmentService).reenrollSubject(visitBookingDetails.getVisit());
        verify(visitBookingDetailsDataService, never()).findByClinicIdVisitPlannedDateAndType(anyLong(), any(LocalDate.class), any(VisitType.class));
    }

    private VisitScheduleOffset createVistScheduleOffset(VisitType visitType, Long stageId, Integer timeOffset, Integer earliestDateOffset, Integer latestDateOffset) {
        VisitScheduleOffset visitScheduleOffset = new VisitScheduleOffset();
        visitScheduleOffset.setVisitType(visitType);
        visitScheduleOffset.setStageId(stageId);
        visitScheduleOffset.setTimeOffset(timeOffset);
        visitScheduleOffset.setEarliestDateOffset(earliestDateOffset);
        visitScheduleOffset.setLatestDateOffset(latestDateOffset);
        return visitScheduleOffset;
    }

    private Visit createVisit(Long id, VisitType visitType, LocalDate date, LocalDate projectedDate, Subject subject) {
        Visit visit = new Visit();
        visit.setId(id);
        visit.setType(visitType);
        visit.setDate(date);
        visit.setMotechProjectedDate(projectedDate);
        visit.setSubject(subject);
        return visit;
    }
}
