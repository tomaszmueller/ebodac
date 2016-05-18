package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class SMSPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";

    static final By LOG = By.linkText("Log");
    public SMSPage(WebDriver driver) {
        super(driver);
    }



    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }

    public boolean logExists() {
        try {
            if (findElement(LOG) != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
