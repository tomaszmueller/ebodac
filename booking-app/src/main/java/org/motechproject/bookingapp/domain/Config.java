package org.motechproject.bookingapp.domain;

import java.util.ArrayList;
import java.util.List;

public class Config {

    private List<String> clinicMainFields;

    private List<String> clinicExtendedFields;

    public Config() {
    }

    public List<String> getClinicMainFields() {
        if (clinicMainFields == null) {
            clinicMainFields = new ArrayList<>();
        }
        return clinicMainFields;
    }

    public void setClinicMainFields(List<String> clinicMainFields) {
        this.clinicMainFields = clinicMainFields;
    }

    public List<String> getClinicExtendedFields() {
        if (clinicExtendedFields == null) {
            clinicExtendedFields = new ArrayList<>();
        }
        return clinicExtendedFields;
    }

    public void setClinicExtendedFields(List<String> clinicExtendedFields) {
        this.clinicExtendedFields = clinicExtendedFields;
    }
}
