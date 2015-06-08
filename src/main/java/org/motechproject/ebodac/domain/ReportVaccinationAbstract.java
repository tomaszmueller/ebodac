package org.motechproject.ebodac.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public abstract class ReportVaccinationAbstract {

    @Field
    private DateTime date;

    @Field
    private Integer adultMales;

    @Field
    private Integer adultFemales;

    @Field
    private Integer children_0_5;

    @Field
    private Integer children_6_11;

    @Field
    private Integer children_12_17;

    public ReportVaccinationAbstract() {
    }

    public ReportVaccinationAbstract(DateTime date, Integer adultMales, Integer adultFemales, Integer children_0_5,
                                     Integer children_6_11, Integer children_12_17) {
        this.date = date;
        this.adultMales = adultMales;
        this.adultFemales = adultFemales;
        this.children_0_5 = children_0_5;
        this.children_6_11 = children_6_11;
        this.children_12_17 = children_12_17;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public Integer getAdultMales() {
        return adultMales;
    }

    public void setAdultMales(Integer adultMales) {
        this.adultMales = adultMales;
    }

    public Integer getAdultFemales() {
        return adultFemales;
    }

    public void setAdultFemales(Integer adultFemales) {
        this.adultFemales = adultFemales;
    }

    public Integer getChildren_0_5() {
        return children_0_5;
    }

    public void setChildren_0_5(Integer children_0_5) {
        this.children_0_5 = children_0_5;
    }

    public Integer getChildren_6_11() {
        return children_6_11;
    }

    public void setChildren_6_11(Integer children_6_11) {
        this.children_6_11 = children_6_11;
    }

    public Integer getChildren_12_17() {
        return children_12_17;
    }

    public void setChildren_12_17(Integer children_12_17) {
        this.children_12_17 = children_12_17;
    }
}
