package org.motechproject.ebodac.domain;

import org.joda.time.LocalDate;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.UIDisplayable;
import org.motechproject.mds.annotations.NonEditable;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.util.SecurityMode;

@Access(value = SecurityMode.PERMISSIONS, members = {"manageEbodac"})
@Entity
public class ReportPrimerVaccination extends ReportVaccinationAbstract {

    @NonEditable
    @UIDisplayable(position = 1)
    @Field
    private Integer peopleVaccinated;

    public ReportPrimerVaccination() {
    }

    public ReportPrimerVaccination(LocalDate date, Integer adultMales, Integer adultFemales, Integer children_1_5, Integer children_6_11,
                                   Integer children_12_17, Integer adultUnidentified, Integer adultUndifferentiated, Integer peopleVaccinated) {
        super(date, adultMales, adultFemales, children_1_5, children_6_11, children_12_17, adultUnidentified, adultUndifferentiated);
        this.peopleVaccinated = peopleVaccinated;
    }

    public Integer getPeopleVaccinated() {
        return peopleVaccinated;
    }

    public void setPeopleVaccinated(Integer peopleVaccinated) {
        this.peopleVaccinated = peopleVaccinated;
    }

    public void updateReportData(Integer adultMales, Integer adultFemales, Integer children_1_5, Integer children_6_11,
                                 Integer children_12_17, Integer adultUnidentified, Integer adultUndifferentiated, Integer peopleVaccinated) {
        updateReportData(adultMales, adultFemales, children_1_5, children_6_11, children_12_17, adultUnidentified, adultUndifferentiated);
        this.peopleVaccinated = peopleVaccinated;
    }
}
