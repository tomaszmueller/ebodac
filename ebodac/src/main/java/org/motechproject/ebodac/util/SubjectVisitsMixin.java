package org.motechproject.ebodac.util;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.motechproject.ebodac.domain.Visit;

import java.util.List;

public abstract class SubjectVisitsMixin {

    @JsonSerialize(using = CustomVisitListSerializer.class)
    public abstract List<Visit> getVisits();
}
