package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ScreeningCardPage extends AbstractBasePage {
    private static final int SLEEP_2000 = 2000;
    public static final String URL_PATH = "/booking-app/resources/partials/card/screeningCard.html";
    static final By BOOKING_ID = By.id("bookingId");

    public ScreeningCardPage(WebDriver driver) {
        super(driver);
    }


    public String getBookingId() throws InterruptedException {
        WebElement element = findElement(BOOKING_ID);
        Thread.sleep(SLEEP_2000);
        return element.getText();
    }
    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }
}
