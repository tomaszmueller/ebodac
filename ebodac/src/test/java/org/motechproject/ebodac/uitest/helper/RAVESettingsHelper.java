package org.motechproject.ebodac.uitest.helper;

import org.motechproject.ebodac.uitest.page.AdminPage;
import org.motechproject.ebodac.uitest.page.EBODACSettingsPage;
import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.ModulesPage;
import org.openqa.selenium.WebDriver;


public class RAVESettingsHelper {

    private WebDriver driver;
    private HomePage homePage;
    private AdminPage adminPage;
    private ModulesPage modulesPage;
    private EBODACSettingsPage ebodacSettingsPage;

    public RAVESettingsHelper(WebDriver driver) {
        this.driver = driver;
        homePage = new HomePage(driver);
        adminPage = new AdminPage(driver);
        modulesPage = new ModulesPage(driver);
        ebodacSettingsPage = new EBODACSettingsPage(driver);
    }

    public void createNewRAVESettings() throws InterruptedException {
        homePage.openAdmin();
        adminPage.manageModules();
        modulesPage.openEbodacSettings();
        Long startTime = Long.valueOf(System.currentTimeMillis());

        while(System.currentTimeMillis() - startTime.longValue() < 10000L) {
            try {
                ebodacSettingsPage.enableRAVE();
                ebodacSettingsPage.setHostName();
                ebodacSettingsPage.setPort();
                ebodacSettingsPage.setRemoteFolder();
                ebodacSettingsPage.setUsername();
                ebodacSettingsPage.setPassword();
                ebodacSettingsPage.setEmail();
                break;
            } catch (Exception e) {

            }
        }

        ebodacSettingsPage.saveValues();
//        Thread.sleep(100000);
    }
}
