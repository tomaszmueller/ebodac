package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class NumberOfTimesListenedReportPage extends AbstractBasePage {

    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final By ID_COLUMN = By.id("reportTable_subjectId");
    static final By PHONE_COLUMN = By.id("reportTable_phone");
    static final By GENDER_COLUMN = By.id("reportTable_gender");
    static final By AGE_COLUMN = By.id("reportTable_age");
    static final By COMMUNITY_COLUMN = By.id("reportTable_community");
    static final By MESSAGE_COLUMN = By.id("reportTable_messageId");
    static final By SENT_DATE_COLUMN = By.id("reportTable_sendDate");
    static final By EXPECTED_DURATION_COLUMN = By.id("reportTable_expectedDuration");
    static final By TIME_LISTENED_COLUMN = By.id("reportTable_timeListenedTo");
    static final By PERCENT_LISTENED_COLUMN = By.id("reportTable_messagePercentListened");
    static final By DATE_RECEIVED_COLUMN = By.id("reportTable_receivedDate");
    static final By NUMBER_OF_ATTEMPTS_COLUMN = By.id("reportTable_numberOfAttempts");
    static final By SMS_COLUMN = By.id("reportTable_sms");
    static final By SMS_RECEIVED_COLUMN = By.id("reportTable_smsReceivedDate");
    static final By STAGE_ID_COLUMN = By.id("reportTable_stageId");
    static final By TABLE = By.xpath("//table[@class='ui-jqgrid-htable']");

    public NumberOfTimesListenedReportPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }

    public boolean existTable() {
        boolean status = false;
        try {
            if (!findElement(TABLE).getAttribute("innerHTML").isEmpty()) {
                status = true;
            }
        } catch (Exception e) {
            getLogger().error("existTable - Exception - Reason : " + e.getLocalizedMessage(), e);
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
            getLogger().error("isReportEmpty -  Exc. Reason : " + e.getLocalizedMessage(), e);
            status = false;
        }
        return status;
    }

    public boolean checkColumns() {
        List<By> columns = Arrays.asList(ID_COLUMN, PHONE_COLUMN, GENDER_COLUMN, AGE_COLUMN, COMMUNITY_COLUMN, MESSAGE_COLUMN, SENT_DATE_COLUMN, EXPECTED_DURATION_COLUMN,
                TIME_LISTENED_COLUMN, PERCENT_LISTENED_COLUMN, DATE_RECEIVED_COLUMN, NUMBER_OF_ATTEMPTS_COLUMN, SMS_COLUMN, SMS_RECEIVED_COLUMN, STAGE_ID_COLUMN);
        List<String> columnNames = Arrays.asList("Participant Id", "Phone", "Gender", "Age", "Location (Community)", "Message ID", "Sent Date",
                "Expected Duration", "Time Listened To", "Percent Listened", "Received Date", "No. of Attempts", "SMS", "SMS Received Date", "Stage ID");
        try {
            for (int i = 0; i < columns.size(); i++) {
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

    public void sleep(long timeout) throws InterruptedException {
        Thread.sleep(timeout);
        
    }
}
