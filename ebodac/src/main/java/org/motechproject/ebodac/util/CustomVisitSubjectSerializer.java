package org.motechproject.ebodac.util;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.motechproject.ebodac.domain.Subject;

import java.io.IOException;

public class CustomVisitSubjectSerializer extends JsonSerializer<Subject> {

    @Override
    public void serialize(Subject subject, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (subject != null) {
            jsonGenerator.writeString(subject.getSubjectId());
        }
    }
}
