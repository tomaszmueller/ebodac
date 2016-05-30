package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class ServerLogPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    public static final By REFRESH_BUTTON = By.id("Refresh");
    public static final By LOG_CONTENT = By.id("logContent");
    public ServerLogPage(WebDriver driver) {
        super(driver);
    }

    public void refresh() throws InterruptedException {
        clickWhenVisible(REFRESH_BUTTON);
    }

    public String getLogContent() {
        waitForElement(LOG_CONTENT);
        return findElement(LOG_CONTENT).getText();
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }
}
