package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class DataServicesPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";

    static final By PARTICIPANTS = By.xpath("(//div[@id='data-browser-entity']/div[3]/a/div)[2]");
    public DataServicesPage(WebDriver driver) {
        super(driver);
    }

    public void showParticipants() throws InterruptedException {
        clickWhenVisible(PARTICIPANTS);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }
}
