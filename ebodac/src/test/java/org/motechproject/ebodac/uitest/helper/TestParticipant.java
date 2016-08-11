package org.motechproject.ebodac.uitest.helper;

public class TestParticipant {
    private String id;
    private String participantId;
    private String name;
    private String language;
    private String phoneNumber;
    private String siteId;
    private String siteName;
    private String householdName;
    private String headOfHousehold;
    private String community;
    private String address;

    public TestParticipant() {
        this.setId("9999999999");
        this.setParticipantId("9999999999");
        this.setName("TestTest");
        this.setLanguage("eng");
        this.setPhoneNumber("232000000000054");
        this.setSiteId("B05-SL10001");
        this.setSiteName("Kambia I");
        this.setHouseholdName("Kingdom");
        this.setHeadOfHousehold("King");
        this.setCommunity("Magadascar");
        this.setAddress("Nowa str.");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSiteId() {
        return this.siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return this.siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getHouseholdName() {
        return this.householdName;
    }

    public void setHouseholdName(String householdName) {
        this.householdName = householdName;
    }

    public String getHeadOfHousehold() {
        return this.headOfHousehold;
    }

    public void setHeadOfHousehold(String headOfHousehold) {
        this.headOfHousehold = headOfHousehold;
    }

    public String getCommunity() {
        return this.community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
