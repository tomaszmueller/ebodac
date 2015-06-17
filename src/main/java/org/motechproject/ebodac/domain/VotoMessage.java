package org.motechproject.ebodac.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class VotoMessage {

    @Field
    private Long votoIvrId;

    @Field
    private Long votoSmsId;

    @Field
    private String messageKey;

    public Long getVotoIvrId() {
        return votoIvrId;
    }

    public void setVotoIvrId(Long votoIvrId) {
        this.votoIvrId = votoIvrId;
    }

    public Long getVotoSmsId() {
        return votoSmsId;
    }

    public void setVotoSmsId(Long votoSmsId) {
        this.votoSmsId = votoSmsId;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
}
