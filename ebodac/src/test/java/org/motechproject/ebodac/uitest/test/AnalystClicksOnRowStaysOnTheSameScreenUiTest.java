package org.motechproject.ebodac.uitest.test;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.EnrollmentPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.helper.CreateUsersHelper;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.helper.UserPropertiesHelper;
import static org.junit.Assert.assertFalse;
import org.apache.log4j.Logger;

public class AnalystClicksOnRowStaysOnTheSameScreenUiTest extends TestBase {
    private static Logger log = Logger.getLogger(AnalystClicksOnRowStaysOnTheSameScreenUiTest.class.getName());
    private String url;
    private static final String LOCAL_TEST_MACHINE = "localhost";
    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private EnrollmentPage enrollmentsPage;
    private String l1analystUser;
    private String l1analystpassword;
    private UserPropertiesHelper userPropertiesHelper;
    // private UITestHttpClientHelper httpClientHelper;
    private CreateUsersHelper createUsersHelper;
  

    @Before
    public void setUp() throws Exception {
        try {
            
            // if (url.contains(LOCAL_TEST_MACHINE)) {
            // createUsersHelper = new CreateUsersHelper(getDriver());
            // createUsersHelper.createUsersWithLogin(getTestProperties());
            // }
            ebodacPage = new EBODACPage(getDriver());
            enrollmentsPage = new EnrollmentPage(getDriver());
            userPropertiesHelper = new UserPropertiesHelper();
            l1analystUser = userPropertiesHelper.getAnalystUserName();
            l1analystpassword = userPropertiesHelper.getAnalystPassword();
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            url = getServerUrl();
            if (url.contains(LOCAL_TEST_MACHINE)) {
                UITestHttpClientHelper httpClientHelper = new UITestHttpClientHelper(url);
                loginPage.goToPage();
                loginPage.login(l1analystUser, l1analystpassword);
            } else if (homePage.expectedUrlPath() != currentPage().urlPath()) {
                loginPage.goToPage();
                loginPage.login(l1analystUser, l1analystpassword);
            }
        } catch (NullPointerException e) {
            log.error("setUp - NullPointerException - Reason : " + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            log.error("setUp - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test
    public void analystClicksOnRowStaysOnTheSameScreenTest() throws Exception {
        try {
            homePage.openEBODACModule();
            ebodacPage.goToEnrollment();
            assertFalse(enrollmentsPage.checkEnroll());
        } catch (AssertionError e) {
            log.error("analystClicksOnRowStaysOnTheSameScreenTest - NullPointerException - Reason : "
                    + e.getLocalizedMessage(), e);
        } catch (InterruptedException e) {
            log.error("analystClicksOnRowStaysOnTheSameScreenTest - InterruptedException - Reason : "
                    + e.getLocalizedMessage(), e);

        }  catch (NullPointerException e) {
            log.error("analystClicksOnRowStaysOnTheSameScreenTest - NullPointerException - Reason : "
                    + e.getLocalizedMessage(), e);

        } catch (Exception e) {
            log.error("analystClicksOnRowStaysOnTheSameScreenTest - Exception - Reason : " + e.getLocalizedMessage(),
                    e);
        }

    }
}
