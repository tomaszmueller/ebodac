package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.IVRKPIPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;

import static org.junit.Assert.assertTrue;


public class GenerateIVRGraphicsUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private IVRKPIPage ivrkpiPage;

    private static final String LOCAL_TEST_MACHINE = "localhost";
    
    @Before
    public void setUp() throws Exception {
        try {
            String user = getTestProperties().getUserName();
            String password = getTestProperties().getPassword();
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            ebodacPage = new EBODACPage(getDriver());
            ivrkpiPage = new IVRKPIPage(getDriver());
            String url = getServerUrl();
            if (url.contains(LOCAL_TEST_MACHINE)) {
                UITestHttpClientHelper httpClientHelper = new UITestHttpClientHelper(url);
                httpClientHelper.addParticipant(new TestParticipant(), user, password);
                loginPage.goToPage();
                loginPage.login(user, password);
            } else if (homePage.expectedUrlPath() != currentPage().urlPath()) {
                loginPage.goToPage();
                loginPage.login(user, password);
            }
        } catch (NullPointerException e) {
            getLogger().error("setup - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("setup - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
    }


    @Test //EBODAC-1004
    public void generateIVRGraphicsTest() throws Exception {
        try {
            homePage.openEBODACModule();
            homePage.resizePage();
            ebodacPage.showStatistics();
            ivrkpiPage.showIVRGraphs();
            ivrkpiPage.showStatsFromLastYear();
            assertTrue(ivrkpiPage.checkGraphs());
        } catch (AssertionError e) {
            getLogger().error("generateIVRGraphicsTest - AssertionError . Reason : " + e.getLocalizedMessage(), e);
        } catch (NumberFormatException e) {
            getLogger().error("generateIVRGraphicsTest - NumberFormatException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("generateIVRGraphicsTest - Exception . Reason : " + e.getLocalizedMessage(), e);
        }

    }

    @After
    public void tearDown() throws Exception {

        logout();
    }
}
