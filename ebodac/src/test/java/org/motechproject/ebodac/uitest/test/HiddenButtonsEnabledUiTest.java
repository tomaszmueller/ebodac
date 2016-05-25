package org.motechproject.ebodac.uitest.test;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.ParticipantPage;

import static org.junit.Assert.assertTrue;

public class HiddenButtonsEnabledUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private String l1adminUser;
    private String l1adminPassword;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;

    @Before
    public void setUp() throws Exception {
        UserPropertiesHelper userPropertiesHelper = new UserPropertiesHelper();
        l1adminUser = userPropertiesHelper.getAdminUserName();
        l1adminPassword = userPropertiesHelper.getAdminPassword();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        participantPage = new ParticipantPage(driver);
        participantEditPage = new ParticipantEditPage(driver);
        if (!StringUtils.equals(homePage.expectedUrlPath(), currentPage().urlPath())) {
            loginPage.login(l1adminUser , l1adminPassword);
        }
    }

    @Test
    public void hiddenButtonsEnabledTest() throws Exception {
        assertTrue(homePage.isEBODACModulePresent());
        assertTrue(homePage.isIVRModulePresent());
        assertTrue(homePage.isSMSModulePresent());
        homePage.openEBODACModule();
        participantPage.openFirstParticipant();
        assertTrue(participantEditPage.checkButtons());
    }

    @After
    public void tearDown() throws Exception {
        loginPage.logOut();
    }
}
