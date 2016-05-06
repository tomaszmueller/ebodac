package org.motechproject.ebodac.uitest.helper;

import org.motechproject.ebodac.client.EbodacHttpClient;

public class UITestHttpClientHelper extends EbodacHttpClient {

    private String serverURL;
    private String zatesRegistrationURL;
    private String fetchCsvUrl;
    public UITestHttpClientHelper(String serverUrl) {
        serverURL = serverUrl.substring(0, serverUrl.lastIndexOf("/"));
        zatesRegistrationURL = serverURL + "/ebodac/registration/submit";
        fetchCsvUrl = serverURL + "/ebodac/web-api/fetch-csv";
    }

    public void addParticipant(TestParticipant participant , String user , String password) {
        sendJson(zatesRegistrationURL,
                "{\"subjectId\":\"" + participant.getId() + "\"," +
                        "\"name\":\"" + participant.getName() + "\"," +
                        "\"language\":\"" + participant.getLanguage() + "\"," +
                        "\"phoneNumber\":\"" + participant.getPhoneNumber() + "\"," +
                        "\"siteId\":\"" + participant.getSiteId() + "\",\"headOfHousehold\":\"" + participant.getHeadOfHousehold() + "\"," +
                        "\"householdName\":\"" + participant.getHeadOfHousehold() + "\"," +
                        "\"community\":\"" + participant.getCommunity() + "\",\"address\":\"" + participant.getAddress() + "\"" +
                        "}" , user , password);
    }

    public void fetchCSV(String userName , String password) {
        sendJson(fetchCsvUrl, "", userName, password);
    }
}
