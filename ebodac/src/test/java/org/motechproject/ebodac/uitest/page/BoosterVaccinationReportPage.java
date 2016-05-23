package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class BoosterVaccinationReportPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final By TABLE = By.xpath("//table[@class='ui-jqgrid-htable']");

    public BoosterVaccinationReportPage(WebDriver driver) {
        super(driver);
    }

    public boolean isReportEmpty() {
        try {
            if (findElement(TABLE) != null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }
}
