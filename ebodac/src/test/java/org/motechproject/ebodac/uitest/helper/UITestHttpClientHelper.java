package org.motechproject.ebodac.uitest.helper;

import org.motechproject.ebodac.client.EbodacHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UITestHttpClientHelper extends EbodacHttpClient {
    private static final String URL_IMPORT_CSV = "/module/ebodac/web-api/import-csv";
    private static final String URL_FETCH_CSV = "/module/ebodac/web-api/fetch-csv";
    private static final String URL_PARTICIPANT_REGISTRATION = "/module/ebodac/registration/submit";
    private static final Logger LOGGER = LoggerFactory.getLogger(UITestHttpClientHelper.class);
    private String serverURL;
    private String zetesRegistrationURL;
    private String fetchCsvUrl;
    private String importCsvUrl;

    public UITestHttpClientHelper(String serverUrl) {
        try {

            if (serverUrl != null) {
                this.setServerURL(serverUrl);
                this.setZetesRegistrationURL(this.getServerURL() + URL_PARTICIPANT_REGISTRATION);
                this.setFetchCsvUrl(this.getServerURL() + URL_FETCH_CSV);
                this.setImportCsvUrl(this.getServerURL() + URL_IMPORT_CSV);

            } else {
                LOGGER.error("UITestHttpClientHelper -  serverUrl cannot build the constructor properly");
            }

        } catch (Exception e) {
            LOGGER.error("UITestHttpClientHelper -  Exception. serverUrl : " + serverUrl + " Reason :"
                    + e.getLocalizedMessage(), e);
        }

    }

    public boolean addParticipant(TestParticipant participant, String user, String password) {
        boolean status = false;
        try {
            if (this.getZetesRegistrationURL() != null) {
                status = sendJson(zetesRegistrationURL, "{\"subjectId\":\"" + participant.getParticipantId() + "\","
                        + "\"name\":\"" + participant.getName() + "\"," + "\"language\":\"" + participant.getLanguage()
                        + "\"," + "\"phoneNumber\":\"" + participant.getPhoneNumber() + "\"," + "\"siteId\":\""
                        + participant.getSiteId() + "\"," + "\"siteName\":\"" + participant.getSiteName() + "\","
                        + "\"headOfHousehold\":\"" + participant.getHeadOfHousehold() + "\"," + "\"householdName\":\""
                        + participant.getHeadOfHousehold() + "\"," + "\"community\":\"" + participant.getCommunity()
                        + "\"," + "\"address\":\"" + participant.getAddress() + "\"}", user, password) != null;
            }
        } catch (NullPointerException e) {
            status = false;
            LOGGER.error("addParticipant - NullPointerException . Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            status = false;
            LOGGER.error("addParticipant - Exception . Reason : " + e.getLocalizedMessage(), e);

        }

        return status;
    }

    public void fetchCSV(String userName, String password) {
        sendJson(this.getFetchCsvUrl(), "", userName, password);
    }

    public void importCsv(String userName, String password, String fileName) {
        sendCsvFile(this.getImportCsvUrl(), userName, password, getClass().getResourceAsStream(fileName));
    }

    public String getZetesRegistrationURL() {
        return this.zetesRegistrationURL;
    }

    public void setZetesRegistrationURL(String zetesRegistrationURL) {
        this.zetesRegistrationURL = zetesRegistrationURL;
    }

    public String getFetchCsvUrl() {
        return this.fetchCsvUrl;
    }

    public void setFetchCsvUrl(String fetchCsvUrl) {
        this.fetchCsvUrl = fetchCsvUrl;
    }

    public String getImportCsvUrl() {
        return this.importCsvUrl;
    }

    public void setImportCsvUrl(String importCsvUrl) {
        this.importCsvUrl = importCsvUrl;
    }

    public String getServerURL() {
        return this.serverURL;
    }

    public void setServerURL(String serverURL) {
        String result = serverURL;
        if (serverURL.endsWith("/")) {
            result = serverURL.substring(0, serverURL.lastIndexOf("/"));

        }
        this.serverURL = result;

    }

}
