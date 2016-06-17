package org.motechproject.ebodac.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.enums.Gender;
import org.motechproject.ebodac.domain.enums.Language;
import org.motechproject.ebodac.util.json.serializer.CustomDateDeserializer;
import org.motechproject.ebodac.util.json.serializer.CustomDateSerializer;
import org.motechproject.ebodac.util.json.serializer.CustomDateTimeDeserializer;
import org.motechproject.ebodac.util.json.serializer.CustomDateTimeSerializer;
import org.motechproject.ebodac.util.json.serializer.CustomVisitListDeserializer;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.mds.annotations.NonEditable;
import org.motechproject.mds.annotations.ReadAccess;
import org.motechproject.mds.annotations.UIDisplayable;
import org.motechproject.mds.util.SecurityMode;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Unique;
import java.util.ArrayList;
import java.util.List;

/**
 * Models data for registration of Subject in EBODAC
 */
@ReadAccess(value = SecurityMode.PERMISSIONS, members = { "manageEbodac" })
@Access(value = SecurityMode.PERMISSIONS, members = { "manageSubjects" })
@Entity(recordHistory = true, name = "Participant")
public class Subject {

    /**
     * Fields captured in ZETES
     */

    private Long id;

    @Unique
    @NonEditable
    @UIDisplayable(position = 0)
    @Field(required = true, displayName = EbodacConstants.SUBJECT_ID_FIELD_DISPLAY_NAME)
    private String subjectId;

    @UIDisplayable(position = 1)
    @Field
    private String name;

    @UIDisplayable(position = 2)
    @Field
    private String householdName;

    @UIDisplayable(position = 3)
    @Field
    private String headOfHousehold;

    @UIDisplayable(position = 4)
    @Field
    private String phoneNumber;

    @UIDisplayable(position = 5)
    @Field
    private String address;

    @UIDisplayable(position = 7)
    @Column(length = 20)
    @Field
    private Language language;

    @NonEditable
    @UIDisplayable(position = 8)
    @Field
    private String siteId;

    @NonEditable
    @UIDisplayable(position = 9)
    @Field(required = true)
    private String siteName;

    @UIDisplayable(position = 10)
    @Field
    private String community;

    @NonEditable(display = false)
    @Field
    private String chiefdom;

    @NonEditable(display = false)
    @Field
    private String section;

    @NonEditable(display = false)
    @Field
    private String district;

    /**
     * Fields captured in RAVE
     */

    @NonEditable
    @UIDisplayable(position = 6)
    @Field
    private Gender gender;

    @NonEditable
    @Field
    private Long stageId;

    @NonEditable
    @Field
    private LocalDate dateOfBirth;

    @NonEditable
    @Field
    private LocalDate primerVaccinationDate;

    @NonEditable
    @Field
    private LocalDate boosterVaccinationDate;

    @NonEditable
    @Field(displayName = "Date of Discontinuation Vac.")
    private LocalDate dateOfDisconVac;

    @NonEditable
    @Field(displayName = "Withdrawal Date")
    private LocalDate dateOfDisconStd;

    /**
     * Motech internal fields
     */
    @Field(defaultValue = "false")
    private boolean changed;

    @NonEditable(display = false)
    @Field
    private String owner;

    @Field
    private DateTime creationDate;

    @Field
    private DateTime modificationDate;

    @NonEditable
    @Field
    @Persistent(mappedBy = "subject")
    @Cascade(delete = true)
    private List<Visit> visits = new ArrayList<>();

    public Subject() {
    }

    public Subject(String subjectId, String name, String householdName, String headOfHousehold, //NO CHECKSTYLE ParameterNumber
                   String phoneNumber, String address, Language language, String community, String siteId, String siteName,
                   String chiefdom, String section, String district) {
        this.subjectId = subjectId;
        this.name = name;
        this.householdName = householdName;
        this.headOfHousehold = headOfHousehold;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.language = language;
        this.community = community;
        this.chiefdom = chiefdom;
        this.section = section;
        this.district = district;
        this.siteId = siteId;
        this.siteName = siteName;
    }


    public Subject(Subject subject) {
        subjectId = subject.getSubjectId();
        name = subject.getName();
        householdName = subject.getHouseholdName();
        headOfHousehold = subject.getHeadOfHousehold();
        phoneNumber = subject.getPhoneNumber();
        address = subject.getAddress();
        language = subject.getLanguage();
        community = subject.getCommunity();
        chiefdom = subject.getChiefdom();
        section = subject.getSection();
        district = subject.getDistrict();
        siteId = subject.getSiteId();
        siteName = subject.getSiteName();
        gender = subject.getGender();
        stageId = subject.getStageId();
        dateOfBirth = subject.getDateOfBirth();
        primerVaccinationDate = subject.getPrimerVaccinationDate();
        boosterVaccinationDate = subject.getBoosterVaccinationDate();
        dateOfDisconStd = subject.getDateOfDisconStd();
        dateOfDisconVac = subject.getDateOfDisconVac();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (StringUtils.isEmpty(phoneNumber)) {
            this.phoneNumber = null;
        } else {
            this.phoneNumber = phoneNumber;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHouseholdName() {
        return householdName;
    }

    public void setHouseholdName(String householdName) {
        this.householdName = householdName;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (StringUtils.isBlank(address)) {
            this.address = null;
        } else {
            this.address = address;
        }
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getHeadOfHousehold() {
        return headOfHousehold;
    }

    public void setHeadOfHousehold(String headOfHousehold) {
        this.headOfHousehold = headOfHousehold;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getChiefdom() {
        return chiefdom;
    }

    public void setChiefdom(String chiefdom) {
        this.chiefdom = chiefdom;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getPrimerVaccinationDate() {
        return primerVaccinationDate;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setPrimerVaccinationDate(LocalDate primerVaccinationDate) {
        this.primerVaccinationDate = primerVaccinationDate;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getBoosterVaccinationDate() {
        return boosterVaccinationDate;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setBoosterVaccinationDate(LocalDate boosterVaccinationDate) {
        this.boosterVaccinationDate = boosterVaccinationDate;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getDateOfDisconVac() {
        return dateOfDisconVac;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setDateOfDisconVac(LocalDate dateOfDisconVac) {
        this.dateOfDisconVac = dateOfDisconVac;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getDateOfDisconStd() {
        return dateOfDisconStd;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setDateOfDisconStd(LocalDate dateOfDisconStd) {
        this.dateOfDisconStd = dateOfDisconStd;
    }

    public Boolean getChanged() {
        return changed;
    }

    public void setChanged(Boolean changed) {
        this.changed = changed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    public DateTime getCreationDate() {
        return creationDate;
    }

    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    public DateTime getModificationDate() {
        return modificationDate;
    }

    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    public void setModificationDate(DateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public List<Visit> getVisits() {
        return visits;
    }

    @JsonDeserialize(using = CustomVisitListDeserializer.class)
    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }

    @Ignore
    public String getLanguageCode() {
        if (language != null) {
            return language.getCode();
        } else {
            return null;
        }

    }

    public void setLanguageCode(String languageCode) {
        //this setter is needed, because json deserialization doesn't work properly without it
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Subject subject = (Subject) o;

        if (!subjectId.equals(subject.subjectId)) {
            return false;
        }
        if (!equalsForZetes(subject)) {
            return false;
        }
        if (!equalsForRave(subject)) {
            return false;
        }
        if (visits != null ? !visits.equals(subject.visits) : subject.visits != null) {
            return false;
        }

        return true;
    }

    public boolean equalsForZetes(Subject subject) { //NO CHECKSTYLE CyclomaticComplexity
        if (language != subject.language) {
            return false;
        }
        if (phoneNumber != null ? !phoneNumber.equals(subject.phoneNumber) : subject.phoneNumber != null) {
            return false;
        }
        if (name != null ? !name.equals(subject.name) : subject.name != null) {
            return false;
        }
        if (address != null ? !address.equals(subject.address) : subject.address != null) {
            return false;
        }
        if (community != null ? !community.equals(subject.community) : subject.community != null) {
            return false;
        }
        if (headOfHousehold != null ? !headOfHousehold.equals(subject.headOfHousehold) : subject.headOfHousehold != null) {
            return false;
        }
        if (householdName != null ? !householdName.equals(subject.householdName) : subject.householdName != null) {
            return false;
        }
        if (siteId != null ? !siteId.equals(subject.siteId) : subject.siteId != null) {
            return false;
        }
        if (chiefdom != null ? !chiefdom.equals(subject.chiefdom) : subject.chiefdom != null) {
            return false;
        }
        if (district != null ? !district.equals(subject.district) : subject.district != null) {
            return false;
        }
        if (section != null ? !section.equals(subject.section) : subject.section != null) {
            return false;
        }
        if (siteName != null ? !siteName.equals(subject.siteName) : subject.siteName != null) {
            return false;
        }

        return true;
    }

    public boolean equalsForRave(Subject subject) { //NO CHECKSTYLE CyclomaticComplexity
        if (primerVaccinationDate != null ? !primerVaccinationDate.equals(subject.primerVaccinationDate) : subject.primerVaccinationDate != null) {
            return false;
        }
        if (boosterVaccinationDate != null ? !boosterVaccinationDate.equals(subject.boosterVaccinationDate) : subject.boosterVaccinationDate != null) {
            return false;
        }
        if (dateOfBirth != null ? !dateOfBirth.equals(subject.dateOfBirth) : subject.dateOfBirth != null) {
            return false;
        }
        if (gender != subject.gender) {
            return false;
        }
        if (dateOfDisconStd != null ? !dateOfDisconStd.equals(subject.dateOfDisconStd) : subject.dateOfDisconStd != null) {
            return false;
        }
        if (dateOfDisconVac != null ? !dateOfDisconVac.equals(subject.dateOfDisconVac) : subject.dateOfDisconVac != null) {
            return false;
        }
        if (stageId != null ? !stageId.equals(subject.stageId) : subject.stageId != null) {
            return false;
        }

        return true;
    }

    @Override //NO CHECKSTYLE CyclomaticComplexity
    public int hashCode() {
        int result = subjectId.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (householdName != null ? householdName.hashCode() : 0);
        result = 31 * result + (headOfHousehold != null ? headOfHousehold.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (siteId != null ? siteId.hashCode() : 0);
        result = 31 * result + (siteName != null ? siteName.hashCode() : 0);
        result = 31 * result + (community != null ? community.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (stageId != null ? stageId.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        result = 31 * result + (primerVaccinationDate != null ? primerVaccinationDate.hashCode() : 0);
        result = 31 * result + (boosterVaccinationDate != null ? boosterVaccinationDate.hashCode() : 0);
        result = 31 * result + (dateOfDisconVac != null ? dateOfDisconVac.hashCode() : 0);
        result = 31 * result + (dateOfDisconStd != null ? dateOfDisconStd.hashCode() : 0);
        result = 31 * result + (visits != null ? visits.hashCode() : 0);
        result = 31 * result + (chiefdom != null ? chiefdom.hashCode() : 0);
        result = 31 * result + (section != null ? section.hashCode() : 0);
        result = 31 * result + (district != null ? district.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return subjectId;
    }
}
