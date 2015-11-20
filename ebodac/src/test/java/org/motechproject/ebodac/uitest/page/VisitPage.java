package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class VisitPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final By VISIT = By.xpath("//tr[5]/td[2]");


    public VisitPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }

    public void clickVisit() throws InterruptedException{
        Thread.sleep(500);
        waitForElement(VISIT);
        clickOn(VISIT);
    }
}
