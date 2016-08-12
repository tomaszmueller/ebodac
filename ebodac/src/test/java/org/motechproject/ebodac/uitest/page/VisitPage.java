package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class VisitPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final By VISIT = By.xpath("//table[@id='instancesTable']/tbody/tr[2]");
    static final By VISIT_DATE = By.xpath("//table[@id='instancesTable']/tbody/tr[2]/td[2]");
    static final int SLEEP_500 = 500;
    static final int SLEEP_1000 = 1000;
    public static final By PLANNED_VISIT_DATE_HEAD = By.id("jqgh_instancesTable_motechProjectedDate");
    public static final By PLANNED_VISIT_DATE_SORT = By
            .xpath("//div[@id='jqgh_instancesTable_motechProjectedDate']/span/span[2]");
    private static final long SLEEP_2SEG = 2000;

    public VisitPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }

    public boolean visitsExist() {
        try {
            Thread.sleep(SLEEP_1000);
            findElement(VISIT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickVisit() throws InterruptedException {
        Thread.sleep(SLEEP_500);
        waitForElement(VISIT);
        clickOn(VISIT);
    }

    public void sortByPlannedDateColumn() throws InterruptedException {
        clickWhenVisible(PLANNED_VISIT_DATE_HEAD);
        Thread.sleep(SLEEP_1000);
        clickWhenVisible(PLANNED_VISIT_DATE_SORT);
        Thread.sleep(SLEEP_1000);
    }

    public boolean findByParticipantId(String participantId) {
        boolean status = false;
        String namePath = "";
        try {
            // Click on Button Lookup
            sleep(SLEEP_2SEG);
            namePath = "//*[@id='lookupDialogButton']";
            clickWhenVisible(By.xpath(namePath));

            // click on select
            namePath = "//*[@id='lookup-dialog']/div[2]/div[1]/div/button";
            clickWhenVisible(By.xpath(namePath));

            // Select Find by Participant
            namePath = "//*[@id='lookup-dialog']/div[2]/div[1]/div/ul/li[6]/a";
            clickWhenVisible(By.xpath(namePath));

            // Add the value participant id
            namePath = "//*[@id='lookup-dialog']/div[2]/div[2]/div/input";
            sleep(SLEEP_500);
            findElement(By.xpath(namePath)).sendKeys(participantId);

            namePath = "//*[@id='lookup-dialog']/div[2]/div[3]/div/button";
            clickWhenVisible(By.xpath(namePath));
            sleep(SLEEP_500);
            status = true;

        } catch (NullPointerException e) {
            status = false;
            getLogger().error("findByParticipantId - namePath : " + namePath + "  - NullPointerException . Reason : "
                    + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            status = false;
            getLogger().error("findByParticipantId - namePath : " + namePath + " - Exception . Reason : "
                    + e.getLocalizedMessage(), e);
        }

        return status;

    }

    public boolean findVisitsByParticipantID(String participantId) {
        boolean status = false;
        String webElementHtml = "";
        try {
            webElementHtml = findElement(By.xpath("//*[@id='instancesTable']/tbody/tr[2]/td[1]"))
                    .getAttribute("innerHTML");
            getLogger().error("findVisitsByParticipantID - webElementHTML :" + webElementHtml);
            status = webElementHtml.contains(participantId);
        } catch (NullPointerException e) {
            status = false;
            getLogger().error("findVisitsByParticipantID - webElementHTML :" + webElementHtml);
            getLogger().error("findVisitsByParticipantID - NullPointerException . Reason : " + e.getLocalizedMessage(),
                    e);
        } catch (Exception e) {
            status = false;
            getLogger().error("findVisitsByParticipantID - webElementHTML :" + webElementHtml);
            getLogger().error("findVisitsByParticipantID - Exception . Reason : " + e.getLocalizedMessage(), e);
        }

        return status;

    }

    public boolean findActualDate(String actualDate) {
        boolean status = false;
        try {
            status = findElement(By.xpath("//*[@id='instancesTable']/tbody/tr[2]/td[3]")).getAttribute("innerHTML")
                    .contains(actualDate);
        } catch (Exception e) {
            status = false;
            getLogger().error("findActualDate - Exception . Reason : " + e.getLocalizedMessage(), e);
        }
        return status;
    }

    public void sleep(long sleep) throws InterruptedException {
        Thread.sleep(sleep);

    }
}
