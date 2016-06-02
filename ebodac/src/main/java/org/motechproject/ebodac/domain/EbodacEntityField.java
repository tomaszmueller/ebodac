package org.motechproject.ebodac.domain;

import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.util.SecurityMode;

import static org.motechproject.ebodac.constants.EbodacConstants.EMAIL_REPORTS_TAB_PERMISSION;

@Access(value = SecurityMode.PERMISSIONS, members = { EMAIL_REPORTS_TAB_PERMISSION })
@Entity
public class EbodacEntityField {

    @Field
    private Long id;

    @Field(required = true)
    private Long fieldId;

    @Field(required = true)
    private String name;

    @Field
    private String displayName;

    @Field
    private boolean relationField;

    @Field
    private String relatedFieldDisplayName;

    @Field(required = true)
    private String fieldPath;

    public EbodacEntityField() {
    }

    public EbodacEntityField(Long fieldId, String name, String displayName) {
        this.fieldId = fieldId;
        this.name = name;
        this.displayName = displayName;
        this.fieldPath = name;
        this.relationField = false;
    }

    public EbodacEntityField(Long fieldId, String name, String displayName, String relatedFieldName, String relatedFieldDisplayName) {
        this.fieldId = fieldId;
        this.name = name;
        this.displayName = displayName;
        this.relatedFieldDisplayName = relatedFieldDisplayName;
        this.fieldPath = name + "." + relatedFieldName;
        this.relationField = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isRelationField() {
        return relationField;
    }

    public void setRelationField(boolean relationField) {
        this.relationField = relationField;
    }

    public String getRelatedFieldDisplayName() {
        return relatedFieldDisplayName;
    }

    public void setRelatedFieldDisplayName(String relatedFieldDisplayName) {
        this.relatedFieldDisplayName = relatedFieldDisplayName;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }
}
