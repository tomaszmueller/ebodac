package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.RAVESettingsHelper;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.AdminPage;
import org.motechproject.ebodac.uitest.page.VisitPage;
import org.motechproject.ebodac.uitest.page.ServerLogPage;

import static org.junit.Assert.assertTrue;

@Ignore
public class GetVisitDataFromRAVEUiTest extends TestBase {
    private LoginPage loginPage;
    private HomePage homePage;
    private AdminPage adminPage;
    private EBODACPage ebodacPage;
    private VisitPage visitPage;
    private ServerLogPage serverLogPage;
    private String adminUser;
    private String adminPassword;
    private UITestHttpClientHelper httpClientHelper;
    private RAVESettingsHelper raveSettingsHelper;
    private String url;

    @Before
    public void setUp() {
        adminUser = getTestProperties().getUserName();
        adminPassword = getTestProperties().getPassword();
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        adminPage = new AdminPage(getDriver());
        ebodacPage = new EBODACPage(getDriver());
        visitPage = new VisitPage(getDriver());
        serverLogPage = new ServerLogPage(getDriver());
        url = getServerUrl();
        httpClientHelper = new UITestHttpClientHelper(url);
        raveSettingsHelper = new RAVESettingsHelper(getDriver());
    }

    @Test//Test for EBODAC-512
    public void getVisitDataFromRAVETest() throws Exception {
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.goToPage();
            loginPage.login(adminUser , adminPassword);
        }
        if (url.contains("localhost")) {
            httpClientHelper.addParticipant(new TestParticipant() , adminUser , adminPassword);
            raveSettingsHelper.createNewRAVESettings();
        }
        httpClientHelper.fetchCSV(adminUser , adminPassword);
        if (url.contains("localhost")) {
            adminPage.backToHomePage();
            homePage.openEBODACModule();
            ebodacPage.showVisits();
            boolean visitsExist = visitPage.visitsExist();
            assertTrue(visitsExist);
        } else {
            homePage.openAdmin();
            adminPage.openServerLog();
            serverLogPage.refresh();
            Thread.sleep(500);
            String logContent = serverLogPage.getLogContent();
            assertTrue(logContent.contains("Started fetching CSV files"));
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
