package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.ParticipantPage;



import static org.junit.Assert.assertEquals;

public class ChangeLanguageUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;
    private String user;
    private String password;
    private String testLanguage;
    private String changedLanguage;
    private UITestHttpClientHelper httpClientHelper;
    private String url;

    @Before
    public void setUp() {
        user = getTestProperties().getUserName();
        password = getTestProperties().getPassword();
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        ebodacPage = new EBODACPage(getDriver());
        participantPage = new ParticipantPage(getDriver());
        participantEditPage = new ParticipantEditPage(getDriver());
        url = getServerUrl();
        if (url.contains("localhost")) {
            httpClientHelper = new UITestHttpClientHelper(url);
            httpClientHelper.addParticipant(new TestParticipant() , user , password);
        }
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.goToPage();
            loginPage.login(user , password);
        }
    }

    @Test
    public void changeLanguageTest() throws Exception {
        homePage.openEBODACModule();
        participantPage.openFirstParticipant();
        testLanguage = participantEditPage.changeLanguage("1");
        changedLanguage = participantPage.getFirstParticipantLanguage();
        assertEquals(changedLanguage.replace(" ", ""), testLanguage.replace(" ", ""));
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
