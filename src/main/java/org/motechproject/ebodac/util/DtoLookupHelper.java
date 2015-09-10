package org.motechproject.ebodac.util;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.exception.EbodacLookupException;
import org.motechproject.ebodac.web.domain.GridSettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DtoLookupHelper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static GridSettings changeLookupForFollowupsAfterPrimeInjectionReport(GridSettings settings) throws IOException {
        Map<String,String> fieldsMap = new HashMap<>();

        if (StringUtils.isBlank(settings.getFields())) {
            settings.setFields("{}");
        }
        if (StringUtils.isNotBlank(settings.getLookup()) && settings.getLookup().equals("Find Visits By Participant Address")) {
            String address = getAddressFromLookupFields(settings.getFields());
            if (StringUtils.isNotBlank(address) && !address.equals("null")) {
                fieldsMap.put(Visit.SUBJECT_ADDRESS_PROPERTY_NAME, address);
                settings.setLookup(settings.getLookup() + " Phone Number And Type");
            } else {
                return null;
            }
        } else if (StringUtils.isBlank(settings.getLookup())) {
            fieldsMap.put(Visit.SUBJECT_ADDRESS_PROPERTY_NAME, null);
            settings.setLookup("Find Visits By Type Phone Number And Address");
        } else {
            String fields = settings.getFields();
            fieldsMap = OBJECT_MAPPER.readValue(fields, new TypeReference<HashMap>() {});
            fieldsMap.put(Visit.SUBJECT_ADDRESS_PROPERTY_NAME, null);
            settings.setLookup(settings.getLookup() + " Type Phone Number And Address");
        }

        fieldsMap.put(Visit.VISIT_TYPE_PROPERTY_NAME, VisitType.PRIME_VACCINATION_FOLLOW_UP_VISIT.toString());
        fieldsMap.put(Visit.SUBJECT_PHONE_NUMBER_PROPERTY_NAME, null);
        settings.setFields(OBJECT_MAPPER.writeValueAsString(fieldsMap));
        return settings;
    }

    public static GridSettings changeLookupAndOrderForFollowupsMissedClinicVisitsReport(GridSettings settings) throws IOException {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        Map<String,String> fieldsMap = new HashMap<>();

        if (StringUtils.isNotBlank(settings.getSortColumn())) {
            String sortColumn = settings.getSortColumn();
            if (sortColumn.equals("planedVisitDate") || sortColumn.equals("noOfDaysExceededVisit")) {
                settings.setSortColumn(Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME);
                if (sortColumn.equals("noOfDaysExceededVisit")) {
                    if (settings.getSortDirection().equals("asc")) {
                        settings.setSortDirection("desc");
                    } else {
                        settings.setSortDirection("asc");
                    }
                }
            }
        }
        if(StringUtils.isBlank(settings.getFields())) {
            settings.setFields("{}");
        }
        if(StringUtils.isBlank(settings.getLookup())) {
            settings.setLookup("Find Visits By Planned Date Less And Actual Date Eq And Subject Phone Number Eq");
            fieldsMap.put(Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME, LocalDate.now().toString(formatter));
            fieldsMap.put(Visit.ACTUAL_VISIT_DATE_PROPERTY_NAME, null);
            settings.setFields(OBJECT_MAPPER.writeValueAsString(fieldsMap));
        } else {
            switch (settings.getLookup()) {
                case "Find Visits By Planned Visit Date":
                case "Find Visits By Planned Visit Date And Type": {
                    LocalDate date = getLocalDateFromLookupFields(settings.getFields(),
                            Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME);
                    if (date == null) {
                        return null;
                    }
                    if (date.isAfter(LocalDate.now())) {
                        return null;
                    }
                    String fields = settings.getFields();
                    fieldsMap = OBJECT_MAPPER.readValue(fields, new TypeReference<HashMap>() {});
                    fieldsMap.put(Visit.ACTUAL_VISIT_DATE_PROPERTY_NAME, null);

                    String newLookupName = settings.getLookup() + " Eq";
                    settings.setLookup(newLookupName);
                    break;
                }
                case "Find Visits By Planned Visit Date Range":
                case "Find Visits By Planned Visit Date Range And Type": {
                    Range<LocalDate> dateRange = getDateRangeFromLookupFields(settings.getFields(),
                            Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME);
                    if (dateRange == null) {
                        return null;
                    }
                    if (dateRange.getMax().isAfter(LocalDate.now()) && dateRange.getMin().isAfter(LocalDate.now())) {
                        return null;
                    } else if (dateRange.getMax().isAfter(LocalDate.now()) && dateRange.getMin().isBefore(LocalDate.now())) {
                        settings.setFields(setNewMaxDateInRangeFields(settings.getFields(), Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME, LocalDate.now()));
                    }
                    String fields = settings.getFields();
                    fieldsMap = OBJECT_MAPPER.readValue(fields, new TypeReference<HashMap>() {});
                    fieldsMap.put(Visit.ACTUAL_VISIT_DATE_PROPERTY_NAME, null);

                    String newLookupName = settings.getLookup() + " Eq";
                    settings.setLookup(newLookupName);
                    break;
                }
                default: {
                    String fields = settings.getFields();
                    fieldsMap = OBJECT_MAPPER.readValue(fields, new TypeReference<HashMap>() {});
                    fieldsMap.put(Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME, LocalDate.now().toString(formatter));
                    fieldsMap.put(Visit.ACTUAL_VISIT_DATE_PROPERTY_NAME, null);

                    String newLookupName = settings.getLookup() + " Less";
                    settings.setLookup(newLookupName);
                    break;
                }
            }
        }
        fieldsMap.put(Visit.SUBJECT_PHONE_NUMBER_PROPERTY_NAME, null);
        settings.setFields(OBJECT_MAPPER.writeValueAsString(fieldsMap));
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
        LocalDate min = null;
        LocalDate max = null;
        Map<String, String> rangeMap = (Map < String, String>)getObjectFromLookupFields(lookupFields, dateName);
        if (StringUtils.isNotBlank(rangeMap.get("min"))) {
            min = formatter.parseLocalDate(rangeMap.get("min"));
        }
        if (StringUtils.isNotBlank(rangeMap.get("max"))) {
            max = formatter.parseLocalDate(rangeMap.get("max"));
        }
        if(max != null && min != null && max.isBefore(min)) {
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
