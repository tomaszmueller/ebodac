package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.Override;
import java.lang.String;

public class EBODACPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final int SMALL_TIMEOUT = 500;
    static final By PARTICIPANTS = By.linkText("Participants");
    static final By REPORTS = By.linkText("Reports");
    static final By VISITS = By.linkText("Visits");
    static final By ENROLLMENT = By.linkText("Enrollment");
    private static final By EMAIL_REPORTS = By.linkText("Email Reports");;

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
            clickWhenVisible(By.id("lookupDialogButton"));
            // Click select
            clickWhenClickable(By.xpath("//*[@id='lookup-dialog']/div[2]/div[1]/div/button"));
            // Search the specific lookup.

            WebElement webElement = findElement(By.xpath("//*[@id='lookup-dialog']/div[2]/div[1]/div/ul/li[1]/a"));

            if (webElement.getAttribute("innerHTML").contains("Find By Participant Id")) {
                // Select specific option
                webElement.click();
                // Add participant id
                sleep(SMALL_TIMEOUT);
                findElement(By.xpath("//*[@id='lookup-dialog']/div[2]/div[2]/div/input")).sendKeys(participantId);
                sleep(SMALL_TIMEOUT);
                // Click find
                clickOn(By.xpath("//*[@id='lookup-dialog']/div[2]/div[3]/div/button"));
                sleep(SMALL_TIMEOUT);
                // Check if we have data for such participant
                status = findElement(By.xpath("//*[@id='instancesTable']/tbody/tr[2]/td[1]")).getAttribute("innerHTML")
                        .contains(participantId);
            } else {
                status = false;
                getLogger().error("findByParticipantID - Non Found Find by Participant id");

            }

        } catch (NullPointerException e) {
            status = false;
            getLogger().error("findByParticipantID - NullPointerException . Reason : " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            status = false;
            getLogger().error("findByParticipantID - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
        return status;
    }

    public void showEmailExport() throws InterruptedException {
        clickWhenVisible(EMAIL_REPORTS);
        
    }
}
