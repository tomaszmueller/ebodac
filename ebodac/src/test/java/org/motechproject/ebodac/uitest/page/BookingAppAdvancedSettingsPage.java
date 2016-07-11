package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class BookingAppAdvancedSettingsPage extends AbstractBasePage {

    static final By MAX_CAPACITY_BY_DAY = By.xpath("//td[@aria-describedby='instancesTable_maxCapacityByDay']");
    static final By LOCATION_KAMBIA_I = By.xpath("//td[contains(text(), 'Kambia I')]");
    public static final By SAVE_BUTTON_EDIT_ADVANCED_SETTINGS = By.xpath("//*[@id=\"dataBrowser\"]/div/div/div/ng-form/div[2]/div[1]/button[1]");
    public static final By SHOW_MORE_ADVANCED_SETTINGS_BUTTON = By.xpath("//*[@id=\"dataBrowser\"]/div/div/div/ng-form/div[1]/form/div[7]/div/ng-form/div[1]/button");
    public static final String LOCATION_INPUT_AMMOUNT_OF_ROOMS = "//*[@id=\"dataBrowser\"]/div/div/div/ng-form/div[1]/form/div[8]/div/ng-form/div[1]/input";
    public BookingAppAdvancedSettingsPage(WebDriver driver) {
        super(driver);
    }

    public static final String URL_PATH = "/home#/mds/dataBrowser";

    public String getMaxCapacity() {
        WebElement element = findElement(MAX_CAPACITY_BY_DAY);
        String value = element.getAttribute("title");
        return value;
    }

    public void clickKambiaIRowAndShowMore() throws InterruptedException {
        clickWhenVisible(LOCATION_KAMBIA_I);
        clickWhenVisible(SHOW_MORE_ADVANCED_SETTINGS_BUTTON);
    }

    public void removeTextFromInputMaxPrimeVisitsPasteOtherValue() {
        findElement(By.xpath(LOCATION_INPUT_AMMOUNT_OF_ROOMS)).clear();
        findElement(By.xpath(LOCATION_INPUT_AMMOUNT_OF_ROOMS)).sendKeys("3");
    }

    public void clickSaveAfterEditKambiaI() throws InterruptedException {
        clickWhenVisible(SAVE_BUTTON_EDIT_ADVANCED_SETTINGS);
    }


    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }
}
