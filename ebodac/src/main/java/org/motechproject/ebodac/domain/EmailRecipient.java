package org.motechproject.ebodac.domain;

import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.util.SecurityMode;

import javax.jdo.annotations.Unique;

import static org.motechproject.ebodac.constants.EbodacConstants.EMAIL_REPORTS_TAB_PERMISSION;

@Access(value = SecurityMode.PERMISSIONS, members = { EMAIL_REPORTS_TAB_PERMISSION })
@Entity
public class EmailRecipient {

    @Field
    private Long id;

    @Field
    private String name;

    @Unique
    @Field(required = true)
    private String emailAddress;

    public EmailRecipient() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
