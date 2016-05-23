package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.lang.Override;
import java.lang.String;


public class HomePage extends AbstractBasePage {

    public static final String URL_PATH = "/home";
    static final By EBODAC = By.linkText("EBODAC");
    static final By SECURITY = By.linkText("Security");
    public static final By DATA_SERVICES = By.linkText("Data Services");
    static final By EMAIL = By.linkText("Email");
    static final By MESSAGE_CAMPAIGN = By.linkText("Message Campaign");
    static final By IVR = By.linkText("IVR");
    static final By SMS = By.linkText("SMS");
    static final By SCHEDULER = By.linkText("Scheduler");
    static final By TASKS = By.linkText("Tasks");
    static final By MODULES = By.linkText("Modules");
    static final By BOOKING_APP = By.linkText("Booking App");
    static final By ADMIN = By.linkText("Admin");
    static final By USER_LANGUAGE_CONTROL = By.cssSelector("span.ng-binding");
    static final By LANGUAGE_PL = By.linkText("Polski");
    static final By POPUP_OK = By.id("popup_ok");
    static final int SLEEP_500 = 500;
    static final int SLEEP_1000 = 1000;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }
    public void openEBODACModule() throws InterruptedException {
        clickWhenVisible(EBODAC);
    }

    public void openBookingAppModule() throws  InterruptedException {
        clickWhenVisible(BOOKING_APP);
    }
    public boolean isEBODACModulePresent() throws InterruptedException {
        try {
            if (findElement(EBODAC) != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDataServicesModulePresent() throws InterruptedException {
        try {
            if (findElement(DATA_SERVICES) != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void openDataServicesModule() throws InterruptedException {
        clickWhenVisible(DATA_SERVICES);
    }

    public void openSecurity() throws InterruptedException {
        clickWhenVisible(SECURITY);
    }

    public boolean isEmailModulePresent() throws InterruptedException {
        try {
            if (findElement(EMAIL) != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isMessageCampaignModulePresent() throws InterruptedException {
        try {
            if (findElement(MESSAGE_CAMPAIGN) != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isIVRModulePresent() throws InterruptedException {
        try {
            if (findElement(IVR) != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSMSModulePresent() throws InterruptedException {
        try {
            if (findElement(SMS) != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isSchedulerModulePresent() throws InterruptedException {
        try {
            if (findElement(SCHEDULER) != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTasksModulePresent() throws InterruptedException {
        try {
            if (findElement(TASKS) != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickOnEbodac() throws InterruptedException {
        Thread.sleep(SLEEP_500);
        clickOn(EBODAC);
    }

    public void clickModules() throws InterruptedException {
        clickWhenVisible(MODULES);
    }

    public void openAdmin() throws InterruptedException {
        clickWhenVisible(ADMIN);
    }

    public boolean isElementPresent(By by) {
        try {
            findElement(by);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void openIVRModule() throws InterruptedException {
        clickWhenVisible(IVR);
        Thread.sleep(SLEEP_1000);
        clickWhenVisible(IVR);
    }

    public void changeUserLanguage() throws InterruptedException {
        clickWhenVisible(USER_LANGUAGE_CONTROL);
        clickWhenVisible(LANGUAGE_PL);
        clickWhenVisible(POPUP_OK);
    }
}
