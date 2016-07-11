package org.motechproject.bookingapp.dto;

public class CapacityReportDto {

    private String date;

    private String location;

    private Integer maxCapacity;

    private Integer availableCapacity;

    private Integer screeningSlotRemaining;

    private Integer vaccineSlotRemaining;

    public CapacityReportDto() {
    }

    public CapacityReportDto(String date, String location, Integer maxCapacity, Integer availableCapacity, Integer screeningSlotRemaining, Integer vaccineSlotRemaining) {
        this.date = date;
        this.location = location;
        this.maxCapacity = maxCapacity;
        this.availableCapacity = availableCapacity;
        this.screeningSlotRemaining = screeningSlotRemaining;
        this.vaccineSlotRemaining = vaccineSlotRemaining;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(Integer availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public Integer getScreeningSlotRemaining() {
        return screeningSlotRemaining;
    }

    public void setScreeningSlotRemaining(Integer screeningSlotRemaining) {
        this.screeningSlotRemaining = screeningSlotRemaining;
    }

    public Integer getVaccineSlotRemaining() {
        return vaccineSlotRemaining;
    }

    public void setVaccineSlotRemaining(Integer vaccineSlotRemaining) {
        this.vaccineSlotRemaining = vaccineSlotRemaining;
    }
}
