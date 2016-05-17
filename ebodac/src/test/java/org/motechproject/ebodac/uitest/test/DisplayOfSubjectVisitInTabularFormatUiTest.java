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
    public void displayOfSubjectVisitInTabularFormatTest() throws Exception {
        homePage.openEBODACModule();
        participantPage.openFirstParticipant();
        assertTrue(participantEditPage.isTable());
    }

    @After
    public void tearDown() throws Exception {
        loginPage.logOut();
    }
}
