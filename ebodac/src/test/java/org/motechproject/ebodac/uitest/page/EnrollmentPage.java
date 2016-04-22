package org.motechproject.ebodac.uitest.page;
import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
public class EnrollmentPage extends AbstractBasePage {
    public static final String URL_PATH = "home#/ebodac/enrollment";
    static final By ACTION = By.xpath("(//button[@type='button'])[3]");
    static final By POPUP_OK = By.id("popup_ok");
    static final By POPUP_CONTENT = By.id("popup_content");
    static final int SLEEP_500 = 500;
    static final int LAST_ENROLL = 3;
    public EnrollmentPage(WebDriver driver) {
        super(driver);
    }
    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }
    public void clickAction() throws InterruptedException {
        Thread.sleep(SLEEP_500);
        clickOn(ACTION);
    }
    public void clickOK() throws InterruptedException {
        Thread.sleep(SLEEP_500);
        clickOn(POPUP_OK);
    }
    public boolean error() {
        try {
            return driver.findElement(POPUP_CONTENT).getText().contains("Error occurred during enrolling Participant: Cannot enroll Participant with ");
            }
        catch (Exception ex) {
            return false;
        }
    }
    public boolean enrolled() {
        try {
            return driver.findElement(POPUP_CONTENT).getText().contains("Participant was enrolled successfully");
            }
        catch (Exception ex) {
            return false;
        }
    }
    public boolean unenrolled() {
        try {
            return driver.findElement(POPUP_CONTENT).getText().contains("Participant was unenrolled successfully.");
            }
        catch (Exception ex) {
            return false;
        }
    }
    public void nextAction() throws InterruptedException {
        int lastEnroll = LAST_ENROLL;
        do {
            lastEnroll++;
            actionSecond();
            clickOn(POPUP_OK);
        }
        while (error());
    }
    public void actionSecond() {
        WebElement action = findElement(By.xpath("(//button[@type='button'])[" + LAST_ENROLL + "]"));
        action.click();
    }
}
