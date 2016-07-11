package org.motechproject.bookingapp.dto;

import org.motechproject.bookingapp.domain.Screening;

public class ScreeningDto {

    private String id;
    private String volunteerId;
    private String clinicId;
    private String date;
    private String startTime;

    public ScreeningDto() {
    }

    public ScreeningDto(Screening screening) {
        setId(screening.getId().toString());
        setVolunteerId(screening.getVolunteer().getId().toString());
        setDate(screening.getDate().toString());

        if (screening.getClinic() != null) {
            setClinicId(screening.getClinic().getId().toString());
        } else {
            setClinicId(null);
        }

        if (screening.getStartTime() != null) {
            setStartTime(screening.getStartTime().toString());
        } else {
            setStartTime(null);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(String volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
