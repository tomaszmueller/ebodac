package org.motechproject.ebodac.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class VotoMessage {

    @Field
    private String votoIvrId;

    @Field
    private String votoSmsId;

    @Field
    private String messageKey;

    public String getVotoIvrId() {
        return votoIvrId;
    }

    public void setVotoIvrId(String votoIvrId) {
        this.votoIvrId = votoIvrId;
    }

    public String getVotoSmsId() {
        return votoSmsId;
    }

    public void setVotoSmsId(String votoSmsId) {
        this.votoSmsId = votoSmsId;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
}
