package org.motechproject.bookingapp.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class Room {

    @Field(required = true)
    private String number;

    @Field(defaultValue = "1")
    private Integer maxPatients;

}
