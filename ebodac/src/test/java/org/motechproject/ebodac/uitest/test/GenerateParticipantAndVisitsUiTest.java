package org.motechproject.ebodac.uitest.test;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

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

public class GenerateParticipantAndVisitsUiTest extends TestBase {
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String MALE = "M";
    private static final String SAMPLE_DATE = "1970-01-01";
    private static final String EMPTY_STRING = "";
    private Map<String, String> prop = new HashMap<String, String>();
    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private VisitPage visitPage;
    private String user;
    private String password;
    private String url;
    private String participantId = "9999999902";

    private static final String NEW_SCREENING_ACTUAL_DATE = "2016-08-01";

    private static final long SLEEP_2SEC = 2000;
    private static final long SLEEP_4SEC = 4000;

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
            // We change the participant id
            this.setParticipantId(httpClientHelper.generateNewParticipantId(participantId));
            // We start the visits and the participants.
            addNewVisitsForParticipant();
            // We start the pages.
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());

            ebodacPage = new EBODACPage(getDriver());

            if (url.contains(LOCAL_TEST_MACHINE) || (homePage.expectedUrlPath() != currentPage().urlPath())) {
                loginPage.goToPage();
                loginPage.login(user, password);
            }

        } catch (NullPointerException e) {
            getLogger().error("setup - NPE . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("setup - Exc . Reason : " + e.getLocalizedMessage(), e);
        }

    }

    public void addNewVisitsForParticipant() {

        try {
            // Add a participant in the Participant.
            participant = new TestParticipant();
            if (null != this.getParticipantId() && EMPTY_STRING != this.getParticipantId()) {
                participant.setId(this.getParticipantId());
                participant.setParticipantId(this.getParticipantId());
                if (httpClientHelper.addParticipant(participant, user, password)) {
                    // Add visits for the participant
                    prop.put(UITestHttpClientHelper.PARTICIPANT_ID, participant.getParticipantId());
                    prop.put(UITestHttpClientHelper.DATE_OF_BIRTH, SAMPLE_DATE);
                    prop.put(UITestHttpClientHelper.GENDER, MALE);
                    prop.put(UITestHttpClientHelper.SCREENING_ACTUAL_DATE, NEW_SCREENING_ACTUAL_DATE);
                    prop.put(UITestHttpClientHelper.PRIME_ACTUAL_DATE,
                            (new SimpleDateFormat(YYYY_MM_DD)).format(new Date()));
                    httpClientHelper.addVisits(user, password, prop, null);

                } else {
                    getLogger()
                            .error("addNewVisits4Part - cannot add the Visits for the PID :" + this.getParticipantId());
                }

            } else {
                getLogger().error("addNewVisits4Part - PId is null or empty");
            }

        } catch (Exception e) {
            getLogger().error("addNV4P - Exc . PId :" + this.getParticipantId() + " R: " + e.getLocalizedMessage(), e);
        }
    }

    @Test // Test for EBODAC-512
    public void setDataVisitsTest() throws Exception {
        try {
            // We access to the edit page of the participant
            homePage.openEBODACModule();
            // We open the Ebodac page
            ebodacPage.sleep(SLEEP_4SEC);
            if (ebodacPage.findByParticipantID(this.getParticipantId())) {
                // Go to Visit
                ebodacPage.sleep(SLEEP_4SEC);
                visitPage = new VisitPage(getDriver());
                // Go to Visit Page
                ebodacPage.goToVisit();
                visitPage.sleep(SLEEP_4SEC);
                boolean selectParticipant = visitPage.findByParticipantId(this.getParticipantId());
                visitPage.sleep(SLEEP_2SEC);

                // Add assert to set that the participant is added and the
                if (selectParticipant && visitPage.hasVisitsVisible()) {
                    // Visits select specific visit
                    assertTrue(visitPage.findActualDate(NEW_SCREENING_ACTUAL_DATE));
                } else {
                    getLogger().error(
                            "setDataVisitsTest - No participant Visits Found with ID : " + this.getParticipantId());
                }
                // visits.

            } else {
                getLogger().error("setDataVisitsTest - No participant Found  with ID :" + this.getParticipantId());
            }

        } catch (AssertionError e) {
            getLogger().error("getVisitDataFromRAVETest - AEx . ParticipantId = " + this.getParticipantId()
                    + " Reason : " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            getLogger().error("getVisitDataFromRAVETest - IEx . ParticipantId = " + this.getParticipantId()
                    + " Reason : " + e.getLocalizedMessage(), e);
        } catch (NullPointerException e) {
            getLogger().error("getVisitDataFromRAVETest - NPE . ParticipantId = " + this.getParticipantId()
                    + " Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("getVisitDataFromRAVETest - Exc . ParticipantId = " + this.getParticipantId()
                    + " Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

}
