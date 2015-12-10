package org.motechproject.bookingapp.listener;

import org.apache.commons.lang.StringUtils;
import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.repository.ClinicDataService;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingAppEventListener {

    @Autowired
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

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

                for (VisitBookingDetails details : visitBookingDetailsList) {
                    details.setClinic(clinic);
                    visitBookingDetailsDataService.update(details);
                }
            }
        }
    }
}
