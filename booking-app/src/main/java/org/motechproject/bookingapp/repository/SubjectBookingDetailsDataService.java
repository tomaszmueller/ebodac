package org.motechproject.bookingapp.repository;

import org.motechproject.bookingapp.domain.SubjectBookingDetails;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

public interface SubjectBookingDetailsDataService extends MotechDataService<SubjectBookingDetails> {

    @Lookup(name = "Find By exact Participant Id")
    SubjectBookingDetails findBySubjectId(@LookupField(name = "subject.subjectId") String subjectId);
}
