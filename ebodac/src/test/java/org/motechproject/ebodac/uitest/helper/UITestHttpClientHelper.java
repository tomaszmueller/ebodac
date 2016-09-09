package org.motechproject.ebodac.uitest.helper;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.motechproject.ebodac.client.EbodacHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UITestHttpClientHelper extends EbodacHttpClient {

    public static final String PARTICIPANT_ID = "participantId";
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    public static final String GENDER = "gender";
    public static final String SITE_ID = "siteId";
    public static final String STAGE_ID = "stageId";

    public static final String WITHADRAWAL_STUDY_DATE = "withadrawalStudyDate";
    public static final String WITHADRAWAL_VAC_DATE = "withadrawalVacDate";

    public static final String SCREENING_PLANNING_DATE = "screeningPlanningDate";
    public static final String SCREENING_ACTUAL_DATE = "screeningActualDate";

    public static final String PRIME_PLANNING_DATE = "primePlanningDate";
    public static final String PRIME_ACTUAL_DATE = "primeActualDate";

    public static final String PRIME_FFUP_ACTUAL_DATE = "primeFUPActualDate";;
    public static final String PRIME_FFUP_PLANNING_DATE = "primePlanningDate";;

    public static final String BOOST_PLANNING_DATE = "boostPlanningDate";
    public static final String BOOST_ACTUAL_DATE = "boostActualDate";

    public static final String BOOST1FU_PLANNING_DATE = "boost1FUAPlanningDate";
    public static final String BOOST1FU_ACTUAL_DATE = "boost1FUActualDate";

    public static final String BOOST2FU_ACTUAL_DATE = "boost2FUActualDate";
    public static final String BOOST2FU_PLANNING_DATE = "boost2FUAPlanningDate";

    public static final String BOOST3FU_ACTUAL_DATE = "boost3FUActualDate";
    public static final String BOOST3FU_PLANNING_DATE = "boost3FUAPlanningDate";

    public static final String FIRST_LTV_ACTUAL_DATE = "firstLTVActualDate";
    public static final String FIRST_LTV_PLANNING_DATE = "firstLTVPlanningDate";

    public static final String SEC_LTV_ACTUAL_DATE = "secLTVActualDate";
    public static final String SEC_LTV_PLANNING_DATE = "secLTVPlanningDate";

    public static final String THIRD_LTV_ACTUAL_DATE = "thirdLTVActualDate";
    public static final String THIRD_LTV_PLANNING_DATE = "thirdLTVPlanningDate";

    private static final int MIN_RANDOM_NUM = 11;
    private static final int MAX_RANDOM_NUM = 99;

    private String serverURL;
    private String zetesRegistrationURL;
    private String fetchCsvUrl;
    private String importCsvUrl;
    private String sCsvFile;
    private Map<String, String> prop = new HashMap<String, String>() {

        private static final long serialVersionUID = 1L;

        {
            put(SITE_ID, "B05-SL10001");
            put(PARTICIPANT_ID, "9999999952");
            put(DATE_OF_BIRTH, "1981-10-09");
            put(GENDER, "M");
            put(STAGE_ID, "2");

            put(WITHADRAWAL_VAC_DATE, "");
            put(WITHADRAWAL_STUDY_DATE, "");

            put(SCREENING_ACTUAL_DATE, "2016-08-10");
            put(SCREENING_PLANNING_DATE, "");

            put(PRIME_ACTUAL_DATE, "");
            put(PRIME_PLANNING_DATE, "2016-09-03");

            put(PRIME_FFUP_ACTUAL_DATE, "");
            put(PRIME_FFUP_PLANNING_DATE, "2016-09-03");

            put(BOOST_ACTUAL_DATE, "");
            put(BOOST_PLANNING_DATE, "2016-10-03");

            put(BOOST1FU_ACTUAL_DATE, "");
            put(BOOST1FU_PLANNING_DATE, "2017-01-03");

            put(BOOST2FU_ACTUAL_DATE, "");
            put(BOOST2FU_PLANNING_DATE, "2017-03-03");

            put(BOOST3FU_ACTUAL_DATE, "");
            put(BOOST3FU_PLANNING_DATE, "2017-03-03");

            put(FIRST_LTV_ACTUAL_DATE, "");
            put(FIRST_LTV_PLANNING_DATE, "2017-06-03");

            put(SEC_LTV_ACTUAL_DATE, "");
            put(SEC_LTV_PLANNING_DATE, "2017-09-03");

            put(THIRD_LTV_ACTUAL_DATE, "");
            put(THIRD_LTV_PLANNING_DATE, "2017-12-03");

        }
    };

    private static final Logger LOGGER = LoggerFactory.getLogger(UITestHttpClientHelper.class);
    private static final String URL_IMPORT_CSV = "/module/ebodac/web-api/import-csv";
    private static final String URL_FETCH_CSV = "/module/ebodac/web-api/fetch-csv";
    private static final String URL_PARTICIPANT_REGISTRATION = "/module/ebodac/registration/submit";

    public UITestHttpClientHelper(String serverUrl) {
        try {

            if (serverUrl != null) {
                this.setServerURL(serverUrl);
                this.setZetesRegistrationURL(this.getServerURL() + URL_PARTICIPANT_REGISTRATION);
                this.setFetchCsvUrl(this.getServerURL() + URL_FETCH_CSV);
                this.setImportCsvUrl(this.getServerURL() + URL_IMPORT_CSV);
                this.setSCsvFile(this.generateCSVFileFromString());

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
        String sJSONAddParticipant = "";
        try {
            if (this.getZetesRegistrationURL() != null) {
                sJSONAddParticipant = "{\"subjectId\":\"" + participant.getParticipantId() + "\"," + "\"name\":\""
                        + participant.getName() + "\"," + "\"language\":\"" + participant.getLanguage() + "\","
                        + "\"phoneNumber\":\"" + participant.getPhoneNumber() + "\"," + "\"siteId\":\""
                        + participant.getSiteId() + "\"," + "\"siteName\":\"" + participant.getSiteName() + "\","
                        + "\"headOfHousehold\":\"" + participant.getHeadOfHousehold() + "\"," + "\"householdName\":\""
                        + participant.getHeadOfHousehold() + "\"," + "\"community\":\"" + participant.getCommunity()
                        + "\"," + "\"address\":\"" + participant.getAddress() + "\"}";
                status = null != sendJson(zetesRegistrationURL, sJSONAddParticipant, user, password);
            }
        } catch (NullPointerException e) {
            status = false;
            LOGGER.error("addParticipant - NPE - JSONString: " + sJSONAddParticipant + " . Reason : "
                    + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            status = false;
            LOGGER.error("addParticipant - Exc. - JSONString: " + sJSONAddParticipant + " . Reason : "
                    + e.getLocalizedMessage(), e);

        }
       
        return status;
    }

    public boolean addVisits(String userName, String password, Map<String, String> properties, String csvFileString) {
        boolean status = false;

        try {
            if (null != properties) {
                this.updateProperties(properties);
                this.setSCsvFile(this.generateCSVFileFromString());
            } else {
                setSCsvFile(csvFileString);
            }

            if (null != this.getSCsvFile()) {
                // Define the right url to access to the IMPORT-CSV
                this.sendCsvFile(this.getImportCsvUrl(), userName, password,
                        new ByteArrayInputStream(this.getSCsvFile().getBytes("UTF-8")));
                status = true;
            } else {
                status = false;
            }
        } catch (UnsupportedEncodingException e) {
            status = false;
            LOGGER.error("addVisits - UnsupportedEncodingException . Reason : " + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            status = false;
            LOGGER.error("addVisits - NullPointerException . Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            status = false;
            LOGGER.error("addVisits - Exception . Reason : " + e.getLocalizedMessage(), e);

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

    public Map<String, String> getProp() {
        return this.prop;
    }

    public void setProp(Map<String, String> prop) {
        this.prop = prop;
    }

    public String getSCsvFile() {
        return this.sCsvFile;
    }

    public void setSCsvFile(String sCsvFile) {
        this.sCsvFile = sCsvFile;
    }

    private void updateProperties(Map<String, String> newProps) {
        // We add the properties for specific key

        Iterator<?> iter = newProps.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            this.getProp().put(entry.getKey().toString(), entry.getValue().toString());
        }

    }

    private String generateCSVFileFromString() {
        return "SiteNumber,Subject,BRTHDT,SEX,STAGE,PRMDT,BOOSTDT,VACDSDT,TRDSDT,VISIT,VISITDT,VISITDTPRJ\n"
                + this.getProp().get(SITE_ID) + "," + this.getProp().get(PARTICIPANT_ID) + ","
                + this.getProp().get(DATE_OF_BIRTH) + "," + this.getProp().get(GENDER) + ","
                + this.getProp().get(STAGE_ID) + "," + this.getProp().get(PRIME_ACTUAL_DATE) + ","
                + this.getProp().get(BOOST_ACTUAL_DATE) + "," + this.getProp().get(WITHADRAWAL_VAC_DATE) + ","
                + this.getProp().get(WITHADRAWAL_STUDY_DATE) + ",Screening," + this.getProp().get(SCREENING_ACTUAL_DATE)
                + "," + this.getProp().get(SCREENING_PLANNING_DATE) + "\n" + this.getProp().get(SITE_ID) + ","
                + this.getProp().get(PARTICIPANT_ID) + "," + this.getProp().get(DATE_OF_BIRTH) + ","
                + this.getProp().get(GENDER) + "," + this.getProp().get(STAGE_ID) + ","
                + this.getProp().get(PRIME_ACTUAL_DATE) + "," + this.getProp().get(BOOST_ACTUAL_DATE) + ","
                + this.getProp().get(WITHADRAWAL_VAC_DATE) + "," + this.getProp().get(WITHADRAWAL_STUDY_DATE)
                + ",Prime Vaccination Day," + this.getProp().get(PRIME_ACTUAL_DATE) + ","
                + this.getProp().get(PRIME_PLANNING_DATE) + "\n" + this.getProp().get(SITE_ID) + ","
                + this.getProp().get(PARTICIPANT_ID) + "," + this.getProp().get(DATE_OF_BIRTH) + ","
                + this.getProp().get(GENDER) + "," + this.getProp().get(STAGE_ID) + ","
                + this.getProp().get(PRIME_ACTUAL_DATE) + "," + this.getProp().get(BOOST_ACTUAL_DATE) + ","
                + this.getProp().get(WITHADRAWAL_VAC_DATE) + "," + this.getProp().get(WITHADRAWAL_STUDY_DATE)
                + ",Prime Vaccination First Follow-up visit," + this.getProp().get(PRIME_FFUP_ACTUAL_DATE) + ","
                + this.getProp().get(PRIME_FFUP_PLANNING_DATE) + "\n" + this.getProp().get(SITE_ID) + ","
                + this.getProp().get(PARTICIPANT_ID) + "," + this.getProp().get(DATE_OF_BIRTH) + ","
                + this.getProp().get(GENDER) + "," + this.getProp().get(STAGE_ID) + ","
                + this.getProp().get(PRIME_ACTUAL_DATE) + "," + this.getProp().get(BOOST_ACTUAL_DATE) + ","
                + this.getProp().get(WITHADRAWAL_VAC_DATE) + "," + this.getProp().get(WITHADRAWAL_STUDY_DATE)
                + ",Boost Vaccination Day," + this.getProp().get(BOOST_ACTUAL_DATE) + ","
                + this.getProp().get(BOOST_PLANNING_DATE) + "\n" + this.getProp().get(SITE_ID) + ","
                + this.getProp().get(PARTICIPANT_ID) + "," + this.getProp().get(DATE_OF_BIRTH) + ","
                + this.getProp().get(GENDER) + "," + this.getProp().get(STAGE_ID) + ","
                + this.getProp().get(PRIME_ACTUAL_DATE) + "," + this.getProp().get(BOOST_ACTUAL_DATE) + ","
                + this.getProp().get(WITHADRAWAL_VAC_DATE) + "," + this.getProp().get(WITHADRAWAL_STUDY_DATE)
                + ",Boost Vaccination First Follow-up visit," + this.getProp().get(BOOST1FU_ACTUAL_DATE) + ","
                + this.getProp().get(BOOST1FU_PLANNING_DATE) + "\n" + this.getProp().get(SITE_ID) + ","
                + this.getProp().get(PARTICIPANT_ID) + "," + this.getProp().get(DATE_OF_BIRTH) + ","
                + this.getProp().get(GENDER) + "," + this.getProp().get(STAGE_ID) + ","
                + this.getProp().get(PRIME_ACTUAL_DATE) + "," + this.getProp().get(BOOST_ACTUAL_DATE) + ","
                + this.getProp().get(WITHADRAWAL_VAC_DATE) + "," + this.getProp().get(WITHADRAWAL_STUDY_DATE)
                + ",Boost Vaccination Second Follow-up visit," + this.getProp().get(BOOST2FU_ACTUAL_DATE) + ","
                + this.getProp().get(BOOST2FU_PLANNING_DATE) + "\n" + this.getProp().get(SITE_ID) + ","
                + this.getProp().get(PARTICIPANT_ID) + "," + this.getProp().get(DATE_OF_BIRTH) + ","
                + this.getProp().get(GENDER) + "," + this.getProp().get(STAGE_ID) + ","
                + this.getProp().get(PRIME_ACTUAL_DATE) + "," + this.getProp().get(BOOST_ACTUAL_DATE) + ","
                + this.getProp().get(WITHADRAWAL_VAC_DATE) + "," + this.getProp().get(WITHADRAWAL_STUDY_DATE)
                + ",Boost Vaccination Third Follow-up visit," + this.getProp().get(BOOST3FU_ACTUAL_DATE) + ","
                + this.getProp().get(BOOST3FU_PLANNING_DATE) + "\n" + this.getProp().get(SITE_ID) + ","
                + this.getProp().get(PARTICIPANT_ID) + "," + this.getProp().get(DATE_OF_BIRTH) + ","
                + this.getProp().get(GENDER) + "," + this.getProp().get(STAGE_ID) + ","
                + this.getProp().get(PRIME_ACTUAL_DATE) + "," + this.getProp().get(BOOST_ACTUAL_DATE) + ","
                + this.getProp().get(WITHADRAWAL_VAC_DATE) + "," + this.getProp().get(WITHADRAWAL_STUDY_DATE)
                + ",First Long-term Follow-up visit," + this.getProp().get(FIRST_LTV_ACTUAL_DATE) + ","
                + this.getProp().get(FIRST_LTV_PLANNING_DATE) + "\n" + this.getProp().get(SITE_ID) + ","
                + this.getProp().get(PARTICIPANT_ID) + "," + this.getProp().get(DATE_OF_BIRTH) + ","
                + this.getProp().get(GENDER) + "," + this.getProp().get(STAGE_ID) + ","
                + this.getProp().get(PRIME_ACTUAL_DATE) + "," + this.getProp().get(BOOST_ACTUAL_DATE) + ","
                + this.getProp().get(WITHADRAWAL_VAC_DATE) + "," + this.getProp().get(WITHADRAWAL_STUDY_DATE)
                + ",Second Long-term Follow-up visit," + this.getProp().get(SEC_LTV_ACTUAL_DATE) + ","
                + this.getProp().get(SEC_LTV_PLANNING_DATE) + "\n" + this.getProp().get(SITE_ID) + ","
                + this.getProp().get(PARTICIPANT_ID) + "," + this.getProp().get(DATE_OF_BIRTH) + ","
                + this.getProp().get(GENDER) + "," + this.getProp().get(STAGE_ID) + ","
                + this.getProp().get(PRIME_ACTUAL_DATE) + "," + this.getProp().get(BOOST_ACTUAL_DATE) + ","
                + this.getProp().get(WITHADRAWAL_VAC_DATE) + "," + this.getProp().get(WITHADRAWAL_STUDY_DATE)
                + ",Third Long-term Follow-up visit," + this.getProp().get(THIRD_LTV_ACTUAL_DATE) + ","
                + this.getProp().get(THIRD_LTV_PLANNING_DATE) + "\n";

    }

    public String generateNewParticipantId(String originalId) {
        StringBuffer result = new StringBuffer();
        try {

            Integer newVal = new Integer(getRandomArbitary(MIN_RANDOM_NUM, MAX_RANDOM_NUM));
            result.append(originalId.substring(0, originalId.length() - 2)).append(newVal);
        } catch (NullPointerException e) {
            LOGGER.error("generateNewParticipantId - NPE . Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            LOGGER.error("generateNewParticipantId - Exception . Reason : " + e.getLocalizedMessage(), e);

        }
        return result.toString();
    }

    /**
     * Returns a random number between min and max
     */
    private int getRandomArbitary(int min, int max) {
        return (int) (Math.round(Math.random() * (max - min) + min));
    }

}
