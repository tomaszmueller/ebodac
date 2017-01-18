package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class FollowupsMissedClinicVisitsReportPage extends AbstractBasePage {
    // Object initialization for log
    private static Logger log = Logger.getLogger(FollowupsMissedClinicVisitsReportPage.class.getName());
    public static final String URL_PATH = "/home#/mds/dataBrowser";

    static final By NAME_COLUMN = By.id("reportTable_subjectName");
    static final By HOUSEHOLDNAME_COLUMN = By.id("reportTable_subjectHouseholdName");
    static final By HEADOFHOUSEHOLD_COLUMN = By.id("reportTable_subjectHeadOfHousehold");
    static final By DATEOFBIRTH_COLUMN = By.id("reportTable_subjectDateOfBirth");
    static final By GENDER_COLUMN = By.id("reportTable_subjectGender");
    static final By ADDRESS_COLUMN = By.id("reportTable_subjectAddress");
    static final By TYPE_COLUMN = By.id("reportTable_type");
    static final By VISITDATE_COLUMN = By.id("reportTable_planedVisitDate");
    static final By EXCEEDEDVISIT_COLUMN = By.id("reportTable_noOfDaysExceededVisit");
    static final By COMMUNITY_COLUMN = By.id("reportTable_subjectCommunity");
    static final By STAGE_ID_COLUMN = By.id("reportTable_subjectStageId");
    static final By SITENAME_COLUMN = By.id("reportTable_subjectSiteName");
    static final By TABLE = By.id("reportInstanceTable");

    public FollowupsMissedClinicVisitsReportPage(WebDriver driver) {
        super(driver);
    }

    public boolean existTable() {
        boolean status = false;
        try {
            if (!findElement(TABLE).getAttribute("innerHTML").isEmpty()) {
                status = true;
            }
        } catch (Exception e) {
            log.error("existTable - Exception - Reason : " + e.getLocalizedMessage(), e);
        }
        return status;
    }

    public boolean isReportEmpty() {
        boolean status = false;
        try {
            if (findElement(By.xpath("//*[@id='pageReportTable_left']/div")).getAttribute("innerHTML")
                    .contains("No records to view")) {
                status = true;
            }
        } catch (Exception e) {
            log.error("isReportEmpty -  Exception . Reason : " + e.getLocalizedMessage(), e);
            status = false;
        }
        return status;
    }

    public boolean checkColumns() {
        List<By> columns = Arrays.asList(NAME_COLUMN, HOUSEHOLDNAME_COLUMN, HEADOFHOUSEHOLD_COLUMN, DATEOFBIRTH_COLUMN, GENDER_COLUMN,
                ADDRESS_COLUMN, TYPE_COLUMN, VISITDATE_COLUMN, EXCEEDEDVISIT_COLUMN, COMMUNITY_COLUMN, STAGE_ID_COLUMN, SITENAME_COLUMN);
        List<String> columnNames = Arrays.asList("Name", "Household name", "Head of Household", "DOB", "Gender", "Address",
                "Visit type", "Planed Visit Date", "No. of Days Exceeded of the Clinic Visit", "Community", "Stage ID", "Site Name");
        try {
            for(int i=0; i<columns.size(); i++) {
                WebElement element = findElement(columns.get(i));
                if (element == null) {
                    return false;
                } else {
                    assertTrue(element.getText().contains(columnNames.get(i)));
                }
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }
    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }

    public void sleep(long timeout) throws InterruptedException {
        Thread.sleep(timeout);
        
    }

}
