package org.motechproject.ebodac.service;

/**
 * Simple example of a service interface.
 */
public interface EbodacService {

    void sendUpdatedSubjects(String zetesUrl, String username, String password);

    void fetchCSVUpdates();
}
