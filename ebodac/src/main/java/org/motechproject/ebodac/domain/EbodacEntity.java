package org.motechproject.ebodac.domain;

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
public class EbodacEntity {

    @Field
    private Long id;

    @Field(required = true)
    private Long entityId;

    @Field(required = true)
    private String name;

    @Field(required = true)
    private String className;

    @Field
    @Cascade(delete = true)
    private List<EbodacEntityField> fields;

    public EbodacEntity() {
    }

    public EbodacEntity(Long entityId, String name, String className) {
        this.entityId = entityId;
        this.name = name;
        this.className = className;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<EbodacEntityField> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return fields;
    }

    public void setFields(List<EbodacEntityField> fields) {
        this.fields = fields;
    }
}
