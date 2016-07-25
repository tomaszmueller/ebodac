package org.motechproject.ebodac.uitest.test;

import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.ParticipantPage;
import java.util.HashMap;
import static org.junit.Assert.assertNotEquals;
import java.util.Map;
import org.apache.log4j.Logger;

public class ChangeLanguageUiTest extends TestBase {

    private static final int OFFSET_HTML = 2;
    private static final String LOCALHOST = "localhost";

    // Map for the languages
    private Map<String, String> map = new HashMap<String, String>();
    // Original language
    private String originalLanguage;
    // Object initialization for log
    private static Logger log = Logger.getLogger(ChangeLanguageUiTest.class.getName());
    private LoginPage loginPage;
    private HomePage homePage;
    // private EBODACPage ebodacPage;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;

    @Before
    public void setUp() {
        String user = getTestProperties().getUserName();
        String password = getTestProperties().getPassword();
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        participantPage = new ParticipantPage(getDriver());
        participantEditPage = new ParticipantEditPage(getDriver());
        String url = getServerUrl();
        if (url.contains(LOCALHOST)) {
            UITestHttpClientHelper httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant(), user, password);
        }
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.goToPage();
            loginPage.login(user, password);
        }
    }

    /**
     * Method : changeLanguageTest Description: This method changes the Language
     * of the participant and test if it is different.
     * 
     * @throws Exception
     */
    @Test
    public void changeLanguageTest() throws Exception {
        // We access to the edit page of the participant
        homePage.openEBODACModule();
        participantPage.openFirstParticipant();
        originalLanguage = participantEditPage.getLanguage();
        // We get the list of positions languages
        participantEditPage.setListLanguagePosition();
        // We use the map to set up the right new language.
        map = participantEditPage.getMapLangPos();
        // We change the language to choose a different one of the original one.
        String changedLanguage = participantEditPage.changeLanguageFromOriginal(originalLanguage);
        // We change the particiapant language.
        int intPosition = new Integer(map.get(changedLanguage)).intValue();
        Integer htmlposition = new Integer(intPosition + OFFSET_HTML);
        // Change the language
        if (!participantEditPage.changeLanguage(htmlposition.toString())) {
            log.error("Cannot setup language :" + htmlposition);
        } else {
            assertNotEquals(changedLanguage.replace(" ", ""), originalLanguage.replace(" ", ""));
        }

    }

    @After
    public void tearDown() throws Exception {
        // We restore the language to the original one.
        int intPosition = new Integer(map.get(originalLanguage)).intValue();
        Integer htmlposition = new Integer(intPosition + OFFSET_HTML);

        if (!participantEditPage.changeLanguage(htmlposition.toString())) {
            log.error("Cannot setup the original language : " + htmlposition);
        }

        // We close the page and the motech.
        if (!participantEditPage.closeEditPage()) {
            log.error("Cannot close EditPageParticipant");
        }
        logout();
    }
}
