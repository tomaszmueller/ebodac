package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class BookingAppAdvancedSettingsPage extends AbstractBasePage {

    static final By MAX_CAPACITY_BY_DAY = By.xpath("//td[@aria-describedby='instancesTable_maxCapacityByDay']");
    public BookingAppAdvancedSettingsPage(WebDriver driver) {
        super(driver);
    }

    public static final String URL_PATH = "/home#/mds/dataBrowser";

    public String getMaxCapacity() {
        WebElement element = findElement(MAX_CAPACITY_BY_DAY);
        String value = element.getAttribute("title");
        return value;
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }
}
