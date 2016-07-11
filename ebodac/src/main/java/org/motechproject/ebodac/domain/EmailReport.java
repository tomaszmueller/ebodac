package org.motechproject.ebodac.domain;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.motechproject.commons.date.model.Time;
import org.motechproject.ebodac.domain.enums.DayOfWeek;
import org.motechproject.ebodac.domain.enums.EmailReportStatus;
import org.motechproject.ebodac.domain.enums.EmailSchedulePeriod;
import org.motechproject.ebodac.util.json.serializer.CustomTimeSerializer;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.util.SecurityMode;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.ebodac.constants.EbodacConstants.EMAIL_REPORTS_TAB_PERMISSION;

@Access(value = SecurityMode.PERMISSIONS, members = { EMAIL_REPORTS_TAB_PERMISSION })
@Entity
public class EmailReport {

    @Field
    private Long id;

    @Field(required = true)
    private String name;

    @Field
    private String subject;

    @Field
    private String messageContent;

    @Field
    private List<EmailRecipient> recipients;

    @Field(required = true)
    @Cascade(delete = true)
    private EbodacEntity entity;

    @Field(required = true)
    private EmailSchedulePeriod schedulePeriod;

    @Field(required = true)
    private Time scheduleTime;

    @Field
    private DayOfWeek dayOfWeek;

    @Field
    private EmailReportStatus status;

    public EmailReport() {
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public List<EmailRecipient> getRecipients() {
        if (recipients == null) {
            recipients = new ArrayList<>();
        }
        return recipients;
    }

    public void setRecipients(List<EmailRecipient> recipients) {
        this.recipients = recipients;
    }

    public EbodacEntity getEntity() {
        return entity;
    }

    public void setEntity(EbodacEntity entity) {
        this.entity = entity;
    }

    public EmailSchedulePeriod getSchedulePeriod() {
        return schedulePeriod;
    }

    public void setSchedulePeriod(EmailSchedulePeriod schedulePeriod) {
        this.schedulePeriod = schedulePeriod;
    }

    @JsonSerialize(using = CustomTimeSerializer.class)
    public Time getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Time scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public EmailReportStatus getStatus() {
        return status;
    }

    public void setStatus(EmailReportStatus status) {
        this.status = status;
    }
}
