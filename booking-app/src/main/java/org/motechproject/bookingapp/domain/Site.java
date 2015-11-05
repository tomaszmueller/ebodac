package org.motechproject.bookingapp.domain;

import org.motechproject.mds.annotations.Entity;

import java.util.List;

@Entity
public class Site {

    private List<Clinic> clinics;

    public List<Clinic> getClinics() {
        return clinics;
    }

    public void setClinics(List<Clinic> clinics) {
        this.clinics = clinics;
    }
}
