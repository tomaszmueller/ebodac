package org.motechproject.bookingapp.service.impl;

import org.joda.time.LocalDate;
import org.motechproject.bookingapp.constants.BookingAppConstants;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.domain.VisitScheduleOffset;
import org.motechproject.bookingapp.exception.VisitScheduleException;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.bookingapp.service.VisitScheduleOffsetService;
import org.motechproject.bookingapp.service.VisitScheduleService;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.enums.VisitType;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("visitScheduleService")
public class VisitScheduleServiceImpl implements VisitScheduleService {

    @Autowired
    private SubjectDataService subjectDataService;

    @Autowired
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Autowired
    private VisitScheduleOffsetService visitScheduleOffsetService;

    @Autowired
    private ConfigService configService;

    @Override
    public Map<String, String> getPrimeVaccinationDateAndDateRange(String subjectId) {

        Subject subject = subjectDataService.findBySubjectId(subjectId);

        LocalDate primeVacDate = null;
        LocalDate earliestDate = null;
        LocalDate latestDate = null;

        if (subject != null) {
            VisitBookingDetails details = getPrimerVaccinationDetails(subject);

            if (subject.getPrimerVaccinationDate() != null) {
                primeVacDate = subject.getPrimerVaccinationDate();
            } else if (details != null) {
                primeVacDate = details.getBookingActualDate();
            }

            LocalDate screeningDate = getScreeningDate(subject);
            if (screeningDate != null) {
                if (!isFemaleChildBearingAge(details)) {
                    earliestDate = screeningDate.plusDays(BookingAppConstants.EARLIEST_DATE);
                } else {
                    earliestDate = screeningDate.plusDays(BookingAppConstants.EARLIEST_DATE_IF_FEMALE_CHILD_BEARING_AGE);
                }
                latestDate = screeningDate.plusDays(BookingAppConstants.LATEST_DATE);
            }
        }

        Map<String, String> dates = new HashMap<>();
        dates.put("primeVacDate", primeVacDate != null ?
                primeVacDate.toString(BookingAppConstants.SIMPLE_DATE_FORMAT) : "");
        dates.put("earliestDate", earliestDate != null ?
                earliestDate.toString(BookingAppConstants.SIMPLE_DATE_FORMAT) : "");
        dates.put("latestDate", latestDate != null ?
                latestDate.toString(BookingAppConstants.SIMPLE_DATE_FORMAT) : "");

        return dates;

    }

    @Override
    public Map<String, String> calculatePlannedVisitDates(String subjectId, LocalDate primeVaccinationDate) {
        Map<String, String> plannedDates = new HashMap<>();
        List<VisitBookingDetails> visitBookingDetailsList = visitBookingDetailsDataService.findBySubjectId(subjectId);

        if (visitBookingDetailsList == null || visitBookingDetailsList.isEmpty()) {
            throw new VisitScheduleException(String.format("Cannot save Planned Dates, because Participant with Id: %s has no Visits", subjectId));
        }

        Subject subject = visitBookingDetailsList.get(0).getSubject();

        if (subject.getPrimerVaccinationDate() == null) {
            for (VisitBookingDetails details : calculatePlannedDates(visitBookingDetailsList, primeVaccinationDate, subject.getStageId())) {
                if (VisitType.PRIME_VACCINATION_DAY.equals(details.getVisit().getType())) {
                    plannedDates.put(details.getVisit().getType().toString(), details.getBookingActualDate().toString(BookingAppConstants.SIMPLE_DATE_FORMAT));
                } else {
                    plannedDates.put(details.getVisit().getType().toString(), details.getBookingPlannedDate().toString(BookingAppConstants.SIMPLE_DATE_FORMAT));
                }
            }
        } else {
            for (Visit visit : subject.getVisits()) {
                if (visit.getMotechProjectedDate() != null && !visit.getType().equals(VisitType.THIRD_LONG_TERM_FOLLOW_UP_VISIT)) {
                    plannedDates.put(visit.getType().toString(), visit.getMotechProjectedDate().toString(BookingAppConstants.SIMPLE_DATE_FORMAT));
                }
            }
        }

        return plannedDates;
    }

    @Override
    public void savePlannedVisitDates(String subjectId, LocalDate primeVaccinationDate) {
        List<VisitBookingDetails> visitBookingDetailsList = visitBookingDetailsDataService.findBySubjectId(subjectId);

        if (visitBookingDetailsList == null || visitBookingDetailsList.isEmpty()) {
            throw new VisitScheduleException(String.format("Cannot save Planned Dates, because Participant with Id: %s has no Visits", subjectId));
        }

        Subject subject = visitBookingDetailsList.get(0).getSubject();

        if (subject.getPrimerVaccinationDate() != null) {
            throw new VisitScheduleException(String.format("Cannot save Planned Dates, because Participant with Id: %s has been vaccinated", subjectId));
        }

        for (VisitBookingDetails details: calculatePlannedDates(visitBookingDetailsList, primeVaccinationDate, subject.getStageId())) {
            visitBookingDetailsDataService.update(details);
        }
    }

    private List<VisitBookingDetails> calculatePlannedDates(List<VisitBookingDetails> visitBookingDetailsList, LocalDate primeVaccinationDate, Long stageId) {

        if (primeVaccinationDate == null) {
            throw new VisitScheduleException("Cannot calculate Planned Dates, because Prime Vaccination Date is empty");
        }

        Long actualStageId = getActualStageId(stageId);

        Map<VisitType, VisitScheduleOffset> offsetMap = visitScheduleOffsetService.getAsMapByStageId(actualStageId);

        if (offsetMap == null || offsetMap.isEmpty()) {
            throw new VisitScheduleException(String.format("Cannot calculate Planned Dates, because no Visit Schedule Offset found for stageId: %s",
                    stageId.toString()));
        }

        List<VisitBookingDetails> detailsList = new ArrayList<>();
        LocalDate screeningDate = null;
        VisitBookingDetails primeVacDetails = null;

        for (VisitBookingDetails details: visitBookingDetailsList) {
            if (VisitType.SCREENING.equals(details.getVisit().getType())) {
                screeningDate = details.getVisit().getDate();
            } else if (VisitType.PRIME_VACCINATION_DAY.equals(details.getVisit().getType())) {
                details.setBookingActualDate(primeVaccinationDate);
                detailsList.add(details);
                primeVacDetails = details;
            } else if (VisitType.PRIME_VACCINATION_FIRST_FOLLOW_UP_VISIT.equals(details.getVisit().getType())) {
                VisitScheduleOffset offset = offsetMap.get(details.getVisit().getType());
                if (offset == null) {
                    throw new VisitScheduleException(String.format("Cannot calculate Planned Dates, because no Visit Schedule Offset found for visit: %s",
                            details.getVisit().getType().getValue()));
                }
                details.setBookingPlannedDate(primeVaccinationDate.plusDays(offset.getTimeOffset()));
                detailsList.add(details);
            }
        }

        validateDate(primeVaccinationDate, screeningDate, primeVacDetails);

        return detailsList;
    }

    private Long getActualStageId(Long stageId) {
        Long actualStageId = stageId;
        if (actualStageId == null) {
            actualStageId = configService.getConfig().getActiveStageId();
        }

        if (actualStageId == null) {
            throw new VisitScheduleException("Cannot calculate Planned Dates, because Participant stageId is empty");
        }

        return actualStageId;
    }

    private void validateDate(LocalDate date, LocalDate screeningDate, VisitBookingDetails details) {

        if (screeningDate == null) {
            throw new VisitScheduleException("Couldn't save Planned Dates, because Participant didn't participate in screening visit");
        }

        LocalDate earliestDate;
        LocalDate latestDate = screeningDate.plusDays(BookingAppConstants.LATEST_DATE);

        if (!isFemaleChildBearingAge(details)) {
            earliestDate = screeningDate.plusDays(BookingAppConstants.EARLIEST_DATE);
        } else {
            earliestDate = screeningDate.plusDays(BookingAppConstants.EARLIEST_DATE_IF_FEMALE_CHILD_BEARING_AGE);
        }

        if (date.isBefore(earliestDate) || date.isAfter(latestDate)) {
            throw new VisitScheduleException(String.format("The date should be between %s and %s but is %s",
                    earliestDate, latestDate, date));
        }
    }

    private boolean isFemaleChildBearingAge(VisitBookingDetails details) {
        return details != null && details.getSubjectBookingDetails().getFemaleChildBearingAge() != null
                && details.getSubjectBookingDetails().getFemaleChildBearingAge();
    }

    private LocalDate getScreeningDate(Subject subject) {
        LocalDate screeningDate = null;

        for (Visit visit : subject.getVisits()) {
            if (VisitType.SCREENING.equals(visit.getType())) {
                screeningDate = visit.getDate();
            }
        }

        if (screeningDate == null) {
            throw new VisitScheduleException(String.format("Couldn't save Planned Dates, because Participant with Id:" +
                    "%s didn't participate in screening visit", subject.getSubjectId()));
        }
        return screeningDate;
    }

    private VisitBookingDetails getPrimerVaccinationDetails(Subject subject) {
        return visitBookingDetailsDataService.findByParticipantIdAndVisitType(subject.getSubjectId(), VisitType.PRIME_VACCINATION_DAY);
    }
}
