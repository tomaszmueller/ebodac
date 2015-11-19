package org.motechproject.ebodac.uitest.page;

import org.motech.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class EnrollmentsPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    public EnrollmentsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return URL_ROOT + URL_PATH;
    }

    public boolean checkEnroll() throws InterruptedException {
        Thread.sleep(1000);
        driver.findElement(By.xpath("//tr[@id='1']/td[2]")).click();
        try {
            driver.findElement(By.cssSelector("td[title=\"Boost Vaccination Third Follow-up visit\"]")).click();
        } catch(Exception e) {
            return false;
        }
        return true;
    }
}
