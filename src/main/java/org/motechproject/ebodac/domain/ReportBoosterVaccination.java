package org.motechproject.ebodac.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.UIDisplayable;
import org.motechproject.mds.annotations.NonEditable;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.util.SecurityMode;

@Access(value = SecurityMode.PERMISSIONS, members = {"manageEbodac"})
@Entity
public class ReportBoosterVaccination extends ReportVaccinationAbstract {

    @NonEditable
    @UIDisplayable(position = 1)
    @Field
    private Integer peopleBoostered;

    public ReportBoosterVaccination() {
    }

    public ReportBoosterVaccination(DateTime date, Integer adultMales, Integer adultFemales, Integer children_0_5, Integer children_6_11,
                                    Integer children_12_17, Integer adultUnidentified, Integer adultUndifferentiated, Integer peopleBoostered) {
        super(date, adultMales, adultFemales, children_0_5, children_6_11, children_12_17, adultUnidentified, adultUndifferentiated);
        this.peopleBoostered = peopleBoostered;
    }

    public Integer getPeopleBoostered() {
        return peopleBoostered;
    }

    public void setPeopleBoostered(Integer peopleBoostered) {
        this.peopleBoostered = peopleBoostered;
    }

    public void updateReportData(Integer adultMales, Integer adultFemales, Integer children_0_5, Integer children_6_11,
                                 Integer children_12_17, Integer adultUnidentified, Integer adultUndifferentiated, Integer peopleBoostered) {
        updateReportData(adultMales, adultFemales, children_0_5, children_6_11, children_12_17, adultUnidentified, adultUndifferentiated);
        this.peopleBoostered = peopleBoostered;
    }
}
