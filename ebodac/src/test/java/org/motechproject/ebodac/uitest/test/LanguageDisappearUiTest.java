package org.motechproject.ebodac.uitest.test;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motech.page.LoginPage;
import org.motech.test.TestBase;
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
        user = properties.getUserName();
        password = properties.getPassword();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        participantPage = new ParticipantPage(driver);
        participantEditPage = new ParticipantEditPage(driver);
        if (!StringUtils.equals(homePage.expectedUrlPath(), currentPage().urlPath())) {
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
        loginPage.logOut();
    }
}
