package org.motechproject.ebodac.uitest.test;

import org.motech.page.LoginPage;
import org.motech.test.TestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.ebodac.uitest.page.EBODACPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ParticipantEditPage;
import org.motechproject.ebodac.uitest.page.ParticipantPage;

import java.lang.Exception;

import static junit.framework.Assert.assertEquals;


public class ChangePhoneNumberTest extends TestBase {

    private LoginPage loginPage;
    private HomePage homePage;
    private EBODACPage ebodacPage;
    private ParticipantPage participantPage;
    private ParticipantEditPage participantEditPage;
    private final String L1adminUser = "admin";
    private final String L1adminpassword = "testadmin";
    private String testNumber = "55577755";
    private String changedNumber;

    @Before
    public void setUp() {
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        ebodacPage = new EBODACPage(driver);
        participantPage = new ParticipantPage(driver);
        participantEditPage = new ParticipantEditPage(driver);
        loginPage.login(L1adminUser, L1adminpassword);
    }

    @Test //Test for EBODAC-508
    public void changePhoneNumberTest() throws Exception {
        homePage.openEBODACModule();
        ebodacPage.showParticipants();
        participantPage.openFirstParticipant();
        participantEditPage.changePhoneNumber(testNumber);
        changedNumber = participantPage.getFirstParticipantNumber();
        assertEquals(changedNumber, testNumber);
    }

    @After
    public void tearDown() throws Exception {
        loginPage.logOut();
    }
}
