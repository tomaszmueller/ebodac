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


public class GenerateSMSTableUiTest extends TestBase {

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


    @Test //EBODAC-1005
    public void generateSMSTableTest() throws Exception {
        try {
            homePage.openEBODACModule();
            homePage.resizePage();
            ebodacPage.showStatistics();
            ivrkpiPage.showSMSKPIs();
            ivrkpiPage.showStatsFromLast30Days();
            ivrkpiPage.checkSMSColumns();
        } catch (NumberFormatException e) {
            getLogger().error("generateIVRTableTest - NumberFormatException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("generateIVRTableTest - Exception . Reason : " + e.getLocalizedMessage(), e);
        }

    }

    @After
    public void tearDown() throws Exception {

        logout();
    }
}
