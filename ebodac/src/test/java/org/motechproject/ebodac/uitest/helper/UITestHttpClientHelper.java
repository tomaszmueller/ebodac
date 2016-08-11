package org.motechproject.ebodac.uitest.helper;

import org.motechproject.ebodac.client.EbodacHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UITestHttpClientHelper extends EbodacHttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(UITestHttpClientHelper.class);

    private String serverURL;
    private String zatesRegistrationURL;
    private String fetchCsvUrl;
    private String importCsvUrl;

    public UITestHttpClientHelper(String serverUrl) {
        serverURL = serverUrl.substring(0, serverUrl.lastIndexOf("/"));
        zatesRegistrationURL = serverURL + "/module/ebodac/registration/submit";
        fetchCsvUrl = serverURL + "/module/ebodac/web-api/fetch-csv";
        importCsvUrl = serverURL + "/module/ebodac/web-api/import-csv";
    }

    public boolean addParticipant(TestParticipant participant, String user, String password) {
        boolean status = false;
        try {
            status = sendJson(zatesRegistrationURL,
                    "{\"subjectId\":\"" + participant.getParticipantId() + "\"," + "\"name\":\"" + participant.getName()
                            + "\"," + "\"language\":\"" + participant.getLanguage() + "\"," + "\"phoneNumber\":\""
                            + participant.getPhoneNumber() + "\"," + "\"siteId\":\"" + participant.getSiteId() + "\","
                            + "\"siteName\":\"" + participant.getSiteName() + "\"," + "\"headOfHousehold\":\""
                            + participant.getHeadOfHousehold() + "\"," + "\"householdName\":\""
                            + participant.getHeadOfHousehold() + "\"," + "\"community\":\"" + participant.getCommunity()
                            + "\"," + "\"address\":\"" + participant.getAddress() + "\"}",
                    user, password) != null;
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
        LOGGER.error("fetchCSV URL :" + fetchCsvUrl);
        sendJson(fetchCsvUrl, "", userName, password);
    }

    public void importCsv(String userName, String password, String fileName) {
        LOGGER.error("importCsv URL :" + importCsvUrl);
        sendCsvFile(importCsvUrl, userName, password, getClass().getResourceAsStream(fileName));
    }
}
