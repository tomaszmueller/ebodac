package org.motechproject.bookingapp.service.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.LocalDate;
import org.motechproject.bookingapp.constants.BookingAppConstants;
import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.domain.VisitRescheduleDto;
import org.motechproject.bookingapp.domain.VisitScheduleOffset;
import org.motechproject.bookingapp.exception.LimitationExceededException;
import org.motechproject.bookingapp.helper.VisitLimitationHelper;
import org.motechproject.bookingapp.repository.ClinicDataService;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.bookingapp.service.VisitRescheduleService;
import org.motechproject.bookingapp.service.VisitScheduleOffsetService;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.model.Time;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.util.QueryParamsBuilder;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.query.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service("visitRescheduleService")
public class VisitRescheduleServiceImpl implements VisitRescheduleService {

    @Autowired
    private LookupService lookupService;

    @Autowired
    private EbodacEnrollmentService ebodacEnrollmentService;

    @Autowired
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Autowired
    private VisitDataService visitDataService;

    @Autowired
    private ClinicDataService clinicDataService;

    @Autowired
    private VisitScheduleOffsetService visitScheduleOffsetService;

    @Autowired
    private VisitLimitationHelper visitLimitationHelper;

    @Autowired
    private ConfigService configService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Records<VisitRescheduleDto> getVisitsRecords(BookingGridSettings settings) throws IOException {
        QueryParams queryParams = QueryParamsBuilder.buildQueryParams(settings, getFields(settings.getFields()));
        Records<VisitBookingDetails> detailsRecords = lookupService.getEntities(VisitBookingDetails.class, settings.getLookup(), settings.getFields(), queryParams);

        Map<Long, Map<VisitType, VisitScheduleOffset>> offsetMap = visitScheduleOffsetService.getAllAsMap();
        List<String> boosterRelatedMessages = configService.getConfig().getBoosterRelatedMessages();

        List<VisitRescheduleDto> dtos = new ArrayList<>();

        for (VisitBookingDetails details: detailsRecords.getRows()) {
            dtos.add(new VisitRescheduleDto(details, calculateEarliestAndLatestDate(details.getVisit(), offsetMap, boosterRelatedMessages)));
        }

        return new Records<>(detailsRecords.getPage(), detailsRecords.getTotal(), detailsRecords.getRecords(), dtos);
    }

    @Override
    public VisitRescheduleDto saveVisitReschedule(VisitRescheduleDto visitRescheduleDto, Boolean ignoreLimitation) {
        VisitBookingDetails visitBookingDetails = visitBookingDetailsDataService.findById(visitRescheduleDto.getVisitBookingDetailsId());

        if (visitBookingDetails == null) {
            throw new IllegalArgumentException("Cannot reschedule, because details for Visit not found");
        }

        Clinic clinic = visitBookingDetails.getClinic();

        if (clinic != null && !ignoreLimitation) {
            checkNumberOfPatients(visitRescheduleDto, clinic);
        }

        Visit visit = visitBookingDetails.getVisit();
        validateDate(visitRescheduleDto, visit);
        updateVisitPlannedDate(visit, visitRescheduleDto);

        return new VisitRescheduleDto(updateVisitDetailsWithDto(visitBookingDetails, visitRescheduleDto));
    }

    private void checkNumberOfPatients(VisitRescheduleDto dto, Clinic clinic) { //NO CHECKSTYLE CyclomaticComplexity

        List<VisitBookingDetails> visits = visitBookingDetailsDataService
                .findByClinicIdVisitPlannedDateAndType(clinic.getId(), dto.getPlannedDate(), dto.getVisitType());

        visitLimitationHelper.checkCapacityForVisitBookingDetails(dto.getPlannedDate(), clinic, dto.getVisitId());

        if (visits != null && !visits.isEmpty()) {
            int numberOfRooms = clinic.getNumberOfRooms();
            int maxVisits = visitLimitationHelper.getMaxVisitCountForVisitType(dto.getVisitType(), clinic);
            int patients = 0;

            Time startTime = dto.getStartTime();
            Time endTime = null;

            if (startTime != null) {
                endTime = calculateEndTime(startTime);
            }

            for (VisitBookingDetails visit : visits) {
                if (visit.getId().equals(dto.getVisitBookingDetailsId())) {
                    maxVisits++;
                } else if (startTime != null && visit.getStartTime() != null) {
                    if (startTime.isBefore(visit.getStartTime())) {
                        if (visit.getStartTime().isBefore(endTime)) {
                            patients++;
                        }
                    } else {
                        if (startTime.isBefore(visit.getEndTime())) {
                            patients++;
                        }
                    }
                }
            }

            if (visits.size() >= maxVisits) {
                throw new LimitationExceededException("The limit for this type of visit has been reached for this day");
            }
            if (patients >= numberOfRooms) {
                throw new LimitationExceededException("Too many visits at the same time");
            }
        }
    }

    private void validateDate(VisitRescheduleDto dto, Visit visit) {
        if (visit.getDate() != null) {
            throw new IllegalArgumentException("Cannot reschedule, because Visit already took place");
        }

        if (dto.getPlannedDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in the past");
        }

        Map<Long, Map<VisitType, VisitScheduleOffset>> offsetMap = visitScheduleOffsetService.getAllAsMap();
        List<String> boosterRelatedMessages = configService.getConfig().getBoosterRelatedMessages();

        Range<LocalDate> dateRange = calculateEarliestAndLatestDate(visit, offsetMap, boosterRelatedMessages);

        if (dateRange == null) {
            throw new IllegalArgumentException("Cannot calculate Earliest and Latest Date");
        }

        LocalDate earliestDate = dateRange.getMin();
        LocalDate latestDate = dateRange.getMax();

        if (dto.getPlannedDate().isBefore(earliestDate) || dto.getPlannedDate().isAfter(latestDate)) {
            throw new IllegalArgumentException(String.format("The date should be between %s and %s but is %s",
                    earliestDate, latestDate, dto.getPlannedDate()));
        }
    }

    private VisitBookingDetails updateVisitDetailsWithDto(VisitBookingDetails details, VisitRescheduleDto dto) {
        details.setStartTime(dto.getStartTime());
        details.setEndTime(calculateEndTime(dto.getStartTime()));
        return visitBookingDetailsDataService.update(details);
    }

    private Visit updateVisitPlannedDate(Visit visit, VisitRescheduleDto visitRescheduleDto) {
        visit.setMotechProjectedDate(visitRescheduleDto.getPlannedDate());

        if (ebodacEnrollmentService.checkIfEnrolledAndUpdateEnrollment(visit)) {
            ebodacEnrollmentService.reenrollSubject(visit);
        }

        return visitDataService.update(visit);
    }

    private Map<String, Object> getFields(String json) throws IOException {
        if (json == null) {
            return null;
        } else {
            return objectMapper.readValue(json, new TypeReference<LinkedHashMap>() {});  //NO CHECKSTYLE WhitespaceAround
        }
    }

    private Time calculateEndTime(Time startTime) {
        int endTimeHour = (startTime.getHour() + BookingAppConstants.TIME_OF_THE_VISIT) % BookingAppConstants.MAX_TIME_HOUR;
        return new Time(endTimeHour, startTime.getMinute());
    }

    private Range<LocalDate> calculateEarliestAndLatestDate(Visit visit, Map<Long, Map<VisitType, VisitScheduleOffset>> offsetMap, List<String> boosterRelatedMessages) {
        Map<VisitType, VisitScheduleOffset> visitTypeOffset = offsetMap.get(visit.getSubject().getStageId());

        if (visitTypeOffset == null) {
            return null;
        }

        VisitScheduleOffset offset = visitTypeOffset.get(visit.getType());

        if (offset == null) {
            return null;
        }

        String campaignName;
        Long stageId = visit.getSubject().getStageId();

        if (stageId != null && stageId > 1) {
            campaignName = visit.getType().getValue() + EbodacConstants.STAGE + stageId;
        } else {
            campaignName = visit.getType().getValue();
        }

        LocalDate vaccinationDate;

        if (boosterRelatedMessages.contains(campaignName)) {
            vaccinationDate = visit.getSubject().getBoosterVaccinationDate();
        } else {
            vaccinationDate = visit.getSubject().getPrimerVaccinationDate();
        }

        if (vaccinationDate != null) {
            LocalDate minDate = vaccinationDate.plusDays(offset.getEarliestDateOffset());
            LocalDate maxDate = vaccinationDate.plusDays(offset.getLatestDateOffset());
            return new Range<>(minDate, maxDate);
        }

        return null;
    }
}
