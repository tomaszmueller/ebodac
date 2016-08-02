package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;

import com.mchange.util.AssertException;

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

//import org.apache.log4j.Logger;

@Ignore
public class GetVisitDataFromRAVEUiTest extends TestBase {
    private static final int WAIT_500MLSEC = 500;
    // Object initialization for log
    // private static Logger log =
    // Logger.getLogger(GetVisitDataFromRAVEUiTest.class.getName());
    private LoginPage loginPage;
    private HomePage homePage;
    private AdminPage adminPage;
    private EBODACPage ebodacPage;
    private VisitPage visitPage;
    private ServerLogPage serverLogPage;
    private RAVESettingsHelper raveSettingsHelper;
    private String user;
    private String password;
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private UITestHttpClientHelper httpClientHelper;

    @Before
    public void setUp() throws Exception {
        try {
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            adminPage = new AdminPage(getDriver());
            ebodacPage = new EBODACPage(getDriver());
            visitPage = new VisitPage(getDriver());
            serverLogPage = new ServerLogPage(getDriver());
            url = getServerUrl();
            user = getTestProperties().getUserName();
            password = getTestProperties().getPassword();
            httpClientHelper = new UITestHttpClientHelper(url);
            raveSettingsHelper = new RAVESettingsHelper(getDriver());
        } catch (NullPointerException e) {
            getLogger().error("setup - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("setup - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test // Test for EBODAC-512
    public void getVisitDataFromRAVETest() throws Exception {
        try {
            if (homePage.expectedUrlPath() != currentPage().urlPath()) {
                loginPage.goToPage();
                loginPage.login(user, password);
            }
            if (url.contains(LOCAL_TEST_MACHINE)) {
                httpClientHelper.addParticipant(new TestParticipant(), user, password);
                raveSettingsHelper.createNewRAVESettings();
            }
            httpClientHelper.fetchCSV(user, password);
            if (url.contains(LOCAL_TEST_MACHINE)) {
                adminPage.backToHomePage();
                homePage.openEBODACModule();
                ebodacPage.showVisits();
                boolean visitsExist = visitPage.visitsExist();
                assertTrue(visitsExist);
            } else {
                homePage.openAdmin();
                adminPage.openServerLog();
                serverLogPage.refresh();
                Thread.sleep(WAIT_500MLSEC);
                String logContent = serverLogPage.getLogContent();
                assertTrue(logContent.contains("Started fetching CSV files"));
            }
        } catch (AssertException e) {
            getLogger().error("getVisitDataFromRAVETest - AssertException . Reason : " + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            getLogger().error("getVisitDataFromRAVETest - NullPointerException . Reason : " + e.getLocalizedMessage(),
                    e);
        } catch (NullPointerException e) {
            getLogger().error("getVisitDataFromRAVETest - NullPointerException . Reason : " + e.getLocalizedMessage(),
                    e);
        } catch (Exception e) {
            getLogger().error("getVisitDataFromRAVETest - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
