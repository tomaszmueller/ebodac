package org.motechproject.ebodac.domain;

public class SubjectAgeRange {

    private Integer minAge;

    private Integer maxAge;

    private Long stageId;

    public SubjectAgeRange() {
    }

    public SubjectAgeRange(Integer minAge, Integer maxAge, Long stageId) {
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.stageId = stageId;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
    }
}
