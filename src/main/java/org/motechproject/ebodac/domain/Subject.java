package org.motechproject.ebodac.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.UIDisplayable;

import javax.jdo.annotations.Column;
import java.util.Objects;

/**
 * Models data for registration of Subject in EBODAC
 */
@Entity(recordHistory = true)
public class Subject {

    /**
     *  Fields captured in ZETES
     */

    @UIDisplayable(position = 0)
    @Field(required = true)
    private String enrolmentId;

    @UIDisplayable(position = 1)
    @Field(required = true)
    private String name;

    @UIDisplayable(position = 2)
    @Field(required = true)
    private String householdName;

    @UIDisplayable(position = 3)
    @Field
    private String headOfHousehold;

    @UIDisplayable(position = 4)
    @Column(length = 20)
    @Field(required = true)
    private String phoneNumber;

    @UIDisplayable(position = 5)
    @Field(required = true)
    private String address;

    @UIDisplayable(position = 7)
    @Column(length = 20)
    @Field(required = true)
    private Language language;

    @UIDisplayable(position = 8)
    @Field(required = true)
    private String siteId;

    @UIDisplayable(position = 9)
    @Field(required = true)
    private String community;

    /**
     *  Fields captured in RAVE
     */
    @UIDisplayable(position = 6)
    @Field
    private Gender gender;

    /**
     *  Motech internal fields
     */

    @UIDisplayable(position = 10)
    @Field(defaultValue = "false")
    private boolean changed;


    public Subject() {
    }

    public Subject(String phoneNumber, String name, String householdName, String enrolmentId,
                   String siteId, String address, Language language, String community) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.householdName = householdName;
        this.enrolmentId = enrolmentId;
        this.siteId = siteId;
        this.address = address;
        this.language = language;
        this.community = community;
    }

    public Subject(String phoneNumber, String name, String householdName, String enrolmentId,
                   String siteId, String address, Language language, String community, String headOfHousehold) {
        this(phoneNumber, name, householdName, enrolmentId, siteId, address, language, community);
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

    public String getEnrolmentId() {
        return enrolmentId;
    }

    public void setEnrolmentId(String enrolmentId) {
        this.enrolmentId = enrolmentId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Boolean getChanged() {
        return changed;
    }

    public void setChanged(Boolean changed) {
        this.changed = changed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPhoneNumber(), getName(), householdName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Subject other = (Subject) obj;

        return Objects.equals(this.getName(), other.getName()) &&
                Objects.equals(this.getHouseholdName(), other.getHouseholdName()) &&
                Objects.equals(this.getPhoneNumber(), other.getPhoneNumber());
    }

    @Override
    public String toString() {
        return String.format("Subject{name='%s', householdName='%s', phoneNumber='%s'}",
                getName(), getHouseholdName(), getPhoneNumber());
    }
}