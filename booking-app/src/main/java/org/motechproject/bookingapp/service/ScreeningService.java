package org.motechproject.bookingapp.service;

import org.motechproject.bookingapp.domain.Screening;
import org.motechproject.bookingapp.dto.ScreeningDto;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.ebodac.web.domain.Records;

import java.io.IOException;

public interface ScreeningService {

    Records<Screening> getScreenings(BookingGridSettings bookingGridSettings) throws IOException;

    Screening addOrUpdate(ScreeningDto screeningDto, Boolean ignoreLimitation);

    Screening add(ScreeningDto screeningDto, Boolean ignoreLimitation);

    Screening update(ScreeningDto screeningDto, Boolean ignoreLimitation);

    ScreeningDto getScreeningById(Long id);

    void cancelScreening(Long id);

    void activateScreening(Long id, Boolean ignoreLimitation);
}
