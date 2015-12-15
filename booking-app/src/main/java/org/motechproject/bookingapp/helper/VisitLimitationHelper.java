package org.motechproject.bookingapp.helper;

import org.joda.time.LocalDate;
import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.bookingapp.exception.LimitationExceededException;
import org.motechproject.bookingapp.repository.ScreeningDataService;
import org.motechproject.bookingapp.repository.UnscheduledVisitDataService;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
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
            int screeningCount = (int) screeningDataService.countFindByClinicIdDateAndScreeningId(date, clinic.getId(), screeningId);
            int unscheduledVisitCount = (int) unscheduledVisitDataService.countFindByClinicIdAndDateAndVisitId(date, clinic.getId(), unscheduledVisitId);
            int visitBookingDetailsCount = (int) visitBookingDetailsDataService.countFindByBookingPlannedDateAndClinicIdAndVisitId(date, clinic.getId(), visitBookingDetailsId);
            int visitCount = screeningCount + visitBookingDetailsCount + unscheduledVisitCount;
            if (visitCount >= clinic.getMaxCapacityByDay()) {
                throw new LimitationExceededException("The limit of the capacity by day in the clinic is reached");
            }
        }
    }
}
