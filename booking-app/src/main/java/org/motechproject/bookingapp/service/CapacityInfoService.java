package org.motechproject.bookingapp.service;

import org.motechproject.bookingapp.dto.CapacityInfoDto;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.ebodac.web.domain.Records;

public interface CapacityInfoService {

    Records<CapacityInfoDto> getCapacityInfoRecords(BookingGridSettings settings);
}
