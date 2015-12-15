package org.motechproject.bookingapp.listener.impl;


import org.apache.commons.lang.StringUtils;
import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.bookingapp.domain.SubjectBookingDetails;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.listener.BookingAppLifecycleListener;
import org.motechproject.bookingapp.repository.ClinicDataService;
import org.motechproject.bookingapp.repository.SubjectBookingDetailsDataService;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("bookingAppLifecycleListener")
public class BookingAppLifecycleListenerImpl implements BookingAppLifecycleListener {

    @Autowired
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Autowired
    private SubjectBookingDetailsDataService subjectBookingDetailsDataService;

    @Autowired
    private ClinicDataService clinicDataService;

    @Override
    public void createVisitBookingDetails(Visit visit) {

        Subject subject = visit.getSubject();
        SubjectBookingDetails subjectBookingDetails = subjectBookingDetailsDataService.findBySubjectId(subject.getSubjectId());

        if (subjectBookingDetails == null) {
            subjectBookingDetails = new SubjectBookingDetails(subject);
        }

        VisitBookingDetails visitBookingDetails = new VisitBookingDetails(visit, subjectBookingDetails);

        if (StringUtils.isNotBlank(subject.getSiteId())) {
            Clinic clinic = clinicDataService.findByExactSiteId(subject.getSiteId());
            visitBookingDetails.setClinic(clinic);
        }

        visitBookingDetailsDataService.create(visitBookingDetails);
    }
}
