package org.motechproject.ebodac.web;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.validation.ValidationError;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *  DTO for request coming from Zetes
 */
public class SubmitSubjectRequest {

    private String subjectId;

    private String name;

    private String householdName;

    private String headOfHousehold;

    private String phoneNumber;

    private String address;

    private String language;

    private String community;

    private String siteId;

    private List<ValidationError> validationErrors = new ArrayList<>();

    public SubmitSubjectRequest() {
    }

    public SubmitSubjectRequest(String phoneNumber, String name, String householdName, String subjectId,
                                String address, String language, String community, String headOfHousehold) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.householdName = householdName;
        this.subjectId = subjectId;
        this.address = address;
        this.language = language;
        this.community = community;
        this.headOfHousehold = headOfHousehold;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHouseholdName() {
        return householdName;
    }

    public void setHouseholdName(String householdName) {
        this.householdName = householdName;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getHeadOfHousehold() {
        return headOfHousehold;
    }

    public void setHeadOfHousehold(String headOfHousehold) {
        this.headOfHousehold = headOfHousehold;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public List<ValidationError> validate() {

        /**
         *  phoneNumber validations
         */

        if (StringUtils.isNotBlank(phoneNumber) && (phoneNumber.length() != 9 || !phoneNumber.startsWith("0"))) {
                validationErrors.add(new ValidationError(ValidationError.PHONE_NUMBER_NOT_CORRECT));
        }

        /**
         *  subjectId validations
         */

        if (StringUtils.isBlank(subjectId)) {
            validationErrors.add(new ValidationError(ValidationError.SUBJECT_ID_NULL));
        } else {
            if (!StringUtils.isNumeric(subjectId)) {
                validationErrors.add(new ValidationError(ValidationError.SUBJECT_ID_NOT_NUMERIC));
            } else {
                if (!isSubjectIdValid(subjectId)) {
                    validationErrors.add(new ValidationError(ValidationError.SUBJECT_ID_NOT_VERIFIED));
                }
            }
        }

        /**
         *  name validations
         */

        if (StringUtils.isBlank(name)) {
            validationErrors.add(new ValidationError(ValidationError.NAME_NULL));
        } else {
            if (name.matches(".*\\d.*")) {
                validationErrors.add(new ValidationError(ValidationError.NAME_HAS_DIGITS));
            }
        }

        /**
         *  householdName validations
         */

        if (StringUtils.isNotBlank(householdName) && householdName.matches(".*\\d.*")) {
                validationErrors.add(new ValidationError(ValidationError.HOUSEHOLD_NAME_HAS_DIGITS));
        }

        /**
         *  language validations
         */

        if (StringUtils.isBlank(language)) {
            validationErrors.add(new ValidationError(ValidationError.LANGUAGE_NULL));
        } else {
            if (!Language.getListOfCodes().contains(language)) {
                validationErrors.add(new ValidationError(ValidationError.LANGUAGE_NOT_CORRECT));
            }
        }

        /**
         *  headOfHousehold validations
         */

        if (StringUtils.isNotBlank(headOfHousehold) && headOfHousehold.matches(".*\\d.*")) {
                validationErrors.add(new ValidationError(ValidationError.HEAD_OF_HOUSEHOLD_HAS_DIGITS));
        }

        return validationErrors;
    }

    private Boolean isSubjectIdValid(String subjectId) {

        String lastTwoDigits = subjectId.substring(subjectId.length() - 2);
        String idWithoutLastTwoDigits = subjectId.substring(0, subjectId.length() - 2);
        String identificationCalculationResult = BigDecimal.valueOf(Double.valueOf(idWithoutLastTwoDigits) / 97D).toString();
        String expectedIdentificationDigits = identificationCalculationResult.
                substring(identificationCalculationResult.lastIndexOf(".") + 1, identificationCalculationResult.lastIndexOf(".") + 3);

        return lastTwoDigits.equals(expectedIdentificationDigits);

    }
}