package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class IVREditPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";

    public static final By FROM = By.xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[2]/div/ng-form/div/input[@type='text']");

    public IVREditPage(WebDriver driver) {
        super(driver);
    }

    static final int BIG_TIMEOUT = 2000;

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }


    public boolean isFromEditable() throws InterruptedException {
        Thread.sleep(BIG_TIMEOUT);
        waitForElement(FROM);
        WebElement nameElement = findElement(FROM);
        try {
            if (nameElement.getAttribute("readonly").contains("readonly") || nameElement.getAttribute("readonly").contains("true")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }
}
