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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("bookingAppLifecycleListener")
public class BookingAppLifecycleListenerImpl implements BookingAppLifecycleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingAppLifecycleListenerImpl.class);

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
            if (clinic != null) {
                visitBookingDetails.setClinic(clinic);
            } else {
                LOGGER.warn("Cannot find Clinic with siteId: {} for Subject with id: {}", subject.getSiteId(), subject.getSubjectId());
            }
        }

        visitBookingDetailsDataService.create(visitBookingDetails);
    }

    @Override
    public void addClinicToVisitBookingDetails(Clinic clinic) {
        List<VisitBookingDetails> visitBookingDetailsList = visitBookingDetailsDataService.findByExactParticipantSiteId(clinic.getSiteId());

        for (VisitBookingDetails details : visitBookingDetailsList) {
            details.setClinic(clinic);
            visitBookingDetailsDataService.update(details);
        }
    }
}
