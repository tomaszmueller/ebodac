package org.motechproject.ebodac.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import java.util.Objects;

/**
 * Models data for registration of Subject in EBODAC
 */
@Entity
public class SubjectRegistration {

    @Field
    private String firstName;

    @Field
    private String message;

    public SubjectRegistration() {
    }


    public SubjectRegistration(String firstName, String message) {
        this.firstName = firstName;
        this.message = message;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, message);
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

        return Objects.equals(this.firstName, other.firstName) && Objects.equals(this.message, other.message);
    }

    @Override
    public String toString() {
        return String.format("SubjectRegistration{firstName='%s', message='%s'}", firstName, message);
    }
}