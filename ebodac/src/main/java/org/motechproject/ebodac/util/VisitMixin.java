package org.motechproject.ebodac.util;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.motechproject.ebodac.domain.Subject;

public abstract class VisitMixin {

    @JsonSerialize(using = CustomVisitSubjectSerializer.class)
    public abstract Subject getSubject();
}
