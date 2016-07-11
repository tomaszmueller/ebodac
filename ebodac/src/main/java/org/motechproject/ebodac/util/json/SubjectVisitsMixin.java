package org.motechproject.ebodac.util.json;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.util.json.serializer.CustomVisitListSerializer;

import java.util.List;

public abstract class SubjectVisitsMixin {

    @JsonSerialize(using = CustomVisitListSerializer.class)
    public abstract List<Visit> getVisits();
}
