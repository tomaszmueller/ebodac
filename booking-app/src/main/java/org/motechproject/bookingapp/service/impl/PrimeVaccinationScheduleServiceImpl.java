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
import org.motechproject.ebodac.domain.VisitType;
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
    private VisitDataService visitDataService;

    @Autowired
    private LookupService lookupService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Records<PrimeVaccinationScheduleDto> getPrimeVaccinationScheduleRecords(BookingGridSettings settings) throws IOException {
        QueryParams queryParams = QueryParamsBuilder.buildQueryParams(settings, getFields(settings.getFields()));
        return (Records<PrimeVaccinationScheduleDto>) lookupService.getEntities(PrimeVaccinationScheduleDto.class,
                VisitBookingDetails.class, settings.getLookup(), settings.getFields(), queryParams);
    }

    @Override
    public PrimeVaccinationScheduleDto createOrUpdateWithDto(PrimeVaccinationScheduleDto dto, Boolean ignoreLimitation) {
        VisitBookingDetails primeDetails = visitBookingDetailsDataService.findById(dto.getVisitBookingDetailsId());
        VisitBookingDetails screeningDetails = getScreeningDetails(primeDetails);

        if (primeDetails == null || screeningDetails == null) {
            throw new IllegalArgumentException("Cannot save, because details for Visit not found");
        }

        if (!ignoreLimitation) {
            checkNumberOfPatients(dto);
        }

        validateDate(dto);

        return new PrimeVaccinationScheduleDto(updateVisitWithDto(primeDetails, screeningDetails, dto));
    }

    @Override
    public List<PrimeVaccinationScheduleDto> getPrimeVaccinationScheduleRecords() {
        List<PrimeVaccinationScheduleDto> primeVacDtos = new ArrayList<>();
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
        primeDetails.setClinic(clinicDataService.findById(dto.getClinicId()));

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

    private void checkNumberOfPatients(PrimeVaccinationScheduleDto dto) {

        List<VisitBookingDetails> visits = visitBookingDetailsDataService.findByBookingPlannedDateClinicIdAndVisitType(dto.getDate(),
                dto.getClinicId(), VisitType.PRIME_VACCINATION_DAY);

        Clinic clinic = clinicDataService.findById(dto.getClinicId());
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
                throw new LimitationExceededException("Maximum amount of Prime Vaccination Visits exceeded for this day");
            }
            if (patients >= numberOfRooms) {
                throw new LimitationExceededException("Too many Patients at the same time");
            }
        }
        visitLimitationHelper.checkCapacityForVisitBookingDetails(dto.getDate(), clinic, dto.getVisitId());
    }

    private void validateDate(PrimeVaccinationScheduleDto dto) {
        if (dto.getBookingScreeningActualDate() == null) {
            throw new IllegalArgumentException("Screening Date cannot be empty");
        }
        if (dto.getDate() == null) {
            throw new IllegalArgumentException("Prime Vaccination Planned Date cannot be empty");
        }

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

    private Time calculateEndTime(Time startTime) {
        int endTimeHour = (startTime.getHour() + BookingAppConstants.TIME_OF_THE_VISIT) % BookingAppConstants.MAX_TIME_HOUR;
        return new Time(endTimeHour, startTime.getMinute());
    }
}
