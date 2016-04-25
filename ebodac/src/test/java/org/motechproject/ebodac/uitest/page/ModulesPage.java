package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class ModulesPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    public static final By EBODAC_SETTINGS = By.cssSelector("img.action.action-settings");

    public ModulesPage(WebDriver driver) {
        super(driver);
    }

    public void openEbodacSettings() throws InterruptedException {
        waitForElement(EBODAC_SETTINGS);
        clickWhenVisible(EBODAC_SETTINGS);
    }

    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }
}
