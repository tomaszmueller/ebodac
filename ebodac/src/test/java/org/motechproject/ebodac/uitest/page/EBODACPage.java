package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.lang.Override;
import java.lang.String;


public class EBODACPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";

    static final By PARTICIPANTS = By.linkText("Participants");
    static final By REPORTS = By.linkText("Reports");
    static final By VISITS = By.linkText("Visits");
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

    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }
}
