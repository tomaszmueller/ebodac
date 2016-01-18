package org.motechproject.bookingapp.service;

import org.motechproject.bookingapp.domain.CapacityReportDto;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;

import java.util.List;

public interface ReportService {

    List<CapacityReportDto> generateCapacityReports(BookingGridSettings settings);
}
