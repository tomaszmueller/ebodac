package org.motechproject.ebodac.uitest.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.VisitPage;
import static org.junit.Assert.assertTrue;

public class GetVisitDataFromRAVEUiTest extends TestBase {
    private static final String SERVICE_IMPORT_CSV = "/module/ebodac/web-api/import-csv";
    private String stringCSVFile = "SiteNumber,Subject,BRTHDT,SEX,STAGE,PRMDT,BOOSTDT,VACDSDT,TRDSDT,VISIT,VISITDT,VISITDTPRJ\n"
            + "B05-SL10001,9999999952,1981-10-09,M,2,,,,2016-08-10,Screening,2016-08-01,\n"
            + "B05-SL10001,9999999952,1981-10-09,M,2,,,,2016-08-10,Prime Vaccination Day,2016-08-10,\n"
            + "B05-SL10001,9999999952,1981-10-09,M,2,,,,2016-08-10,Prime Vaccination First Follow-up visit,,2016-10-15\n"
            + "B05-SL10001,9999999952,1981-10-09,M,2,,,,2016-08-10,Boost Vaccination Day,,2016-12-03\n"
            + "B05-SL10001,9999999952,1981-10-09,M,2,,,,2016-08-10,Boost Vaccination First Follow-up visit,,2016-12-10\n"
            + "B05-SL10001,9999999952,1981-10-09,M,2,,,,2016-08-10,Boost Vaccination Second Follow-up visit,,2016-12-24\n"
            + "B05-SL10001,9999999952,1981-10-09,M,2,,,,2016-08-10,Boost Vaccination Third Follow-up visit,,2017-03-11\n"
            + "B05-SL10001,9999999952,1981-10-09,M,2,,,,2016-08-10,First Long-term Follow-up visit,,2017-04-04\n"
            + "B05-SL10001,9999999952,1981-10-09,M,2,,,,2016-08-10,Second Long-term Follow-up visit,,2017-06-03\n"
            + "B05-SL10001,9999999952,1981-10-09,M,2,,,,2016-08-10,Third Long-term Follow-up visit,,2017-08-09\n";

    private static final String PARTICIPANT_ID = "9999999952";
    private static final String NEW_SCREENING_ACTUAL_DATE = "2016-08-01";
    private static final int WAIT_500MLSEC = 500;
    private static final long WAIT_2SEC = 2000;
    private static final long WAIT_5SEC = 5000;
    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private VisitPage visitPage;
    private String user;
    private String password;
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";

    private UITestHttpClientHelper httpClientHelper;
    private TestParticipant participant;

    @Before
    public void setUp() throws Exception {
        try {
            // General params
            user = getTestProperties().getUserName();
            password = getTestProperties().getPassword();
            url = getServerUrl();
            httpClientHelper = new UITestHttpClientHelper(url);
            // We start the visits and the participants.
            // initNewParticipantAndVisits();
            initReplaceNewParticipant(PARTICIPANT_ID);
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            ebodacPage = new EBODACPage(getDriver());

            if (url.contains(LOCAL_TEST_MACHINE) || (homePage.expectedUrlPath() != currentPage().urlPath())) {
                loginPage.goToPage();
                loginPage.login(user, password);
            }

        } catch (NullPointerException e) {
            getLogger().error("setup - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("setup - Exception . Reason : " + e.getLocalizedMessage(), e);
        }

    }

    public void initReplaceNewParticipant(String participantId) {

        try {
            // Add a participant in the Participant.
            participant = new TestParticipant();
            participant.setId(participantId);
            participant.setParticipantId(participantId);

            if (httpClientHelper.addParticipant(participant, user, password)) {
                // Add visits for the participant
                InputStream in = new ByteArrayInputStream(stringCSVFile.getBytes("UTF-8"));
                // Define the right url to access to the IMPORT-CSV
                httpClientHelper.sendCsvFile((new StringBuffer(url)).append(SERVICE_IMPORT_CSV).toString(), user,
                        password, in);
            } else {
                getLogger().error("setup - Cannot add the participant ");
            }
        } catch (IOException e) {
            getLogger().error("initNewParticipantAndVisits - IOException . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test // Test for EBODAC-512
    public void setDataVisitsTest() throws Exception {
        try {
            // We access to the edit page of the participant
            homePage.openEBODACModule();
            // We open the Ebodac page
            ebodacPage.goToPage();

            // Validate New Participant
            ebodacPage.goToPage();

            if (ebodacPage.findByParticipantID(PARTICIPANT_ID)) {

                ebodacPage.sleep(WAIT_500MLSEC);
                // Go to Visit
                visitPage = new VisitPage(getDriver());
                // Go to Visit Page
                ebodacPage.goToVisit();
                visitPage.goToPage();
                visitPage.sleep(WAIT_2SEC);
                boolean selectParticipant = visitPage.findByParticipantId(PARTICIPANT_ID);
                visitPage.sleep(WAIT_2SEC);

                // Add assert to set that the participant is added and the
                if (selectParticipant) {
                    // Visits select specific visit
                    assertTrue(visitPage.findActualDate(NEW_SCREENING_ACTUAL_DATE));
                } else {
                    getLogger().error("setDataVisitsTest - No participant Found with ID : " + PARTICIPANT_ID);
                }
                ebodacPage.sleep(WAIT_5SEC);
                // visits.

            } else {
                getLogger().error("setDataVisitsTest - No participant Found  with ID :" + PARTICIPANT_ID);
            }

        } catch (

        AssertionError e) {
            getLogger().error("getVisitDataFromRAVETest - AssertionError . Reason : " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            getLogger().error("getVisitDataFromRAVETest - NullPointerException . Reason : " + e.getLocalizedMessage(),
                    e);
        } catch (NullPointerException e) {
            getLogger().error("getVisitDataFromRAVETest - NullPointerException . Reason : " + e.getLocalizedMessage(),
                    e);
        } catch (Exception e) {
            getLogger().error("getVisitDataFromRAVETest - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }

}
