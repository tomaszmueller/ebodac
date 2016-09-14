package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.Override;
import java.lang.String;

public class EBODACPage extends AbstractBasePage {

    private static final By XPATH_INSTANCES_TABLE = By.xpath("//*[@id='instancesTable']/tbody/tr[2]/td[1]");
    private static final By XPATH_LKUP_DIALOG_BUTTON = By.xpath("//*[@id='lookup-dialog']/div[2]/div[3]/div/button");
    private static final By XPATH_LKUP_DIALOG_INPUT = By.xpath("//*[@id='lookup-dialog']/div[2]/div[2]/div/input");
    private static final String FIND_BY_PARTICIPANT_ID = "Find By Participant Id";
    private static final String INNER_HTML = "innerHTML";
    private static final By XPATH_LOOKUPDIALOG_ELEM = By.xpath("//*[@id='lookup-dialog']/div[2]/div[1]/div/ul/li[1]/a");
    private static final By XPATH_LOOKUP_BUTTON = By.id("lookupDialogButton");
    private static final By XPATH_LOOKUP_DIALOG = By.xpath("//*[@id='lookup-dialog']/div[2]/div[1]/div/button");
    public static final String URL_PATH = "/home#/mds/dataBrowser";
    //public static final String URL_PATH = "/home#/ebodac/subjects";
    static final long SMALL_TIMEOUT = 500;
    static final long SLEEP_2SEC = 2000;
    private static final long SLEEP_4SEC = 4000;
    static final By PARTICIPANTS = By.linkText("Participants");
    static final By REPORTS = By.linkText("Reports");
    static final By VISITS = By.linkText("Visits");
    static final By ENROLLMENT = By.linkText("Enrollment");

    public EBODACPage(WebDriver driver) {
        super(driver);
    }

    public void showParticipants() throws InterruptedException {
        clickWhenVisible(PARTICIPANTS);

    }

    public ReportPage gotoReports() throws InterruptedException {
        clickWhenVisible(REPORTS);
        return new ReportPage(getDriver());
    }

    public void showVisits() throws InterruptedException {
        clickWhenVisible(VISITS);
    }

    public void goToVisit() throws InterruptedException {
        Thread.sleep(SMALL_TIMEOUT);
        clickWhenVisible(VISITS);
    }

    public void goToEnrollment() throws InterruptedException {
        clickWhenVisible(ENROLLMENT);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }

    public void sleep(long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            getLogger().error("sleep - Exception . Reason : " + e.getLocalizedMessage(), e);
        }

    }

    public boolean findByParticipantID(String participantId) {
        boolean status = false;
        try {
            // Click on Lookup field and find by Participant id
            clickWhenVisible(XPATH_LOOKUP_BUTTON);
            sleep(SLEEP_4SEC);
            // Click select
            clickWhenClickable(XPATH_LOOKUP_DIALOG);
            sleep(SLEEP_2SEC);
            // Search the specific lookup.

            WebElement webElement = findElement(XPATH_LOOKUPDIALOG_ELEM);

            if (webElement.getAttribute(INNER_HTML).contains(FIND_BY_PARTICIPANT_ID)) {
                // Select specific option
                webElement.click();
                // Add participant id
                sleep(SLEEP_2SEC);
                findElement(XPATH_LKUP_DIALOG_INPUT).sendKeys(participantId);
                sleep(SLEEP_2SEC);
                // Click find
                clickOn(XPATH_LKUP_DIALOG_BUTTON);
                sleep(SLEEP_2SEC);
                // Check if we have data for such participant
                status = findElement(XPATH_INSTANCES_TABLE).getAttribute(INNER_HTML).contains(participantId);
                sleep(SLEEP_2SEC);
            } else {
                status = false;
                getLogger().error("findByPID - Non Found Find by PID:" + participantId);

            }

        } catch (NullPointerException e) {
            status = false;
            getLogger().error("findByPID - PId:" + participantId + "NPE. Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            status = false;
            getLogger().error("findByPID - PId:" + participantId + "Exc. Reason : " + e.getLocalizedMessage(), e);
        }
        return status;
    }
}
