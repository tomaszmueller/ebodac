package org.motechproject.ebodac.util.json;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.util.json.serializer.CustomVisitSubjectSerializer;

public abstract class VisitMixin {

    @JsonSerialize(using = CustomVisitSubjectSerializer.class)
    public abstract Subject getSubject();
}
