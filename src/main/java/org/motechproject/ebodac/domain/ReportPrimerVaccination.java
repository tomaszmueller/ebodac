package org.motechproject.ebodac.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.UIDisplayable;

@Entity
public class ReportPrimerVaccination extends ReportVaccinationAbstract {

    @UIDisplayable(position = 1)
    @Field
    private Integer peopleVaccinated;

    public ReportPrimerVaccination() {
    }

    public ReportPrimerVaccination(DateTime date, Integer adultMales, Integer adultFemales, Integer children_0_5,
                                   Integer children_6_11, Integer children_12_17, Integer peopleVaccinated) {
        super(date, adultMales, adultFemales, children_0_5, children_6_11, children_12_17);
        this.peopleVaccinated = peopleVaccinated;
    }

    public Integer getPeopleVaccinated() {
        return peopleVaccinated;
    }

    public void setPeopleVaccinated(Integer peopleVaccinated) {
        this.peopleVaccinated = peopleVaccinated;
    }
}
