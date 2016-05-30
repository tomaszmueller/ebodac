package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class IVRPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";

    static final By LOG = By.linkText("Log");
    static final By FIRST_RECORD = By.cssSelector("td[title=\"1\"]");
    public IVRPage(WebDriver driver) {
        super(driver);
    }



    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }

    public void openLog() throws InterruptedException {
        clickWhenVisible(LOG);
    }

    public void openFirstRecord() throws InterruptedException {
        clickWhenVisible(FIRST_RECORD);
    }
}
