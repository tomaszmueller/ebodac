package org.motechproject.ebodac.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import java.io.IOException;

/**
 * Deserializer for DateTime representation in UI
 */
public class CustomDateDeserializer extends JsonDeserializer<DateTime> {

    private static final DateTimeParser[] PARSERS = {
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").getParser(),
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss Z").getParser(),
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z").getParser(),
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss ZZ").getParser(),
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm ZZ").getParser()
    };

    private static final DateTimeFormatter FORMATTER =
            new DateTimeFormatterBuilder().append(null, PARSERS).toFormatter();

    @Override
    public DateTime deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {

        DateTime date = null;
        String dateString = parser.getText();

        if (NumberUtils.isNumber(dateString)) {
            date = new DateTime(Long.parseLong(dateString));
        } else if (StringUtils.isNotBlank(dateString)) {
            date = FORMATTER.parseDateTime(dateString);
        }

        return date;
    }

}
