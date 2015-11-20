package org.motechproject.ebodac.uitest.test;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.motechproject.ebodac.uitest.helper.RAVESettingsHelper;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.VisitPage;
import static junit.framework.Assert.assertTrue;
public class ChangeVisitDateUiTest extends TestBase {
    private LoginPage loginPage;
    private HomePage homePage;
    private VisitPage visitPage;
    private EBODACPage ebodacPage;
    private ParticipantEditPage participantEditPage;
    private String L1adminUser;
    private String L1adminpassword;
    private String data;
    private UITestHttpClientHelper httpClientHelper;
    private String url;
    private RAVESettingsHelper raveSettingsHelper;
    @Before
    public void setUp() throws InterruptedException {
        L1adminUser = properties.getUserName();
        L1adminpassword = properties.getPassword();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        ebodacPage = new EBODACPage(driver);
        visitPage = new VisitPage(driver);
        participantEditPage = new ParticipantEditPage(driver);
        url = properties.getWebAppUrl();
        httpClientHelper = new UITestHttpClientHelper(url);
        raveSettingsHelper = new RAVESettingsHelper(driver);
        if(homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.login(L1adminUser, L1adminpassword);
        }
        if(url.contains("localhost")) {
            raveSettingsHelper.createNewRAVESettings();
            httpClientHelper.fetchCSV(L1adminUser, L1adminpassword);
        }
    }
    @Test //Test for EBODAC-519
    public void changeVisitDateTest() throws Exception {
        homePage.clickOnEbodac();
        ebodacPage.goToVisit();
        visitPage.clickVisit();
        participantEditPage.clickPlannedVisitDate();
        participantEditPage.chageVisit();
        if(participantEditPage.dateEnroll()){
            participantEditPage.clickOK();
            participantEditPage.enter();
        }
        participantEditPage.clickOK();
        data = participantEditPage.getChoosenData();
        assertTrue(participantEditPage.getChoosenData().contains(data));
    }
    @After
    public void tearDown() throws Exception {
        Thread.sleep(10000);
        logout();
    }
}