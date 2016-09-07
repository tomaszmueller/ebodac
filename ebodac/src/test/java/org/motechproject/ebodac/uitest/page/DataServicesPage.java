package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DataServicesPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final By PARTICIPANTS = By.xpath("//div[@id='data-browser-entity']/div[4]/a/div");

    public DataServicesPage(WebDriver driver) {
        super(driver);
    }

    public void showParticipants() throws InterruptedException {
        clickWhenVisible(PARTICIPANTS);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {
        try {
            clickWhenVisible(By.xpath("//*[@id='server.modules']/li[2]/a"));
        } catch (InterruptedException e) {
            getLogger().error("goToPage - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
    }

    public boolean openVisitsEntity() {
        boolean status = false;
        try {
            // Select visits Entity
            findElement(By.xpath("//*[@id='dataBrowser']/div/div[1]/div[1]/input")).sendKeys("Visit");
            clickWhenVisible(By.xpath("(.//*[@id='data-browser-entity']/div[2]/a/div)[2]"));

            status = true;
        } catch (Exception e) {
            status = false;
        }
        return status;

    }

    public void sleep(long timeout) throws Exception {
        Thread.sleep(timeout);
    }

    public void searchEntity(String entity) {
        try {
            findElement(By.xpath("//*[@id='dataBrowser']/div/div[1]/div[1]/input")).sendKeys(entity);
        } catch (Exception e) {
            getLogger().error("searchEntity - Exc . Reason : " + e.getLocalizedMessage(), e);
        }

    }

    public void selectEntity() {
        try {
            findElement(By.xpath("//*[@id='data-browser-entity']/div[2]/a/div")).click();
        } catch (Exception e) {
            getLogger().error("selectEntity - Exc . Reason : " + e.getLocalizedMessage(), e);
        }

    }
}
