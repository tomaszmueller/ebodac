package org.motechproject.ebodac.util;

import org.codehaus.jackson.map.annotate.JsonView;
import org.motechproject.ebodac.domain.Language;

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

    @JsonView(Views.Zetes.class)
    Language language;

    @JsonView(Views.Zetes.class)
    String community;
}
