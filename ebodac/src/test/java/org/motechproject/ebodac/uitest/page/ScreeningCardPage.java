package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class ScreeningCardPage extends AbstractBasePage {

    public static final String URL_PATH = "/booking-app/resources/partials/card/screeningCard.html";

    static final By BOOKING_ID = By.id("bookingId");

    public ScreeningCardPage(WebDriver driver) {
        super(driver);
    }


    public String getBookingId() {
        return findElement(BOOKING_ID).getText();
    }
    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }
}
