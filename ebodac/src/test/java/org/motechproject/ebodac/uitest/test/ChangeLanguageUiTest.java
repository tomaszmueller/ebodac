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

import static org.junit.Assert.assertNotEquals;

import java.util.Random;

public class ChangeLanguageUiTest extends TestBase {

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
        if (url.contains("localhost")) {
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
        homePage.openEBODACModule();
        participantPage.openFirstParticipant();
        String originalLanguage = participantPage.getFirstParticipantLanguage();
        String changedPosition = new Integer(2 + (new Random()).nextInt(3)).toString();
        String changedLanguage = participantEditPage.changeLanguage(changedPosition);
        assertNotEquals(changedLanguage.replace(" ", ""), originalLanguage.replace(" ", ""));
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
