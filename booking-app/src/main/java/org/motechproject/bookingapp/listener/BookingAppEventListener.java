package org.motechproject.bookingapp.listener;

import org.apache.commons.lang.StringUtils;
import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.bookingapp.domain.UnscheduledVisit;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.repository.ClinicDataService;
import org.motechproject.bookingapp.repository.UnscheduledVisitDataService;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingAppEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingAppEventListener.class);

    @Autowired
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Autowired
    private UnscheduledVisitDataService unscheduledVisitDataService;

    @Autowired
    private ClinicDataService clinicDataService;

    @MotechListener(subjects = { EbodacConstants.SITE_ID_CHANGED_EVENT })
    public void siteIdChanged(MotechEvent event) {
        String subjectId = (String) event.getParameters().get(EbodacConstants.SUBJECT_ID);
        String siteId = (String) event.getParameters().get(EbodacConstants.SITE_ID);

        if (StringUtils.isNotBlank(subjectId) && StringUtils.isNotBlank(siteId)) {
            Clinic clinic = clinicDataService.findByExactSiteId(siteId);

            if (clinic != null) {
                List<VisitBookingDetails> visitBookingDetailsList = visitBookingDetailsDataService.findBySubjectId(subjectId);
                List<UnscheduledVisit> unscheduledVisitList = unscheduledVisitDataService.findByParticipantId(subjectId);

                for (VisitBookingDetails details : visitBookingDetailsList) {
                    details.setClinic(clinic);
                    visitBookingDetailsDataService.update(details);
                }
                for (UnscheduledVisit unscheduledVisit : unscheduledVisitList) {
                    unscheduledVisit.setClinic(clinic);
                    unscheduledVisitDataService.update(unscheduledVisit);
                }
            } else {
                LOGGER.warn("Cannot find Clinic with siteId: {} for Subject with id: {}", siteId, subjectId);
            }
        }
    }
}
