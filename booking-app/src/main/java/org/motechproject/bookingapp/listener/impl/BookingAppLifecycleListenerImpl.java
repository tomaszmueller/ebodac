package org.motechproject.bookingapp.listener.impl;


import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.listener.BookingAppLifecycleListener;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.ebodac.domain.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("bookingAppLifecycleListener")
public class BookingAppLifecycleListenerImpl implements BookingAppLifecycleListener {

    @Autowired
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Override
    public void createVisitBookingDetails(Visit visit) {
        VisitBookingDetails visitBookingDetails = new VisitBookingDetails();
        visitBookingDetails.setVisit(visit);
        visitBookingDetailsDataService.create(visitBookingDetails);
    }
}
