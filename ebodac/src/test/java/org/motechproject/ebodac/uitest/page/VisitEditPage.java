package org.motechproject.ebodac.uitest.page;


import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class VisitEditPage extends AbstractBasePage {

    private static final String TRUE = "true";
    private static final String READONLY = "readonly";
    public static final String URL_PATH = "/home#/mds/dataBrowser";
    public static final By PLANNED_VISIT_DATE = By.xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[2]/div/ng-form/div/input[@type='text']");
    public static final By ACTUAL_VISIT_DATE = By.xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[3]/div/ng-form/div/input[@type='text']");
    public static final By VISIT_TYPE = By.xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[4]/div/ng-form/div/input");
    static final int BIG_TIMEOUT = 2000;
    static final By SAVE_BUTTON = By.xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div[2]/div/button");
    static final By POPUP_OK = By.id("popup_ok");
    static final By POPUP_MESSAGE = By.id("popup_message");
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

    public boolean changeVisit() throws InterruptedException {
        Thread.sleep(BIG_TIMEOUT);
        clickWhenVisible(SAVE_BUTTON);
        clickWhenVisible(POPUP_OK);
        String text = findElement(POPUP_MESSAGE).getText();
        clickWhenVisible(POPUP_OK);
        if (text.contains("Planned Visit date has been changed successfully")) {
            return true;
        }
        return false;
    }

    public boolean isPlannedVisitDateEditable() {
        WebElement nameElement = findElement(PLANNED_VISIT_DATE);
        try {
            if (nameElement.getAttribute(READONLY).contains(READONLY) || nameElement.getAttribute(READONLY).contains(TRUE)) {
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
            if (nameElement.getAttribute(READONLY).contains(READONLY) || nameElement.getAttribute(READONLY).contains(TRUE)) {
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
            if (nameElement.getAttribute(READONLY).contains(READONLY) || nameElement.getAttribute(READONLY).contains(TRUE)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public void changePlannedDate(String date) throws InterruptedException {
        sleep(BIG_TIMEOUT);
        findElement(PLANNED_VISIT_DATE).clear();
        findElement(PLANNED_VISIT_DATE).sendKeys(date);
        findElement(PLANNED_VISIT_DATE).sendKeys(Keys.ENTER);
    }

    public void sleep(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (Exception e) {
            getLogger().error("sleep - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
        
    }

}
