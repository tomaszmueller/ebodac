package org.motechproject.ebodac.service;

import org.joda.time.DateTime;

/**
 * Simple example of a service interface.
 */
public interface EbodacService {

    void sendUpdatedSubjects(String zetesUrl, String username, String password);

    void fetchCSVUpdates(String hostname, Integer port, String username, String password, String directory, DateTime afterDate);
}
