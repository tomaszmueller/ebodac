package org.motechproject.ebodac.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class VotoLanguage {

    @Field
    private Long votoId;

    @Field
    private Language language;

    public Long getVotoId() {
        return votoId;
    }

    public void setVotoId(Long votoId) {
        this.votoId = votoId;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
