package org.motechproject.ebodac.uitest.test;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.uitest.page.LoginPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.ParticipantPage;
import static org.junit.Assert.assertTrue;

/**
 * Class created to test Subjects and Visits
 * 
 * @author tmueller
 * @modified rmartin
 *
 */
public class DisplayOfSubjectVisitInTabularFormatUiTest extends TestBase {
    private LoginPage loginPage;
    private HomePage homePage;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;
    private String user;
    private String password;

    @Before
    public void setUp() throws Exception {
        try {
            user = getTestProperties().getUserName();
            password = getTestProperties().getPassword();
            loginPage = new LoginPage(getDriver());
            homePage = new HomePage(getDriver());
            participantPage = new ParticipantPage(getDriver());
            participantEditPage = new ParticipantEditPage(getDriver());
            if (!StringUtils.equals(homePage.expectedUrlPath(), currentPage().urlPath())) {
                loginPage.goToPage();
                loginPage.login(user, password);
            }
        } catch (NullPointerException e) {
            getLogger().error("setup - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error("setup - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @Test
    public void displayOfSubjectVisitInTabularFormatTest() throws Exception {
        try {
            homePage.openEBODACModule();
            participantPage.openFirstParticipant();
            assertTrue(participantEditPage.isTable());
        } catch (AssertionError e) {
            getLogger().error(
                    "displayOfSubjectVisitInTabularFormatTest - AssertionError - Reason : " + e.getLocalizedMessage(),
                    e);

        } catch (NullPointerException e) {
            getLogger().error("displayOfSubjectVisitInTabularFormatTest - NullPointerException - Reason : "
                    + e.getLocalizedMessage(), e);

        } catch (InterruptedException e) {
            getLogger().error("displayOfSubjectVisitInTabularFormatTest - InterruptedException - Reason : "
                    + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            getLogger().error(
                    "displayOfSubjectVisitInTabularFormatTest - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
    }

    @After
    public void tearDown() throws Exception {
        logout();
    }
}
