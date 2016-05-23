package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class AdminPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    public static final By MANAGE_MODULES = By.linkText("Manage Modules");
    public static final By SERVER_LOG = By.linkText("Server Log");

    public static final By MODULES = By.linkText("Modules");
    public AdminPage(WebDriver driver) {
        super(driver);
    }

    public void manageModules() {
        clickOn(MANAGE_MODULES);
    }

    public void backToHomePage() {
        clickOn(MODULES);
    }

    public void openServerLog() throws InterruptedException {
        waitForElement(SERVER_LOG);
        clickWhenVisible(SERVER_LOG);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }
}
