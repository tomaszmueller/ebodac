package org.motechproject.ebodac.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.UIDisplayable;

import javax.jdo.annotations.Unique;

@Entity
public abstract class ReportVaccinationAbstract {

    @Unique
    @UIDisplayable(position = 0)
    @Field
    private DateTime date;

    @UIDisplayable(position = 2)
    @Field
    private Integer adultMales;

    @UIDisplayable(position = 3)
    @Field
    private Integer adultFemales;

    @UIDisplayable(position = 6)
    @Field(displayName = "Children 0-5")
    private Integer children_0_5;

    @UIDisplayable(position = 5)
    @Field(displayName = "Children 6-11")
    private Integer children_6_11;

    @UIDisplayable(position = 4)
    @Field(displayName = "Children 12-17")
    private Integer children_12_17;

    @UIDisplayable(position = 8)
    @Field
    private Integer adultUnidentified;

    @UIDisplayable(position = 7)
    @Field
    private Integer adultUndifferentiated;

    public ReportVaccinationAbstract() {
    }

    public ReportVaccinationAbstract(DateTime date, Integer adultMales, Integer adultFemales, Integer children_0_5, Integer children_6_11,
                                     Integer children_12_17, Integer adultUnidentified, Integer adultUndifferentiated) {
        this.date = date;
        this.adultMales = adultMales;
        this.adultFemales = adultFemales;
        this.children_0_5 = children_0_5;
        this.children_6_11 = children_6_11;
        this.children_12_17 = children_12_17;
        this.adultUnidentified = adultUnidentified;
        this.adultUndifferentiated = adultUndifferentiated;
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

    public Integer getAdultUnidentified() {
        return adultUnidentified;
    }

    public void setAdultUnidentified(Integer adultUnidentified) {
        this.adultUnidentified = adultUnidentified;
    }

    public Integer getAdultUndifferentiated() {
        return adultUndifferentiated;
    }

    public void setAdultUndifferentiated(Integer adultUndifferentiated) {
        this.adultUndifferentiated = adultUndifferentiated;
    }

    public void updateReportData(Integer adultMales, Integer adultFemales, Integer children_0_5, Integer children_6_11,
                                 Integer children_12_17, Integer adultUnidentified, Integer adultUndifferentiated) {
        this.adultMales = adultMales;
        this.adultFemales = adultFemales;
        this.children_0_5 = children_0_5;
        this.children_6_11 = children_6_11;
        this.children_12_17 = children_12_17;
        this.adultUnidentified = adultUnidentified;
        this.adultUndifferentiated = adultUndifferentiated;
    }
}
