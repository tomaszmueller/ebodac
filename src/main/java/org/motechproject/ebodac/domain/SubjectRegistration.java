package org.motechproject.ebodac.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Column;
import java.util.Objects;

/**
 * Models data for registration of Subject in EBODAC
 */
@Entity
public class SubjectRegistration {

    @Column(length = 20)
    @Field
    private String phoneNumber;

    @Field
    private String firstName;

    @Field(required = true)
    private String lastName;

    @Column(length = 3)
    @Field(required = true)
    private Integer age;

    @Field
    private String address;

    @Column(length = 20)
    @Field(required = true)
    private Language language;

    @Column(length = 20)
    @Field(required = true)
    private PhoneType phoneType;

    public SubjectRegistration() {
    }


    public SubjectRegistration(String phoneNumber, String firstName, String lastName, Integer age, String address, Language language, PhoneType phoneType) {
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.address = address;
        this.language = language;
        this.phoneType = phoneType;
    }

    public SubjectRegistration(String firstName, String lastName) {
        this.setFirstName(firstName);
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public PhoneType getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(PhoneType phoneType) {
        this.phoneType = phoneType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPhoneNumber(), getFirstName(), lastName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final SubjectRegistration other = (SubjectRegistration) obj;

        return Objects.equals(this.getFirstName(), other.getFirstName()) && Objects.equals(this.lastName, other.lastName) &&
                Objects.equals(this.getPhoneNumber(), other.getPhoneNumber());
    }

    @Override
    public String toString() {
        return String.format("SubjectRegistration{firstName='%s', lastName='%s', phoneNumber='%s'}",
                getFirstName(), lastName, getPhoneNumber());
    }
}