package org.motechproject.bookingapp.dto;

public class CapacityInfoDto {

    private String clinic;

    private Integer maxCapacity;

    private Integer availableCapacity;

    private Integer screeningSlotRemaining;

    private Integer vaccineSlotRemaining;

    public CapacityInfoDto() {
    }

    public CapacityInfoDto(String clinic, Integer maxCapacity, Integer availableCapacity, Integer screeningSlotRemaining, Integer vaccineSlotRemaining) {
        this.clinic = clinic;
        this.maxCapacity = maxCapacity;
        this.availableCapacity = availableCapacity;
        this.screeningSlotRemaining = screeningSlotRemaining;
        this.vaccineSlotRemaining = vaccineSlotRemaining;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
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
