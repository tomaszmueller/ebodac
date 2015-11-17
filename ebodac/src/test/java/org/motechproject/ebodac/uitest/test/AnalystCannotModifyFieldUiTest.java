package org.motechproject.ebodac.uitest.test;


import org.junit.Before;
import org.junit.Test;
import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.motechproject.ebodac.uitest.helper.CreateUsersHelper;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.ParticipantPage;

import static org.junit.Assert.assertFalse;

public class AnalystCannotModifyFieldUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;
    private String L1analystUser;
    private String L1analystpassword;
    private UserPropertiesHelper userPropertiesHelper;
    private UITestHttpClientHelper httpClientHelper;
    private CreateUsersHelper createUsersHelper;
    private String url;

    @Before
    public void setUp() throws  Exception {
        url = properties.getWebAppUrl();
        if(url.contains("localhost")) {
            createUsersHelper = new CreateUsersHelper(driver);
            createUsersHelper.createUsersWithLogin(properties);
        }
        ebodacPage = new EBODACPage(driver);
        participantPage = new ParticipantPage(driver);
        participantEditPage = new ParticipantEditPage(driver);
        userPropertiesHelper = new UserPropertiesHelper();
        L1analystUser = userPropertiesHelper.getAnalystUserName();
        L1analystpassword = userPropertiesHelper.getAnalystPassword();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        if(url.contains("localhost")) {
            httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant(),L1analystUser,L1analystpassword);
        }
        if(homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.login(L1analystUser, L1analystpassword);
        }
    }

    @Test
    public void analystCannotModifyFieldTest() throws InterruptedException {
        homePage.clickModules();
        homePage.openEBODACModule();
        ebodacPage.showParticipants();
        participantPage.openFirstParticipant();
        assertFalse(participantEditPage.enabled());
    }
}
