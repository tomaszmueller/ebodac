package org.motechproject.bookingapp.service;

import org.motechproject.bookingapp.domain.Config;

public interface ConfigService {

    Config getConfig();

    void updateConfig(Config config);
}
