package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class DataServicesPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";

    static final By PARTICIPANTS = By.xpath("//div[@id='data-browser-entity']/div[4]/a/div");
    public DataServicesPage(WebDriver driver) {
        super(driver);
    }

    public void showParticipants() throws InterruptedException {
        clickWhenVisible(PARTICIPANTS);
    }

    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }
}
