package org.motechproject.ebodac.service;

/**
 * Service interface for getting and logging module settings.
 */
public interface EbodacSettingsService {

    String getSettingsValue(String key);

    void logInfoWithModuleSettings(String info);

}
