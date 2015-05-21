package org.motechproject.ebodac.util;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonView;

// View definitions:
class Views {
    static class Zetes { }
}

abstract class SubjectMixin {
    
    @JsonView(Views.Zetes.class)
    String subjectId;

    @JsonView(Views.Zetes.class)
    String name;

    @JsonView(Views.Zetes.class)
    String householdName;

    @JsonView(Views.Zetes.class)
    String headOfHousehold;

    @JsonView(Views.Zetes.class)
    String phoneNumber;

    @JsonView(Views.Zetes.class)
    String address;

    @JsonProperty("language")
    @JsonView(Views.Zetes.class)
    abstract String getLanguageCode();

    @JsonView(Views.Zetes.class)
    String community;
}
