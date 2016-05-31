package org.motechproject.ebodac.uitest.test;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.ParticipantPage;


public class LanguageDisappearUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private String user;
    private String password;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;

    @Before
    public void setUp() throws Exception {
        user = getTestProperties().getUserName();
        password = getTestProperties().getPassword();
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        participantPage = new ParticipantPage(getDriver());
        participantEditPage = new ParticipantEditPage(getDriver());
        if (!StringUtils.equals(homePage.expectedUrlPath(), currentPage().urlPath())) {
            loginPage.goToPage();
            loginPage.login(user , password);
        }
    }

    @Test
    public void languagedisappearTest() throws Exception {
        homePage.openEBODACModule();
        homePage.changeUserLanguage();
        homePage.openEBODACModule();
        participantPage.openFirstParticipant();
        participantEditPage.changeLanguage("1");
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
