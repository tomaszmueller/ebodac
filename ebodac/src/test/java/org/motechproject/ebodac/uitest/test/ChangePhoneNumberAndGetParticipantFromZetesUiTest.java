package org.motechproject.ebodac.uitest.test;

import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.ParticipantPage;

import java.lang.Exception;

import static org.junit.Assert.assertEquals;

public class ChangePhoneNumberAndGetParticipantFromZetesUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;
    private String l1AdminUser;
    private String l1AdminPassword;
    private String testNumber = "55577755";
    private String changedNumber;
    private UITestHttpClientHelper httpClientHelper;
    private String url;
    @Before
    public void setUp() {
        l1AdminUser = getTestProperties().getUserName();
        l1AdminPassword = getTestProperties().getPassword();
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        ebodacPage = new EBODACPage(getDriver());
        participantPage = new ParticipantPage(getDriver());
        participantEditPage = new ParticipantEditPage(getDriver());
        url = getServerUrl();
        if (url.contains("localhost")) {
            httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant() , l1AdminUser , l1AdminPassword);
        }
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.goToPage();
            loginPage.login(l1AdminUser, l1AdminPassword);
        }
    }

    @Test //Test for EBODAC-508/EBODAC-509
    public void changePhoneNumberTest() throws Exception {
        homePage.openEBODACModule();
        ebodacPage.showParticipants();
        participantPage.openFirstParticipant();
        participantEditPage.changePhoneNumber(testNumber);
        changedNumber = participantPage.getFirstParticipantNumber();
        assertEquals(changedNumber, testNumber);
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
