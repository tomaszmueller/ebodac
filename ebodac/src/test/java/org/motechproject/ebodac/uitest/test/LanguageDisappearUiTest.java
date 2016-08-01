package org.motechproject.ebodac.uitest.test;

import static org.junit.Assert.assertFalse;
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
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.ParticipantPage;
import org.apache.log4j.Logger;

public class LanguageDisappearUiTest extends TestBase {
    // Object initialization for log
    private static Logger log = Logger.getLogger(LanguageDisappearUiTest.class.getName());
    private LoginPage loginPage;
    private HomePage homePage;
    private String user;
    private String password;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;
    private static final int OFFSET_HTML = 2;
    private static final String TEST_LOCAL_MACHINE = "localhost";

    // Map for the languages
    private Map<String, String> map = new HashMap<String, String>();
    private UITestHttpClientHelper httpClientHelper;
    private String url;
    // Original language
    private String originalLanguage;

    @Before
    public void setUp() throws Exception {
        try {
            user = getTestProperties().getUserName();
            password = getTestProperties().getPassword();
            loginPage = new LoginPage(getDriver());

            url = getServerUrl();
            if (url.contains(TEST_LOCAL_MACHINE)) {
                httpClientHelper = new UITestHttpClientHelper(url);
                httpClientHelper.addParticipant(new TestParticipant(), user, password);
                loginPage.goToPage();
                loginPage.login(user, password);
            } else if (homePage.expectedUrlPath() != currentPage().urlPath()) {
                loginPage.goToPage();
                loginPage.login(user, password);
            }
        } catch (NullPointerException e) {
            log.error("setUp - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            log.error("setUp - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test
    public void languagedisappearTest() throws Exception {
        try {
            // Setup the pages
            homePage = new HomePage(getDriver());

            // Access to the page
            homePage.openEBODACModule();
            participantPage = new ParticipantPage(getDriver());
            participantPage.openFirstParticipant();
            participantEditPage = new ParticipantEditPage(getDriver());
            // We store the language.
            originalLanguage = participantEditPage.getLanguage();

            assertFalse(participantEditPage.changeLanguage("1"));

        } catch (AssertionError e) {
            log.error("languagedisappearTest - AssertionError - Reason : " + e.getLocalizedMessage(), e);

        } catch (InterruptedException e) {
            log.error("languagedisappearTest - InterruptedException - Reason : " + e.getLocalizedMessage(), e);

        } catch (NullPointerException e) {
            log.error("languagedisappearTest - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            log.error("languagedisappearTest - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        // We need to restore the original language.
        try {
            // We get the list of positions languages
            participantEditPage.setListLanguagePosition();
            // We use the map to set up the right new language.
            map = participantEditPage.getMapLangPos();
            int intPosition = -1;
            if (!originalLanguage.trim().trim().isEmpty() && originalLanguage != null) {
                intPosition = new Integer(map.get(originalLanguage)).intValue();
                Integer htmlposition = new Integer(intPosition + OFFSET_HTML);
                if (!participantEditPage.changeLanguage(htmlposition.toString())) {
                    log.error("Cannot setup the original language : " + htmlposition);
                }
            } else {
                // We cannot setup the original position , we force to have one
                // right.
                participantEditPage.changeLanguage(new Integer(2).toString());
            }
            // we restore the original language

        } catch (InterruptedException e) {
            log.error("InterruptedException . Reason : " + e.getLocalizedMessage(), e);
            // We force to have 1st language if there is an error.
            participantEditPage.changeLanguage(new Integer(2).toString());
        } catch (NumberFormatException e) {
            log.error("NumberFormatException . Reason : " + e.getLocalizedMessage(), e);
            // We force to have 1st language if there is an error.
            participantEditPage.changeLanguage(new Integer(2).toString());
        } catch (Exception e) {
            log.error("Exception . Reason : " + e.getLocalizedMessage(), e);
            // We force to have 1st language if there is an error.
            participantEditPage.changeLanguage(new Integer(2).toString());
        }
        // We close the page and the motech.
        if (!participantEditPage.closeEditPage()) {
            log.error("Cannot close EditPageParticipant");
        }
        // We make a log out.

        logout();
    }
}
