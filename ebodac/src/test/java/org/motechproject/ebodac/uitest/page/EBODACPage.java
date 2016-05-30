package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.lang.Override;
import java.lang.String;


public class EBODACPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final int SMALL_TIMEOUT = 500;
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

    public void gotoReports() throws InterruptedException {
        clickWhenVisible(REPORTS);
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
}
