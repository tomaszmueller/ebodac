package org.motechproject.ebodac.uitest.page;

import org.motechproject.uitest.page.AbstractBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Map;

public class ParticipantEditPage extends AbstractBasePage {

    private static final String EMPTY = "";
    private static final String TEMNE = "Temne";
    private static final String SUSU = "Susu";
    private static final String LIMBA = "Limba";
    private static final String KRIO = "Krio";
    private static final String ENGLISH = "English";
    private static final int MIN_LANGUAGE_POS = 1;
    private static final int LAST_POSITION_LANGUAGE = 6;
    private static final int START_POS_LANGUAGE = 2;

    private Map<String, String> mapLangPos = new HashMap<String, String>();
    public static final String URL_PATH = "/home#/mds/dataBrowser";
    static final int SMALL_TIMEOUT = 500;
    static final int SLEEP_2000 = 2000;
    static final int TIMEOUT_BORDER = 10000;
    static final By PHONE_NUMBER_FIELD = By.id("phoneNumberForm");
    static final By SAVE_BUTTON = By.xpath("//button[@ng-click='addEntityInstance()']");
    static final By CONFIRMATION_BUTTON = By.xpath("//button[@ng-click='addEntityInstanceDefault()']");
    static final By LANGUAGE_FIELD = By.xpath("(//button[@type='button'])[2]");
    static final String LANGUAGE_PATH = "//*[@id='dataBrowser']/div[1]/div/div/ng-form/div[1]/form/div[8]/div/ng-form/div[1]/div/ul/li[";
    static final String LANGUAGE_PATH_END = "]/a/label";
    static final By SELECTED_LANGUAGE = By
            .xpath("//*[@id='dataBrowser']/div[1]/div/div/ng-form/div[1]/form/div[8]/div/ng-form/div[1]/div/button");
    private static final By SELECTED_PHONE_NUMBER = By.xpath("//*[@id='phoneNumberForm']");
    static final By DELETE_BUTTON = By.xpath("//div[@id='dataBrowser']/div/div/div/div/button[2]");
    static final By DELETE_CONFIRMATION_BUTTON = By.xpath("//div[@id='deleteInstanceModal']/div[2]/div/div[3]/button");
    static final By DATE = By.linkText("1");
    static final By POPUP_OK = By.id("popup_ok");
    static final By PICK_DATA = By.cssSelector(".ui-state-active");
    static final By POPUP_CONTENT = By.id("popup_content");
    static final By DATE_TABLE = By.xpath("//div[3]/div/ng-form/div/input");
    static final By VISIT_TABLE = By.xpath("//div[@ng-include='loadEditValueForm(field)']/table");
    static final By PLANNED_VISITS_DATE = By
            .xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[3]/div/ng-form/div/input");
    static final By NAME = By
            .xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[2]/div/ng-form/div/input[@type='text']");
    static final By HOUSEHOLD_NAME = By
            .xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[3]/div/ng-form/div/input[@type='text']");
    static final By HEAD_OF_HOUSEHOLD = By
            .xpath("//div[@id='dataBrowser']/div/div/div/ng-form/div/form/div[4]/div/ng-form/div/input[@type='text']");
    static final By BUTTON = By.xpath("//div[@ng-include='loadEditValueForm(field)']/table/tbody/tr/*/button");

    public ParticipantEditPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String expectedUrlPath() {
        return getServerURL() + URL_PATH;
    }

    @Override
    public void goToPage() {

    }

    public void changePhoneNumber(String number) throws InterruptedException {
        Thread.sleep(SLEEP_2000);
        setPhoneNumber(number);
        Thread.sleep(SLEEP_2000);
        clickOnSaveButton();
        Thread.sleep(SLEEP_2000);
        clickOnConfirmationButton();
        Thread.sleep(SLEEP_2000);
    }

    public void clickOnConfirmationButton() throws InterruptedException {
        clickWhenVisible(CONFIRMATION_BUTTON);
    }

    public void clickOnSaveButton() {
        clickOn(SAVE_BUTTON);
    }

    public boolean setPhoneNumber(String number) throws InterruptedException {
        findElement(PHONE_NUMBER_FIELD).clear();
        Thread.sleep(SLEEP_2000);
        findElement(PHONE_NUMBER_FIELD).sendKeys(number);
        Thread.sleep(SLEEP_2000);
        if (findElement(PHONE_NUMBER_FIELD).getText().equals(number)) {
            return true;
        }
        return false;
    }

    public boolean changeLanguage(String languagePos) throws InterruptedException {
        boolean status = false;
        try {

            clickWhenVisible(SELECTED_LANGUAGE);
            int intPos = new Integer(languagePos).intValue();
            if (intPos <= LAST_POSITION_LANGUAGE && intPos > MIN_LANGUAGE_POS) {
                if (chooseLanguage(languagePos)) {
                    Thread.sleep(SMALL_TIMEOUT);
                    status = true;
                } else {
                    status = false;
                }
            } else {
                status = false;
            }

        } catch (NullPointerException e) {
            getLogger().error("changeLanguage - NullPointerException - languagePos :" + languagePos + " - Reason : "
                    + e.getLocalizedMessage(), e);
            status = false;

        } catch (Exception e) {
            getLogger().error("changeLanguage - Exception  - languagePos :" + languagePos + " - Reason : "
                    + e.getLocalizedMessage(), e);
            status = false;
        }

        return status;
    }

    /**
     * We close the Edit Page
     * 
     * @return
     * @throws InterruptedException
     */
    public boolean closeEditPage() throws InterruptedException {
        boolean status = true;
        Thread.sleep(SMALL_TIMEOUT);
        try {
            clickOnSaveButton();
            clickOnConfirmationButton();
        } catch (InterruptedException e) {
            status = false;
            throw new InterruptedException(e.getLocalizedMessage());
        }
        return status;
    }

    /**
     * We use this method to change the language
     * 
     * @param languagePos
     *            : The position in the html
     * @return true if the change works.
     * @throws InterruptedException
     */
    public boolean chooseLanguage(String languagePos) throws InterruptedException {
        boolean status = false;
        // We choose the language.
        String routelanguage = LANGUAGE_PATH + languagePos + LANGUAGE_PATH_END;
        By xpathlanguage = null;
        try {
            xpathlanguage = By.xpath(routelanguage);
            if (!findElement(xpathlanguage).isDisplayed()) {
                clickOn(SELECTED_LANGUAGE);
            }
            clickOn(xpathlanguage);
            Thread.sleep(TIMEOUT_BORDER);
            status = true;

        } catch (Exception e) {
            status = false;
            getLogger().error(
                    "chooseLanguage - Exception - RouteLanguage : ##" + routelanguage + "##" + e.getLocalizedMessage(),
                    e);

        }
        return status;
    }

    public void deleteParticipant() throws InterruptedException {
        waitForElement(DELETE_BUTTON);
        Thread.sleep(SMALL_TIMEOUT);
        findElement(DELETE_BUTTON).sendKeys(Keys.RETURN);
        waitForElement(DELETE_CONFIRMATION_BUTTON);
        clickOn(DELETE_CONFIRMATION_BUTTON);
    }

    public void changeVisit() {
        clickOnSaveButton();
        clickOn(POPUP_OK);
    }

    public void clickPlannedVisitDate() throws InterruptedException {
        Thread.sleep(SMALL_TIMEOUT);
        clickWhenVisible(PLANNED_VISITS_DATE);
        Thread.sleep(SMALL_TIMEOUT);
        clickOn(DATE);
    }

    public void clickOK() throws InterruptedException {
        Thread.sleep(SMALL_TIMEOUT);
        clickOn(POPUP_OK);
    }

    public boolean dateEnroll() {
        try {
            return findElement(POPUP_CONTENT).getText().contains(
                    "Error occurred during re-enrolling: Cannot re-enroll Participant for that Visit, because motech projected date wasnt changed");
        } catch (Exception ex) {
            return false;
        }
    }

    public String getChoosenData() {
        return findElement(PICK_DATA).getText();
    }

    public void enter() throws InterruptedException {
        int day = 1;
        do {
            day++;
            Thread.sleep(SMALL_TIMEOUT);
            clickOn(DATE_TABLE);
            WebElement date = findElement(By.linkText(EMPTY + day + EMPTY));
            date.click();
            clickOnSaveButton();
            clickOn(POPUP_OK);
        } while (dateEnroll());
    }

    public boolean isNameEditable() {
        WebElement nameElement = findElement(NAME);
        try {
            if (nameElement.getAttribute("readonly").contains("readonly")
                    || nameElement.getAttribute("readonly").contains("true")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isHouseholdNameEditable() {
        WebElement nameElement = findElement(HOUSEHOLD_NAME);
        try {
            if (nameElement.getAttribute("readonly").contains("readonly")
                    || nameElement.getAttribute("readonly").contains("true")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isHeadOfHouseholdEditable() {
        WebElement nameElement = findElement(HEAD_OF_HOUSEHOLD);
        try {
            if (nameElement.getAttribute("readonly").contains("readonly")
                    || nameElement.getAttribute("readonly").contains("true")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean checkButtons() {
        try {
            if (findElement(BUTTON) != null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isTable() {
        try {
            waitForElement(VISIT_TABLE);
            if (findElement(VISIT_TABLE) != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * We provide the language
     * 
     * @return
     */
    public String getLanguage() {
        return findElement(SELECTED_LANGUAGE).getText();
    }

    /**
     * We provide the Phone Number
     * 
     * @return
     */
    public String getPhoneNumber() throws Exception {
        String number = EMPTY;

        if (findElement(SELECTED_PHONE_NUMBER) != null) {
            number = findElement(SELECTED_PHONE_NUMBER).getText();
            getLogger().error("getphone number : " + number);
            // To make sure we get the phone number from the field.
            if (EMPTY.equalsIgnoreCase(number)) {
                number = findElement(SELECTED_PHONE_NUMBER).getAttribute("innerHTML");
            }
            if (EMPTY.equalsIgnoreCase(number)) {
                number = findElement(SELECTED_PHONE_NUMBER).getAttribute("innerText");
            }
            if (EMPTY.equalsIgnoreCase(number)) {
                number = findElement(SELECTED_PHONE_NUMBER).getAttribute("textContent");
            }
        } else {
            getLogger().error("getPhoneNumber - cannot find Element " + SELECTED_PHONE_NUMBER.toString());
        }

        return number;
    }

    /**
     * This method maps the list of languages with its positions.
     * 
     * @throws InterruptedException
     */
    public void setListLanguagePosition() throws Exception {

        String routelang = new String();
        String languageText = new String();
        By xpathlanguage = null;
        String poshtmlvalue = null;

        for (int counter = START_POS_LANGUAGE; counter <= LAST_POSITION_LANGUAGE; counter++) {

            routelang = LANGUAGE_PATH + ((new Integer(counter)).toString()) + LANGUAGE_PATH_END;
            xpathlanguage = By.xpath(routelang);
            languageText = findElement(xpathlanguage).getAttribute("innerHTML");
            // We get the position of the language.
            poshtmlvalue = findElement(By.xpath(routelang + "/input")).getAttribute("value");

            if (languageText.contains(ENGLISH)) {
                mapLangPos.put(ENGLISH, poshtmlvalue);
            } else if (languageText.contains(KRIO)) {
                mapLangPos.put(KRIO, poshtmlvalue);
            } else if (languageText.contains(LIMBA)) {
                mapLangPos.put(LIMBA, poshtmlvalue);
            } else if (languageText.contains(SUSU)) {
                mapLangPos.put(SUSU, poshtmlvalue);
            } else if (languageText.contains(TEMNE)) {
                mapLangPos.put(TEMNE, poshtmlvalue);
            }

        }

    }

    public Map<String, String> getMapLangPos() {
        return mapLangPos;
    }

    public void setMapLangPos(Map<String, String> mapLangPos) {
        this.mapLangPos = mapLangPos;
    }

    public String changeLanguageFromOriginal(String originalLanguage) {
        boolean langFound = false;
        String newLanguage = null;
        // We asign the original langauge at the begining to find the new one.
        newLanguage = originalLanguage;
        for (String key : mapLangPos.keySet()) {
            if (!langFound && (originalLanguage.equalsIgnoreCase(newLanguage))) {
                newLanguage = key;
            } else {
                langFound = true;
            }
        }
        return newLanguage;
    }

    public void sleep(long timeout) throws InterruptedException {
        Thread.sleep(timeout);

    }

}
