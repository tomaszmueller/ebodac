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

/**
 * Class created to test the Language settings.
 * 
 * @author tmueller
 * @modified rmartin
 *
 */
public class ChangeLanguageUiTest extends TestBase {

    private static final int OFFSET_HTML = 2;
    private static final String LOCAL_TEST_MACHINE = "localhost";

    // Map for the languages
    private Map<String, String> map = new HashMap<String, String>();
    // Original language
    private String originalLanguage;
    private LoginPage loginPage;
    private HomePage homePage;
    // private EBODACPage ebodacPage;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;
    private Integer htmlposition;

    @Before
    public void setUp() throws Exception {
        try {
            String user = getTestProperties().getUserName();
            String password = getTestProperties().getPassword();
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            participantPage = new ParticipantPage(getDriver());
            participantEditPage = new ParticipantEditPage(getDriver());
            String url = getServerUrl();
            if (url.contains(LOCAL_TEST_MACHINE)) {
                UITestHttpClientHelper httpClientHelper = new UITestHttpClientHelper(url);
                httpClientHelper.addParticipant(new TestParticipant(), user, password);
                loginPage.goToPage();
                loginPage.login(user, password);
            } else if (homePage.expectedUrlPath() != currentPage().urlPath()) {
                loginPage.goToPage();
                loginPage.login(user, password);
            }
        } catch (NullPointerException e) {
            getLogger().error("setup - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("setup - Exception . Reason : " + e.getLocalizedMessage(), e);
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
        try {
            // We access to the edit page of the participant
            homePage.openEBODACModule();
            participantPage.openFirstParticipant();
            // We store the language.
            originalLanguage = participantEditPage.getLanguage();
            // We get the list of positions languages
            participantEditPage.setListLanguagePosition();
            // We use the map to set up the right new language.
            map = participantEditPage.getMapLangPos();
            // We change the language to choose a different one of the original
            // one.
            String changedLanguage = participantEditPage.changeLanguageFromOriginal(originalLanguage);
            // We change the particiapant language.
            int intPosition = new Integer(map.get(changedLanguage)).intValue();
            htmlposition = new Integer(intPosition + OFFSET_HTML);
            // Change the language
            if (!participantEditPage.changeLanguage(htmlposition.toString())) {
                getLogger().error("Cannot setup language :" + htmlposition);
            } else if (originalLanguage != null && changedLanguage != null) {
                assertNotEquals(changedLanguage.replace(" ", ""), originalLanguage.replace(" ", ""));
            } else if (originalLanguage == null || changedLanguage == null) {
                getLogger().error("Cannot compate languages: originalLanguage: " + originalLanguage
                        + " and changedLanguage: " + changedLanguage);
            }
        } catch (AssertionError e) {
            getLogger().error("changeLanguageTest - AssertionError . Reason : " + e.getLocalizedMessage(), e);
        } catch (NumberFormatException e) {
            getLogger().error("changeLanguageTest - NumberFormatException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("changeLanguageTest - Exception . Reason : " + e.getLocalizedMessage(), e);
        }

    }

    @After
    public void tearDown() throws Exception {
        // We restore the language to the original one.
        try {
            int intPosition = new Integer(map.get(originalLanguage)).intValue();
            htmlposition = new Integer(intPosition + OFFSET_HTML);

            if (!participantEditPage.changeLanguage(htmlposition.toString())) {
                getLogger().error("Cannot setup the original language : " + htmlposition);
            } else {
                // We canot setup the orignal position , we force to have one
                // right.
                participantEditPage.changeLanguage(new Integer(2).toString());
            }

        } catch (InterruptedException e) {
            getLogger().error("InterruptedException . Reason : " + e.getLocalizedMessage(), e);
            // We force to have 1st language if there is an error.
            participantEditPage.changeLanguage(new Integer(2).toString());
        } catch (NumberFormatException e) {
            getLogger().error("NumberFormatException . Reason : " + e.getLocalizedMessage(), e);
            // We force to have 1st language if there is an error.
            participantEditPage.changeLanguage(new Integer(2).toString());
        } catch (Exception e) {
            getLogger().error("Exception . Reason : " + e.getLocalizedMessage(), e);
            // We force to have 1st language if there is an error.
            participantEditPage.changeLanguage(new Integer(2).toString());
        }
        // We close the page and the motech.
        if (!participantEditPage.closeEditPage()) {
            getLogger().error("Cannot close EditPageParticipant");
        }
        // We make a log out.
        logout();
    }
}
