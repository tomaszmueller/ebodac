package org.motechproject.bookingapp.helper;

import org.joda.time.LocalDate;
import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.bookingapp.domain.ScreeningStatus;
import org.motechproject.bookingapp.exception.LimitationExceededException;
import org.motechproject.bookingapp.repository.ScreeningDataService;
import org.motechproject.bookingapp.repository.UnscheduledVisitDataService;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.ebodac.domain.enums.VisitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VisitLimitationHelper {

    @Autowired
    private ScreeningDataService screeningDataService;

    @Autowired
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Autowired
    private UnscheduledVisitDataService unscheduledVisitDataService;

    public void checkCapacityForUnscheduleVisit(LocalDate date, Clinic clinic, Long id) {
        checkCapacity(date, clinic, null, id, null);
    }

    public void checkCapacityForScreening(LocalDate date, Clinic clinic, Long id) {
        checkCapacity(date, clinic, id, null, null);
    }

    public void checkCapacityForVisitBookingDetails(LocalDate date, Clinic clinic, Long id) {
        checkCapacity(date, clinic, null, null, id);
    }

    private void checkCapacity(LocalDate date, Clinic clinic, Long screeningId, Long unscheduledVisitId, Long visitBookingDetailsId) {
        if (clinic != null && date != null) {
            int screeningCount = (int) screeningDataService.countFindByClinicIdDateAndScreeningIdAndStatus(date, clinic.getId(), screeningId, ScreeningStatus.ACTIVE);
            int unscheduledVisitCount = (int) unscheduledVisitDataService.countFindByClinicIdAndDateAndVisitId(date, clinic.getId(), unscheduledVisitId);
            int visitBookingDetailsCount = (int) visitBookingDetailsDataService.countFindByBookingPlannedDateAndClinicIdAndVisitId(date, clinic.getId(), visitBookingDetailsId);
            int visitCount = screeningCount + visitBookingDetailsCount + unscheduledVisitCount;
            if (visitCount >= clinic.getMaxCapacityByDay()) {
                throw new LimitationExceededException("The clinic capacity limit for this day has been reached");
            }
        }
    }

    public int getMaxVisitCountForVisitType(VisitType visitType, Clinic clinic) {  //NO CHECKSTYLE CyclomaticComplexity
        switch (visitType) {
            case PRIME_VACCINATION_DAY:
                return clinic.getMaxPrimeVisits();
            case PRIME_VACCINATION_FIRST_FOLLOW_UP_VISIT:
                return clinic.getMaxPrimeFirstFollowUpVisits();
            case PRIME_VACCINATION_SECOND_FOLLOW_UP_VISIT:
                return clinic.getMaxPrimeSecondFollowUpVisits();
            case BOOST_VACCINATION_DAY:
                return clinic.getMaxBoosterVisits();
            case BOOST_VACCINATION_FIRST_FOLLOW_UP_VISIT:
                return clinic.getMaxBoosterFirstFollowUpVisits();
            case BOOST_VACCINATION_SECOND_FOLLOW_UP_VISIT:
                return clinic.getMaxBoosterSecondFollowUpVisits();
            case BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT:
                return clinic.getMaxBoosterThirdFollowUpVisits();
            case FIRST_LONG_TERM_FOLLOW_UP_VISIT:
                return clinic.getMaxFirstLongTermFollowUpVisits();
            case SECOND_LONG_TERM_FOLLOW_UP_VISIT:
                return clinic.getMaxSecondLongTermFollowUpVisits();
            case THIRD_LONG_TERM_FOLLOW_UP_VISIT:
                return clinic.getMaxThirdLongTermFollowUpVisits();
            default:
                throw new IllegalArgumentException(String.format("Cannot find max visits number in Clinic for Visit Type: %s",
                        visitType.getValue()));
        }
    }
}
