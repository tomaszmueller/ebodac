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

import static org.junit.Assert.assertTrue;


public class DisplayOfSubjectVisitInTabularFormatUiTest extends TestBase {
    private LoginPage loginPage;
    private HomePage homePage;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;
    private String user;
    private String password;

    @Before
    public void setUp() {
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
    public void displayOfSubjectVisitInTabularFormatTest() throws Exception {
        homePage.openEBODACModule();
        participantPage.openFirstParticipant();
        assertTrue(participantEditPage.isTable());
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
