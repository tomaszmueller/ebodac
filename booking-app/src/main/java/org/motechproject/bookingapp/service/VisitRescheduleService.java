package org.motechproject.bookingapp.service;

import org.motechproject.bookingapp.dto.VisitRescheduleDto;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.ebodac.web.domain.Records;

import java.io.IOException;

public interface VisitRescheduleService {

    Records<VisitRescheduleDto> getVisitsRecords(BookingGridSettings settings) throws IOException;

    VisitRescheduleDto saveVisitReschedule(VisitRescheduleDto visitRescheduleDto, Boolean ignoreLimitation);
}
