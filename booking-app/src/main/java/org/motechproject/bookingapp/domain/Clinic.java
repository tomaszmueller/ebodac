package org.motechproject.bookingapp.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.NonEditable;
import org.motechproject.mds.annotations.UIDisplayable;

import javax.jdo.annotations.Unique;

@Entity(maxFetchDepth = 2)
public class Clinic {

    @Field
    private Long id;

    @Unique
    @UIDisplayable(position = 0)
    @Field(required = true)
    private String siteId;

    @UIDisplayable(position = 1)
    @Field(required = true)
    private String location;

    @UIDisplayable(position = 6)
    @Field(displayName = "Amount of Rooms", required = true, defaultValue = "1")
    private Integer numberOfRooms;

    @UIDisplayable(position = 2)
    @Field(required = true, defaultValue = "10")
    private Integer maxCapacityByDay;

    @UIDisplayable(position = 3)
    @Field(required = true, defaultValue = "5")
    private Integer maxScreeningVisits;

    @UIDisplayable(position = 4)
    @Field(required = true, defaultValue = "5")
    private Integer maxPrimeVisits;

    @UIDisplayable(position = 7)
    @Field(required = true, defaultValue = "10")
    private Integer maxPrimeFirstFollowUpVisits;

    @UIDisplayable(position = 8)
    @Field(required = true, defaultValue = "10")
    private Integer maxPrimeSecondFollowUpVisits;

    @UIDisplayable(position = 5)
    @Field(required = true, defaultValue = "5")
    private Integer maxBoosterVisits;

    @UIDisplayable(position = 9)
    @Field(required = true, defaultValue = "10")
    private Integer maxBoosterFirstFollowUpVisits;

    @UIDisplayable(position = 10)
    @Field(required = true, defaultValue = "10")
    private Integer maxBoosterSecondFollowUpVisits;

    @UIDisplayable(position = 11)
    @Field(required = true, defaultValue = "10")
    private Integer maxBoosterThirdFollowUpVisits;

    @UIDisplayable(position = 12)
    @Field(required = true, defaultValue = "10")
    private Integer maxFirstLongTermFollowUpVisits;

    @UIDisplayable(position = 13)
    @Field(required = true, defaultValue = "10")
    private Integer maxSecondLongTermFollowUpVisits;

    @UIDisplayable(position = 14)
    @Field(required = true, defaultValue = "10")
    private Integer maxThirdLongTermFollowUpVisits;

    @UIDisplayable(position = 15)
    @Field(required = true, defaultValue = "10")
    private Integer maxFourthLongTermFollowUpVisits;

    @UIDisplayable(position = 16)
    @Field(required = true, defaultValue = "10")
    private Integer maxFifthLongTermFollowUpVisits;

    @UIDisplayable(position = 17)
    @Field(required = true, defaultValue = "10")
    private Integer maxSixthLongTermFollowUpVisits;

    @UIDisplayable(position = 18)
    @Field(required = true, defaultValue = "10")
    private Integer maxSeventhLongTermFollowUpVisits;

    @NonEditable(display = false)
    @Field
    private String owner;

    public Clinic() {
    }

    public Clinic(String siteId, String location, Integer numberOfRooms, Integer maxCapacityByDay, Integer maxScreeningVisits, //NO CHECKSTYLE ParameterNumber
                  Integer maxPrimeVisits, Integer maxPrimeFirstFollowUpVisits, Integer maxPrimeSecondFollowUpVisits, Integer maxBoosterVisits,
                  Integer maxBoosterFirstFollowUpVisits, Integer maxBoosterSecondFollowUpVisits, Integer maxBoosterThirdFollowUpVisits,
                  Integer maxFirstLongTermFollowUpVisits, Integer maxSecondLongTermFollowUpVisits, Integer maxThirdLongTermFollowUpVisits,
                  Integer maxFourthLongTermFollowUpVisits, Integer maxFifthLongTermFollowUpVisits, Integer maxSixthLongTermFollowUpVisits,
                  Integer maxSeventhLongTermFollowUpVisits) {
        this.siteId = siteId;
        this.location = location;
        this.numberOfRooms = numberOfRooms;
        this.maxCapacityByDay = maxCapacityByDay;
        this.maxScreeningVisits = maxScreeningVisits;
        this.maxPrimeVisits = maxPrimeVisits;
        this.maxPrimeFirstFollowUpVisits = maxPrimeFirstFollowUpVisits;
        this.maxPrimeSecondFollowUpVisits = maxPrimeSecondFollowUpVisits;
        this.maxBoosterVisits = maxBoosterVisits;
        this.maxBoosterFirstFollowUpVisits = maxBoosterFirstFollowUpVisits;
        this.maxBoosterSecondFollowUpVisits = maxBoosterSecondFollowUpVisits;
        this.maxBoosterThirdFollowUpVisits = maxBoosterThirdFollowUpVisits;
        this.maxFirstLongTermFollowUpVisits = maxFirstLongTermFollowUpVisits;
        this.maxSecondLongTermFollowUpVisits = maxSecondLongTermFollowUpVisits;
        this.maxThirdLongTermFollowUpVisits = maxThirdLongTermFollowUpVisits;
        this.maxFourthLongTermFollowUpVisits = maxFourthLongTermFollowUpVisits;
        this.maxFifthLongTermFollowUpVisits = maxFifthLongTermFollowUpVisits;
        this.maxSixthLongTermFollowUpVisits = maxSixthLongTermFollowUpVisits;
        this.maxSeventhLongTermFollowUpVisits = maxSeventhLongTermFollowUpVisits;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(Integer numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public Integer getMaxScreeningVisits() {
        return maxScreeningVisits;
    }

    public void setMaxScreeningVisits(Integer maxScreeningVisits) {
        this.maxScreeningVisits = maxScreeningVisits;
    }

    public Integer getMaxPrimeVisits() {
        return maxPrimeVisits;
    }

    public void setMaxPrimeVisits(Integer maxPrimeVisits) {
        this.maxPrimeVisits = maxPrimeVisits;
    }

    public Integer getMaxPrimeFirstFollowUpVisits() {
        return maxPrimeFirstFollowUpVisits;
    }

    public void setMaxPrimeFirstFollowUpVisits(Integer maxPrimeFirstFollowUpVisits) {
        this.maxPrimeFirstFollowUpVisits = maxPrimeFirstFollowUpVisits;
    }

    public Integer getMaxPrimeSecondFollowUpVisits() {
        return maxPrimeSecondFollowUpVisits;
    }

    public void setMaxPrimeSecondFollowUpVisits(Integer maxPrimeSecondFollowUpVisits) {
        this.maxPrimeSecondFollowUpVisits = maxPrimeSecondFollowUpVisits;
    }

    public Integer getMaxBoosterVisits() {
        return maxBoosterVisits;
    }

    public void setMaxBoosterVisits(Integer maxBoosterVisits) {
        this.maxBoosterVisits = maxBoosterVisits;
    }

    public Integer getMaxBoosterFirstFollowUpVisits() {
        return maxBoosterFirstFollowUpVisits;
    }

    public void setMaxBoosterFirstFollowUpVisits(Integer maxBoosterFirstFollowUpVisits) {
        this.maxBoosterFirstFollowUpVisits = maxBoosterFirstFollowUpVisits;
    }

    public Integer getMaxBoosterSecondFollowUpVisits() {
        return maxBoosterSecondFollowUpVisits;
    }

    public void setMaxBoosterSecondFollowUpVisits(Integer maxBoosterSecondFollowUpVisits) {
        this.maxBoosterSecondFollowUpVisits = maxBoosterSecondFollowUpVisits;
    }

    public Integer getMaxBoosterThirdFollowUpVisits() {
        return maxBoosterThirdFollowUpVisits;
    }

    public void setMaxBoosterThirdFollowUpVisits(Integer maxBoosterThirdFollowUpVisits) {
        this.maxBoosterThirdFollowUpVisits = maxBoosterThirdFollowUpVisits;
    }

    public Integer getMaxFirstLongTermFollowUpVisits() {
        return maxFirstLongTermFollowUpVisits;
    }

    public void setMaxFirstLongTermFollowUpVisits(Integer maxFirstLongTermFollowUpVisits) {
        this.maxFirstLongTermFollowUpVisits = maxFirstLongTermFollowUpVisits;
    }

    public Integer getMaxSecondLongTermFollowUpVisits() {
        return maxSecondLongTermFollowUpVisits;
    }

    public void setMaxSecondLongTermFollowUpVisits(Integer maxSecondLongTermFollowUpVisits) {
        this.maxSecondLongTermFollowUpVisits = maxSecondLongTermFollowUpVisits;
    }

    public Integer getMaxThirdLongTermFollowUpVisits() {
        return maxThirdLongTermFollowUpVisits;
    }

    public Integer getMaxFourthLongTermFollowUpVisits() {
        return maxFourthLongTermFollowUpVisits;
    }

    public void setMaxFourthLongTermFollowUpVisits(Integer maxFourthLongTermFollowUpVisits) {
        this.maxFourthLongTermFollowUpVisits = maxFourthLongTermFollowUpVisits;
    }

    public Integer getMaxFifthLongTermFollowUpVisits() {
        return maxFifthLongTermFollowUpVisits;
    }

    public void setMaxFifthLongTermFollowUpVisits(Integer maxFifthLongTermFollowUpVisits) {
        this.maxFifthLongTermFollowUpVisits = maxFifthLongTermFollowUpVisits;
    }

    public Integer getMaxSixthLongTermFollowUpVisits() {
        return maxSixthLongTermFollowUpVisits;
    }

    public void setMaxSixthLongTermFollowUpVisits(Integer maxSixthLongTermFollowUpVisits) {
        this.maxSixthLongTermFollowUpVisits = maxSixthLongTermFollowUpVisits;
    }

    public Integer getMaxSeventhLongTermFollowUpVisits() {
        return maxSeventhLongTermFollowUpVisits;
    }

    public void setMaxSeventhLongTermFollowUpVisits(Integer maxSeventhLongTermFollowUpVisits) {
        this.maxSeventhLongTermFollowUpVisits = maxSeventhLongTermFollowUpVisits;
    }

    public void setMaxThirdLongTermFollowUpVisits(Integer maxThirdLongTermFollowUpVisits) {
        this.maxThirdLongTermFollowUpVisits = maxThirdLongTermFollowUpVisits;
    }

    public Integer getMaxCapacityByDay() {
        return maxCapacityByDay;
    }

    public void setMaxCapacityByDay(Integer maxCapacityByDay) {
        this.maxCapacityByDay = maxCapacityByDay;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return location;
    }
}

