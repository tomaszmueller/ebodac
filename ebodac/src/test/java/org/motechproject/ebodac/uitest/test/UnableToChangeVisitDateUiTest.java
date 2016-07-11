package org.motechproject.ebodac.uitest.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.joda.time.LocalDate;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.VisitEditPage;
import org.motechproject.ebodac.uitest.page.VisitPage;
import org.motechproject.uitest.TestBase;
import org.motechproject.uitest.page.LoginPage;
import static org.junit.Assert.assertTrue;

public class UnableToChangeVisitDateUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private VisitPage visitPage;
    private VisitEditPage visitEditPage;
    private String user;
    private String password;
    private UITestHttpClientHelper httpClientHelper;
    private String url;
    @Before
    public void setUp() {
        user = getTestProperties().getUserName();
        password = getTestProperties().getPassword();
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
        ebodacPage = new EBODACPage(getDriver());
        visitEditPage = new VisitEditPage(getDriver());
        visitPage = new VisitPage(getDriver());
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
    public void unableToChangVisitDateTest() throws Exception {
        homePage.openEBODACModule();
        ebodacPage.goToVisit();
        visitPage.sortByPlannedDateColumn();
        visitPage.clickVisit();
        String date = LocalDate.now().toString("yyyy-MM-dd");
        visitEditPage.changePlannedDate(date);
        assertTrue(visitEditPage.changeVisit());
    }

    @After
    public void tearDown() {

    }
}
