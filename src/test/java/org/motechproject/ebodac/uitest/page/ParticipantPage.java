package org.motechproject.ebodac.uitest.page;

import org.ebodac.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created by tomasz on 21.09.15.
 */
public class ParticipantPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final By PARTICIPANT = By.id("1");
    static final By PHONE_NUMBER = By.xpath("//tr[@id='1']/td[@aria-describedby='instancesTable_phoneNumber']");
    public ParticipantPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }

    public void openFirstParticipant() throws InterruptedException {
        clickWhenVisible(PARTICIPANT);
    }

    public String getFirstParticipantNumber() {
        return findElement(PHONE_NUMBER).getText();
    }
}
