package org.motechproject.ebodac.uitest.helper;

/**
 * Created by tomasz on 30.09.15.
 */
public class TestParticipant {
    private String id;
    private String name;
    private String language;
    private String phoneNumber;
    private String siteId;
    private String householdName;
    private String headOfHousehold;
    private String community;
    private String address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getHouseholdName() {
        return householdName;
    }

    public void setHouseholdName(String householdName) {
        this.householdName = householdName;
    }

    public String getHeadOfHousehold() {
        return headOfHousehold;
    }

    public void setHeadOfHousehold(String headOfHousehold) {
        this.headOfHousehold = headOfHousehold;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public TestParticipant() {
        id = "1110079999";
        name = "TestTest";
        language = "English";
        phoneNumber = "999888777";
        siteId = "OTHER";
        householdName = "Kingdom";
        headOfHousehold = "King";
        community = "Klingon";
        address = "Nowa str.";
    }
}
