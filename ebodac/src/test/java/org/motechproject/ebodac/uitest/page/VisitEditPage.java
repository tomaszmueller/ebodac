package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class VisitEditPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    public static final By PLANNED_VISIT_DATE = By.xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[2]/div/ng-form/div/input[@type='text']");
    public static final By ACTUAL_VISIT_DATE = By.xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[3]/div/ng-form/div/input[@type='text']");
    public static final By VISIT_TYPE = By.xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[4]/div/ng-form/div/input");
    static final int BIG_TIMEOUT = 2000;
    static final By SAVE_BUTTON = By.xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div[2]/div/button");
    static final By POPUP_OK = By.id("popup_ok");
    public VisitEditPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }

    public void changeVisit() throws InterruptedException {
        Thread.sleep(BIG_TIMEOUT);
        clickOn(SAVE_BUTTON);
        clickWhenVisible(POPUP_OK);
        clickWhenVisible(POPUP_OK);
    }

    public boolean isPlannedVisitDateEditable() {
        WebElement nameElement = findElement(PLANNED_VISIT_DATE);
        try {
            if (nameElement.getAttribute("readonly").contains("readonly") || nameElement.getAttribute("readonly").contains("true")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isActualVisitDateEditable() {
        WebElement nameElement = findElement(ACTUAL_VISIT_DATE);
        try {
            if (nameElement.getAttribute("readonly").contains("readonly") || nameElement.getAttribute("readonly").contains("true")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isVisitTypeEditable() {
        WebElement nameElement = findElement(VISIT_TYPE);
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
