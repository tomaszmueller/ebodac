package org.motechproject.ebodac.uitest.test;

import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.helper.TestParticipant;
import org.motechproject.ebodac.uitest.helper.UITestHttpClientHelper;
import org.motechproject.ebodac.uitest.page.*;

import java.lang.Exception;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ChangePhoneNumberAndGetParticipantFromZetesUiTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;
    private DataServicesPage dataServicesPage;
    private String L1adminUser;
    private String L1adminpassword;
    private String testNumber = "55577755";
    private String changedNumber;
    private UITestHttpClientHelper httpClientHelper;
    private String url;
    private TestParticipant testParticipant;
    @Before
    public void setUp() {
        L1adminUser = properties.getUserName();
        L1adminpassword = properties.getPassword();
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        ebodacPage = new EBODACPage(driver);
        participantPage = new ParticipantPage(driver);
        participantEditPage = new ParticipantEditPage(driver);
        dataServicesPage = new DataServicesPage(driver);
        url = properties.getWebAppUrl();
        testParticipant = new TestParticipant();
    }

    @Test //Test for EBODAC-508/EBODAC-509
    public void changePhoneNumberAndGetParticipantFromZetesTest() throws Exception {
        httpClientHelper = new UITestHttpClientHelper(url);
        httpClientHelper.addParticipant(new TestParticipant(), L1adminUser, L1adminpassword);
        if(homePage.expectedUrlPath() != currentPage().urlPath()) {
            loginPage.login(L1adminUser, L1adminpassword);
        }
        homePage.openEBODACModule();
        ebodacPage.showParticipants();
        assertTrue(participantPage.findParticipant(testParticipant.id));
        participantPage.openParticipant(testParticipant.id);
        participantEditPage.changePhoneNumber(testNumber);
        changedNumber = participantPage.getFirstParticipantNumber();
        assertEquals(changedNumber, testNumber);
    }


    @After
    public void tearDown() throws Exception {
        deleteParticipant();
        logout();
    }

    private void deleteParticipant() throws Exception  {
        homePage.openDataServicesModule();
        Thread.sleep(500);
        dataServicesPage.showParticipants();
        participantPage.openParticipant(testParticipant.id);
        participantEditPage.deleteParticipant();
    }
}
