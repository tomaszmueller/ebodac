package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class VisitPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final By VISIT = By.xpath("//table[@id='instancesTable']/tbody/tr[2]");
    static final int SLEEP_500 = 500;
    static final int SLEEP_1000 = 1000;


    public VisitPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }

    public boolean visitsExist() {
        try {
            Thread.sleep(SLEEP_1000);
            findElement(VISIT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickVisit() throws InterruptedException {
        Thread.sleep(SLEEP_500);
        waitForElement(VISIT);
        clickOn(VISIT);
    }
}
