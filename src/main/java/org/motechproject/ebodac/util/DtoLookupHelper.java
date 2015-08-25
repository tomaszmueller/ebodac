package org.motechproject.ebodac.util;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.exception.EbodacLookupException;
import org.motechproject.ebodac.web.domain.GridSettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DtoLookupHelper {

    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static GridSettings changeLookupForFollowupsAfterPrimeInjectionReport(GridSettings settings) {
        String fields = settings.getFields();
        if(StringUtils.isBlank(settings.getFields())) {
            settings.setFields("{}");
            fields = "{}";
        }
        if(StringUtils.isNotBlank(settings.getLookup()) && settings.getLookup().equals("Find Visit By Participant Address")) {
            String address = getAddressFromLookupFields(settings.getFields());
            if(StringUtils.isNotBlank(address) && !address.equals("null")) {
                settings.setFields(fields.substring(0, fields.length() - 1) + ", \"type\":\"PRIME_VACCINATION_FOLLOW_UP_VISIT\"}");
                settings.setLookup(settings.getLookup() + " And Type");
                return settings;
            } else {
                return null;
            }
        }
        if(fields.length() > 2) {
            settings.setFields(fields.substring(0, fields.length() - 1) + ", \"type\":\"PRIME_VACCINATION_FOLLOW_UP_VISIT\", \"subject.address\":\"\"}");
        } else {
            settings.setFields(fields.substring(0, fields.length() - 1) + "\"type\":\"PRIME_VACCINATION_FOLLOW_UP_VISIT\", \"subject.address\":\"\"}");
        }
        if(StringUtils.isBlank(settings.getLookup())) {
            settings.setLookup("Find Visit By Type And Address");
        } else {
            settings.setLookup(settings.getLookup() + " Type And Address");
        }
        return settings;
    }

    public static GridSettings changeLookupAndOrderForFollowupsMissedClinicVisitsReport(GridSettings settings) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        if (StringUtils.isNotBlank(settings.getSortColumn())) {
            String sortColumn = settings.getSortColumn();
            if (sortColumn.equals("planedVisitDate") || sortColumn.equals("noOfDaysExceededVisit")) {
                settings.setSortColumn("motechProjectedDate");
                if(settings.getSortDirection().equals("asc")) {
                    settings.setSortDirection("desc");
                } else {
                    settings.setSortDirection("asc");
                }
            }
        }
        if(StringUtils.isBlank(settings.getFields())) {
            settings.setFields("{}");
        }
        if(StringUtils.isBlank(settings.getLookup())) {
            settings.setLookup("Find Visit By Planned Visit Date Less");
            String fields = settings.getFields();
            String now = LocalDate.now().toString(formatter);
            settings.setFields(fields.substring(0, fields.length() - 1) + "\"motechProjectedDate\":\"" + now + "\" , \"date\":\"\"}");
        } else {
            if (settings.getLookup().equals("Find Visit By Planned Visit Date") || settings.getLookup().equals("Find Visit By Planned Visit Date And Type")) {
                LocalDate date = getLocalDateFromLookupFields(settings.getFields(), "motechProjectedDate");
                if (date == null) {
                    return null;
                }
                if (date.isAfter(LocalDate.now())) {
                    return null;
                }
                String fields = settings.getFields();
                settings.setFields(fields.substring(0, fields.length() - 1) + ", \"date\":\"\"}");
                String newLookupName = settings.getLookup() + " Eq";
                settings.setLookup(newLookupName);
            } else if (settings.getLookup().equals("Find Visits By Planned Visit Date Range") || settings.getLookup().equals("Find Visits By Planned Visit Date Range And Type")) {
                Range<LocalDate> dateRange = getDateRangeFromLookupFields(settings.getFields(), "motechProjectedDate");
                if (dateRange == null) {
                    return null;
                }
                if (dateRange.getMax().isAfter(LocalDate.now()) && dateRange.getMin().isAfter(LocalDate.now())) {
                    return null;
                } else if (dateRange.getMax().isAfter(LocalDate.now()) && dateRange.getMin().isBefore(LocalDate.now())) {
                    settings.setFields(setNewMaxDateInRangeFields(settings.getFields(), "motechProjectedDate", LocalDate.now()));
                }
                String fields = settings.getFields();
                settings.setFields(fields.substring(0, fields.length() - 1) + ", \"date\":\"\"}");
                String newLookupName = settings.getLookup() + " Eq";
                settings.setLookup(newLookupName);
            } else {
                String fields = settings.getFields();
                String now = LocalDate.now().toString(formatter);
                settings.setFields(fields.substring(0, fields.length() - 1) + ", \"motechProjectedDate\":\"" + now + "\" , \"date\":\"\"}");
                String newLookupName = settings.getLookup() + " Less";
                settings.setLookup(newLookupName);
            }
        }
        return settings;
    }

    private static Object getObjectFromLookupFields(String lookupFields, String fieldName) {
        Map<String, Object> fieldsMap;
        try {
            fieldsMap = getFields(lookupFields);
        } catch (IOException e) {
            throw new EbodacLookupException("Invalid lookup params", e);
        }
        return fieldsMap.get(fieldName);
    }

    private static LocalDate getLocalDateFromLookupFields(String lookupFields, String dateName) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        return formatter.parseLocalDate((String) getObjectFromLookupFields(lookupFields, dateName));
    }

    private static String getAddressFromLookupFields(String lookupFields) {
        return (String)getObjectFromLookupFields(lookupFields,"subject.address");
    }

    private static Range<LocalDate> getDateRangeFromLookupFields(String lookupFields, String dateName) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        Map<String, String> rangeMap = (Map < String, String>)getObjectFromLookupFields(lookupFields, dateName);
        LocalDate min = formatter.parseLocalDate(rangeMap.get("min"));
        LocalDate max = formatter.parseLocalDate(rangeMap.get("max"));
        if(max.isBefore(min)) {
            return null;
        }
        return new Range<>(min, max);
    }

    private static String setNewMaxDateInRangeFields(String lookupFields, String dateName, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        Map<String, Object> fieldsMap;
        try {
            fieldsMap = getFields(lookupFields);
        } catch (IOException e) {
            throw new EbodacLookupException("Invalid lookup params",e);
        }
        Map<String, String> rangeMap = (Map < String, String>)fieldsMap.get(dateName);
        rangeMap.remove("max");
        rangeMap.put("max", date.toString(formatter));

        try {
            return OBJECT_MAPPER.writeValueAsString(fieldsMap);
        } catch (IOException e) {
            throw new EbodacLookupException("Invalid lookup params", e);
        }
    }

    private static Map<String, Object> getFields(String lookupFields) throws IOException {
        return OBJECT_MAPPER.readValue(lookupFields, new TypeReference<HashMap>() {});
    }
}
