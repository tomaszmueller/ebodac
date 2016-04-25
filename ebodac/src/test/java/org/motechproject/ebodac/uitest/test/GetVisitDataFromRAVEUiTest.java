package org.motechproject.ebodac.uitest.test;

import org.junit.Ignore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.motechproject.ebodac.uitest.helper.RAVESettingsHelper;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.*;

import static junit.framework.Assert.assertTrue;
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
        adminUser = properties.getUserName();
        adminPassword = properties.getPassword();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        adminPage = new AdminPage(driver);
        ebodacPage = new EBODACPage(driver);
        visitPage = new VisitPage(driver);
        serverLogPage = new ServerLogPage(driver);
        url = properties.getWebAppUrl();
        httpClientHelper = new UITestHttpClientHelper(url);
        raveSettingsHelper = new RAVESettingsHelper(driver);
    }

    @Test//Test for EBODAC-512
    public void getVisitDataFromRAVETest() throws Exception {
        if (homePage.expectedUrlPath() != currentPage().urlPath()) {
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
        loginPage.logOut();
    }
}
