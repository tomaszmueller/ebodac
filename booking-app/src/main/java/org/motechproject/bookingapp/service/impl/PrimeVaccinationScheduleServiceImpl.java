package org.motechproject.bookingapp.service.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.LocalDate;
import org.motechproject.bookingapp.constants.BookingAppConstants;
import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.bookingapp.domain.PrimeVaccinationScheduleDto;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.exception.LimitationExceededException;
import org.motechproject.bookingapp.helper.VisitLimitationHelper;
import org.motechproject.bookingapp.repository.ClinicDataService;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.bookingapp.service.PrimeVaccinationScheduleService;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.commons.date.model.Time;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.VisitDataService;
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

@Service
public class PrimeVaccinationScheduleServiceImpl implements PrimeVaccinationScheduleService {

    @Autowired
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Autowired
    private ClinicDataService clinicDataService;

    @Autowired
    private VisitLimitationHelper visitLimitationHelper;

    @Autowired
    private SubjectDataService subjectDataService;

    @Autowired
    private VisitDataService visitDataService;

    @Autowired
    private LookupService lookupService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Records<PrimeVaccinationScheduleDto> getPrimeVaccinationScheduleRecords(BookingGridSettings settings) throws IOException {
        QueryParams queryParams = QueryParamsBuilder.buildQueryParams(settings, getFields(settings.getFields()));
        return lookupService.getEntities(PrimeVaccinationScheduleDto.class,
                VisitBookingDetails.class, settings.getLookup(), settings.getFields(), queryParams);
    }

    @Override
    public PrimeVaccinationScheduleDto createOrUpdateWithDto(PrimeVaccinationScheduleDto dto, Boolean ignoreLimitation) {
        VisitBookingDetails primeDetails = visitBookingDetailsDataService.findById(dto.getVisitBookingDetailsId());
        VisitBookingDetails screeningDetails = getScreeningDetails(primeDetails);

        if (primeDetails == null || screeningDetails == null) {
            throw new IllegalArgumentException("Cannot save, because details for Visit not found");
        }

        Clinic clinic = primeDetails.getClinic();

        validateDate(dto);

        if (clinic != null && !ignoreLimitation) {
            checkNumberOfPatients(dto, clinic);
        }

        return new PrimeVaccinationScheduleDto(updateVisitWithDto(primeDetails, screeningDetails, dto));
    }

    @Override
    public List<PrimeVaccinationScheduleDto> getPrimeVaccinationScheduleRecords() {
        List<PrimeVaccinationScheduleDto> primeVacDtos = new ArrayList<>();
        createEmptyVisitsForSubjectWithoutPrimeVacDate();
        List<VisitBookingDetails> detailsList = visitBookingDetailsDataService
                .findByParticipantNamePrimeVaccinationDateAndVisitTypeAndBookingPlannedDateEq(".", null, VisitType.PRIME_VACCINATION_DAY, null);

        for (VisitBookingDetails details : detailsList) {
            primeVacDtos.add(new PrimeVaccinationScheduleDto(details));
        }

        return primeVacDtos;
    }

    private VisitBookingDetails updateVisitWithDto(VisitBookingDetails primeDetails, VisitBookingDetails screeningDetails,
                                                   PrimeVaccinationScheduleDto dto) {
        primeDetails.setStartTime(dto.getStartTime());
        primeDetails.setEndTime(calculateEndTime(dto.getStartTime()));
        primeDetails.setBookingPlannedDate(dto.getDate());
        primeDetails.getSubjectBookingDetails().setFemaleChildBearingAge(dto.getFemaleChildBearingAge());
        primeDetails.setIgnoreDateLimitation(dto.getIgnoreDateLimitation());

        screeningDetails.setBookingActualDate(dto.getBookingScreeningActualDate());

        visitBookingDetailsDataService.update(screeningDetails);
        return visitBookingDetailsDataService.update(primeDetails);
    }

    private Map<String, Object> getFields(String json) throws IOException {
        if (json == null) {
            return null;
        } else {
            return objectMapper.readValue(json, new TypeReference<LinkedHashMap>() {}); //NO CHECKSTYLE WhitespaceAround
        }
    }

    private void checkNumberOfPatients(PrimeVaccinationScheduleDto dto, Clinic clinic) {

        List<VisitBookingDetails> visits = visitBookingDetailsDataService.findByBookingPlannedDateClinicIdAndVisitType(dto.getDate(),
                clinic.getId(), VisitType.PRIME_VACCINATION_DAY);

        visitLimitationHelper.checkCapacityForVisitBookingDetails(dto.getDate(), clinic, dto.getVisitId());
        if (visits != null) {
            int numberOfRooms = clinic.getNumberOfRooms();
            int maxVisits = clinic.getMaxPrimeVisits();
            int patients = 0;

            for (VisitBookingDetails visit : visits) {
                if (visit.getId().equals(dto.getVisitBookingDetailsId())) {
                    maxVisits++;
                } else {
                    Time startTime = dto.getStartTime();
                    Time endTime = calculateEndTime(startTime);

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
                throw new LimitationExceededException("The booking limit for this type of visit has been reached");
            }
            if (patients >= numberOfRooms) {
                throw new LimitationExceededException("Too many visits at the same time");
            }
        }
    }

    private void validateDate(PrimeVaccinationScheduleDto dto) {
        if (dto.getBookingScreeningActualDate() == null) {
            throw new IllegalArgumentException("Screening Date cannot be empty");
        }
        if (dto.getDate() == null) {
            throw new IllegalArgumentException("Prime Vaccination Planned Date cannot be empty");
        }
        if (dto.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("The date can not be in past");
        }

        if (!dto.getIgnoreDateLimitation()) {

            LocalDate actualScreeningDate = dto.getBookingScreeningActualDate();

            LocalDate earliestDate = dto.getFemaleChildBearingAge() != null && dto.getFemaleChildBearingAge()
                    ? actualScreeningDate.plusDays(BookingAppConstants.EARLIEST_DATE_IF_FEMALE_CHILD_BEARING_AGE)
                    : actualScreeningDate.plusDays(BookingAppConstants.EARLIEST_DATE);
            LocalDate latestDate = actualScreeningDate.plusDays(BookingAppConstants.LATEST_DATE);

            if (dto.getDate().isBefore(earliestDate) || dto.getDate().isAfter(latestDate)) {
                throw new IllegalArgumentException(String.format("The date should be between %s and %s but is %s",
                        earliestDate, latestDate, dto.getDate()));
            }
        }
    }

    private VisitBookingDetails getScreeningDetails(VisitBookingDetails visitBookingDetails) {
        if (visitBookingDetails != null) {
            for (VisitBookingDetails details : visitBookingDetails.getSubjectBookingDetails().getVisitBookingDetailsList()) {
                if (VisitType.SCREENING.equals(details.getVisit().getType())) {
                    return details;
                }
            }
        }
        return null;
    }

    private void createEmptyVisitsForSubjectWithoutPrimeVacDate() {
        List<Subject> subjects = subjectDataService.findByPrimerVaccinationDate(null);
        for (Subject subject : subjects) {
            Visit screeningVisit = visitDataService.findBySubjectIdAndType(subject.getSubjectId(), VisitType.SCREENING);
            Visit primeVisit = visitDataService.findBySubjectIdAndType(subject.getSubjectId(), VisitType.PRIME_VACCINATION_DAY);
            Visit followUpVisit = visitDataService.findBySubjectIdAndType(subject.getSubjectId(), VisitType.PRIME_VACCINATION_FIRST_FOLLOW_UP_VISIT);
            if (screeningVisit == null) {
                screeningVisit = new Visit();
                screeningVisit.setType(VisitType.SCREENING);
                screeningVisit.setSubject(subject);
                visitDataService.create(screeningVisit);
            }
            if (primeVisit == null) {
                primeVisit = new Visit();
                primeVisit.setSubject(subject);
                primeVisit.setType(VisitType.PRIME_VACCINATION_DAY);
                visitDataService.create(primeVisit);
            }
            if (followUpVisit == null) {
                followUpVisit = new Visit();
                followUpVisit.setSubject(subject);
                followUpVisit.setType(VisitType.PRIME_VACCINATION_FIRST_FOLLOW_UP_VISIT);
                visitDataService.create(followUpVisit);
            }
        }
    }

    private Time calculateEndTime(Time startTime) {
        int endTimeHour = (startTime.getHour() + BookingAppConstants.TIME_OF_THE_VISIT) % BookingAppConstants.MAX_TIME_HOUR;
        return new Time(endTimeHour, startTime.getMinute());
    }
}
