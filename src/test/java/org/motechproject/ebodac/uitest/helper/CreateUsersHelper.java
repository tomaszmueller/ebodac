package org.motechproject.ebodac.uitest.helper;

import org.motechproject.ebodac.uitest.page.HomePage;
import org.motechproject.ebodac.uitest.page.SecurityPage;
import org.motechproject.ebodac.uitest.page.UsersPage;
import org.openqa.selenium.WebDriver;

public class CreateUsersHelper {
    private WebDriver driver;

    private HomePage homePage;
    private SecurityPage securityPage;
    private UsersPage usersPage;

    public CreateUsersHelper(WebDriver driver) {
        this.driver = driver;
        homePage = new HomePage(driver);
        securityPage = new SecurityPage(driver);
        usersPage = new UsersPage(driver);
    }
    public void createUser(String userName, String password, String roles) throws InterruptedException {
        homePage.openSecurity();
        securityPage.openUserManagement();
        usersPage.addUser(userName,password,roles);
    }

    public void createAdminUser() throws InterruptedException {
        createUser("admin","testadmin","EBODAC Administrator");
    }

    public void createAnalystUser() throws InterruptedException  {
        createUser("analyst","testanalyst","EBODAC Site-Analyst");
    }

    public void createUsers() throws InterruptedException {
        createAdminUser();
        createAnalystUser();
    }
}
