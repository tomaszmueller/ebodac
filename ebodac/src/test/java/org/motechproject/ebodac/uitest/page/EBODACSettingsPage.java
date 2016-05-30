package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;


public class EBODACSettingsPage extends AbstractBasePage {

    public static final String URL_PATH = "/home";
    static final By RAVE_SETTINGS_CHECKBOX = By.id("raveSettingsCheckBox");
    static final By RAVE_SETTINGS_HOSTNAME = By.id("ftpsHost");
    static final By RAVE_SETTINGS_PORT = By.id("ftpsPort");
    static final By RAVE_SETTINGS_DIRECTORY = By.id("ftpsDirectory");
    static final By RAVE_SETTINGS_USERNAME = By.id("ftpsUsername");
    static final By RAVE_SETTINGS_PASSWORD = By.id("ftpsPassword");
    static final By IVR_SETTINGS = By.id("sendIvrCalls");
    static final By SETTINGS_EMAIL_HOST = By.id("emailHost");
    static final By SETTINGS_EMAIL = By.id("email");
    static final By SETTINGS_EMAIL_PASSWORD = By.id("emailPassword");
    static final By SETTINGS_EMAIL_INTERVAL = By.id("emailCheckInterval");
    static final By SAVE_BUTTON = By.xpath("//div[@id='main-content']/div/div/div/form/div[14]/button[2]");
    static final int SLEEP_500 = 500;
    public EBODACSettingsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }
    public void enableRAVE() throws InterruptedException {
        clickWhenVisible(RAVE_SETTINGS_CHECKBOX);
    }

    public void setHostName() throws InterruptedException {
        findElement(RAVE_SETTINGS_HOSTNAME).click();
        Thread.sleep(SLEEP_500);
        setTextToFieldNoEnter(RAVE_SETTINGS_HOSTNAME , "jnj2.ftp.mdsol.com");
    }

    public void setPort() throws InterruptedException  {
        findElement(RAVE_SETTINGS_PORT).click();
        Thread.sleep(SLEEP_500);
        setTextToFieldNoEnter(RAVE_SETTINGS_PORT , "21");
    }

    public void setRemoteFolder() throws InterruptedException  {
        findElement(RAVE_SETTINGS_DIRECTORY).click();
        Thread.sleep(SLEEP_500);
        setTextToFieldNoEnter(RAVE_SETTINGS_DIRECTORY , "/VAC52150EBL3001/QA/");
    }

    public void setUsername() throws InterruptedException  {
        findElement(RAVE_SETTINGS_USERNAME).click();
        Thread.sleep(SLEEP_500);
        setTextToFieldNoEnter(RAVE_SETTINGS_USERNAME , "ruben.martin.soldevelo.com");
    }

    public void setPassword() throws InterruptedException  {
        findElement(RAVE_SETTINGS_PASSWORD).click();
        Thread.sleep(SLEEP_500);
        setTextToFieldNoEnter(RAVE_SETTINGS_PASSWORD , ";smnf>aY");
    }

    public void setEmail() throws InterruptedException  {
        findElement(SETTINGS_EMAIL_HOST).click();
        Thread.sleep(SLEEP_500);
        setTextToFieldNoEnter(SETTINGS_EMAIL_HOST , "mail.soldevelo.com");
        findElement(SETTINGS_EMAIL).click();
        Thread.sleep(SLEEP_500);
        setTextToFieldNoEnter(SETTINGS_EMAIL , "tmueller@soldevelo.com");
        findElement(SETTINGS_EMAIL_PASSWORD).click();
        Thread.sleep(SLEEP_500);
        setTextToFieldNoEnter(SETTINGS_EMAIL_PASSWORD , "corradi1!");
        findElement(SETTINGS_EMAIL_INTERVAL).click();
        Thread.sleep(SLEEP_500);
        setTextToFieldNoEnter(SETTINGS_EMAIL_INTERVAL , "60");
    }

    public void saveValues() throws InterruptedException {
        try {
            waitForElement(SAVE_BUTTON);
            findElement(SAVE_BUTTON).sendKeys(Keys.ENTER);
        } catch (Exception e) {
            waitForElement(IVR_SETTINGS);
            clickOn(IVR_SETTINGS);
            waitForElement(SAVE_BUTTON);
            findElement(SAVE_BUTTON).sendKeys(Keys.ENTER);
        }

    }
}
