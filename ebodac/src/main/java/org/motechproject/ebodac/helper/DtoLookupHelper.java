package org.motechproject.ebodac.helper;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.enums.EnrollmentStatus;
import org.motechproject.ebodac.domain.SubjectEnrollments;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.enums.VisitType;
import org.motechproject.ebodac.exception.EbodacLookupException;
import org.motechproject.ebodac.web.domain.GridSettings;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.LookupFieldType;
import org.motechproject.mds.dto.SettingDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class DtoLookupHelper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private DtoLookupHelper() {
    }

    public static GridSettings changeLookupForFollowupsAfterPrimeInjectionReport(GridSettings settings) throws IOException {
        Map<String, Object> fieldsMap = new HashMap<>();

        if (StringUtils.isBlank(settings.getFields())) {
            settings.setFields("{}");
        }
        if (StringUtils.isNotBlank(settings.getLookup()) && "Find By Participant Address".equals(settings.getLookup())) {
            String address = getAddressFromLookupFields(settings.getFields());
            if (StringUtils.isNotBlank(address) && !"null".equals(address)) {
                fieldsMap.put(Visit.SUBJECT_ADDRESS_PROPERTY_NAME, address);
                settings.setLookup(settings.getLookup() + " Phone Number And Type");
            } else {
                return null;
            }
        } else if (StringUtils.isBlank(settings.getLookup())) {
            fieldsMap.put(Visit.SUBJECT_ADDRESS_PROPERTY_NAME, null);
            settings.setLookup("Find By Type Phone Number And Address");
        } else {
            fieldsMap = getFields(settings.getFields());
            fieldsMap.put(Visit.SUBJECT_ADDRESS_PROPERTY_NAME, null);
            settings.setLookup(settings.getLookup() + " Type Phone Number And Address");
        }

        fieldsMap.put(Visit.VISIT_TYPE_PROPERTY_NAME, new HashSet<>(Collections.singletonList(VisitType.PRIME_VACCINATION_FIRST_FOLLOW_UP_VISIT.toString())));
        fieldsMap.put(Visit.SUBJECT_PHONE_NUMBER_PROPERTY_NAME, null);
        settings.setFields(OBJECT_MAPPER.writeValueAsString(fieldsMap));
        return settings;
    }

    public static GridSettings changeLookupForScreeningReport(GridSettings settings) throws IOException {
        Map<String, Object> fieldsMap = new HashMap<>();

        if (StringUtils.isBlank(settings.getFields())) {
            settings.setFields("{}");
        }

        if (StringUtils.isBlank(settings.getLookup())) {
            settings.setLookup("Find By Type");
        } else {
            fieldsMap = getFields(settings.getFields());
            settings.setLookup(settings.getLookup() + " And Type");
        }

        fieldsMap.put(Visit.VISIT_TYPE_PROPERTY_NAME, VisitType.SCREENING.toString());
        settings.setFields(OBJECT_MAPPER.writeValueAsString(fieldsMap));
        return settings;
    }

    public static GridSettings changeLookupAndOrderForFollowupsMissedClinicVisitsReport(GridSettings settings) throws IOException { //NO CHECKSTYLE CyclomaticComplexity
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        Map<String, String> fieldsMap = new HashMap<>();
        String newLookupName;

        changeOrderForFollowupsMissedClinicVisitsReport(settings);
        if (StringUtils.isBlank(settings.getFields())) {
            settings.setFields("{}");
        }
        if (StringUtils.isBlank(settings.getLookup())) {
            settings.setLookup("Find By Planned Date Less And Actual Date Eq And Subject Phone Number Eq");
            fieldsMap.put(Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME, LocalDate.now().toString(formatter));
            fieldsMap.put(Visit.ACTUAL_VISIT_DATE_PROPERTY_NAME, null);
            settings.setFields(OBJECT_MAPPER.writeValueAsString(fieldsMap));
        } else {
            fieldsMap = getFieldsMap(settings.getFields());

            switch (settings.getLookup()) {
                case "Find By Planned Visit Date":
                case "Find By Planned Visit Date And Type":
                    LocalDate date = getLocalDateFromLookupFields(settings.getFields(),
                            Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME);

                    LocalDate maxDate = LocalDate.now().minusDays(1);
                    if (date == null || date.isAfter(maxDate)) {
                        return null;
                    }
                    fieldsMap.put(Visit.ACTUAL_VISIT_DATE_PROPERTY_NAME, null);

                    newLookupName = settings.getLookup() + " Eq";
                    break;
                case "Find By Planned Visit Date Range":
                case "Find By Planned Visit Date Range And Site Name":
                case "Find By Planned Visit Date Range And Type":
                    Range<LocalDate> dateRange = getDateRangeFromLookupFields(settings.getFields(),
                            Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME);
                    if (!checkAndUpdateDateRangeForFollowupsMissedClinicVisitsReport(dateRange, settings)) {
                        return null;
                    }
                    fieldsMap.put(Visit.ACTUAL_VISIT_DATE_PROPERTY_NAME, null);

                    newLookupName = settings.getLookup() + " Eq";
                    break;
                default:
                    fieldsMap.put(Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME, LocalDate.now().toString(formatter));
                    fieldsMap.put(Visit.ACTUAL_VISIT_DATE_PROPERTY_NAME, null);

                    newLookupName = settings.getLookup() + " Less";
                    break;
            }
            settings.setLookup(newLookupName);
        }
        fieldsMap.put(Visit.SUBJECT_PHONE_NUMBER_PROPERTY_NAME, null);
        settings.setFields(OBJECT_MAPPER.writeValueAsString(fieldsMap));
        return settings;
    }

    public static GridSettings changeLookupAndOrderForMandEMissedClinicVisitsReport(GridSettings settings) throws IOException { //NO CHECKSTYLE CyclomaticComplexity
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        Map<String, String> fieldsMap = new HashMap<>();
        String newLookupName;

        changeOrderForFollowupsMissedClinicVisitsReport(settings);
        if (StringUtils.isBlank(settings.getFields())) {
            settings.setFields("{}");
        }
        if (StringUtils.isBlank(settings.getLookup())) {
            settings.setLookup("Find By Planned Visit Date Less And Actual Visit Date");
            fieldsMap.put(Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME, LocalDate.now().toString(formatter));
        } else {
            fieldsMap = getFieldsMap(settings.getFields());

            switch (settings.getLookup()) {
                case "Find By Planned Visit Date":
                case "Find By Planned Visit Date And Type":
                    LocalDate date = getLocalDateFromLookupFields(settings.getFields(),
                            Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME);

                    LocalDate maxDate = LocalDate.now().minusDays(1);
                    if (date == null || date.isAfter(maxDate)) {
                        return null;
                    }

                    newLookupName = settings.getLookup() + " And Actual Visit Date";
                    break;
                case "Find By Planned Visit Date Range":
                case "Find By Planned Visit Date Range And Site Name":
                case "Find By Planned Visit Date Range And Type":
                    Range<LocalDate> dateRange = getDateRangeFromLookupFields(settings.getFields(),
                            Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME);
                    if (!checkAndUpdateDateRangeForFollowupsMissedClinicVisitsReport(dateRange, settings)) {
                        return null;
                    }

                    newLookupName = settings.getLookup() + " And Actual Visit Date";
                    break;
                default:
                    fieldsMap.put(Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME, LocalDate.now().toString(formatter));

                    newLookupName = settings.getLookup() + " And Planned Visit Date And Actual Visit Date";
                    break;
            }
            settings.setLookup(newLookupName);
        }

        fieldsMap.put(Visit.ACTUAL_VISIT_DATE_PROPERTY_NAME, null);
        settings.setFields(OBJECT_MAPPER.writeValueAsString(fieldsMap));
        return settings;
    }

    public static GridSettings changeLookupAndOrderForOptsOutOfMotechMessagesReport(GridSettings settings) throws IOException {
        Map<String, Object> fieldsMap = new HashMap<>();

        if ("age".equals(settings.getSortColumn())) {
            settings.setSortColumn(SubjectEnrollments.SUBJECT_DATE_OF_BIRTH_PROPERTY_NAME);
            if ("asc".equals(settings.getSortDirection())) {
                settings.setSortDirection("desc");
            } else {
                settings.setSortDirection("asc");
            }
        }

        if (StringUtils.isBlank(settings.getFields())) {
            settings.setFields("{}");
        }

        if (StringUtils.isBlank(settings.getLookup())) {
            settings.setLookup("Find By Status");
        } else if ("Find By Participant Age".equals(settings.getLookup())) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
            Range<Long> range = getLongRangeFromLookupFields(settings.getFields(), SubjectEnrollments.SUBJECT_AGE_PROPERTY_NAME);

            if (range == null) {
                return null;
            }

            Long minAge = range.getMin();
            Long maxAge = range.getMax();

            Map<String, String> dateRange = new HashMap<>();

            if (minAge != null) {
                dateRange.put("max", LocalDate.now().minusYears(minAge.intValue()).toString(formatter));
            } else {
                dateRange.put("max", "");
            }

            if (maxAge != null) {
                dateRange.put("min", LocalDate.now().minusYears(maxAge.intValue() + 1).plusDays(1).toString(formatter));
            } else {
                dateRange.put("min", "");
            }

            fieldsMap.put(SubjectEnrollments.SUBJECT_DATE_OF_BIRTH_PROPERTY_NAME, dateRange);
            settings.setLookup("Find By Participant Date Of Birth Range And Status");
        } else {
            fieldsMap = getFields(settings.getFields());

            String newLookupName = settings.getLookup() + " And Status";
            settings.setLookup(newLookupName);
        }

        fieldsMap.put(SubjectEnrollments.STATUS_PROPERTY_NAME, EnrollmentStatus.UNENROLLED.toString());
        settings.setFields(OBJECT_MAPPER.writeValueAsString(fieldsMap));
        return settings;
    }

    public static GridSettings changeLookupAndOrderForIvrAndSmsStatisticReport(GridSettings settings) throws IOException {
        if ("age".equals(settings.getSortColumn())) {
            settings.setSortColumn(SubjectEnrollments.SUBJECT_DATE_OF_BIRTH_PROPERTY_NAME);
            if ("asc".equals(settings.getSortDirection())) {
                settings.setSortDirection("desc");
            } else {
                settings.setSortDirection("asc");
            }
        }

        return settings;
    }

    public static List<LookupDto> addLookupForOptsOutOfMotechMessagesReport(List<LookupDto> lookups) {
        LookupFieldDto lookupField = new LookupFieldDto(null, "subject", LookupFieldType.RANGE, null, false, "age");
        lookupField.setClassName("java.lang.Long");
        lookupField.setDisplayName("Participant");
        lookupField.setRelatedFieldDisplayName("Age");

        List<LookupFieldDto> lookupFields = new ArrayList<>();
        lookupFields.add(lookupField);

        List<String> fieldsOrder = new ArrayList<>();
        fieldsOrder.add("subject.age");

        LookupDto lookup = new LookupDto("Find By Participant Age", false, false, lookupFields, true, "findBySubjectAge", fieldsOrder);
        lookups.add(lookup);

        return lookups;
    }

    private static boolean checkAndUpdateDateRangeForFollowupsMissedClinicVisitsReport(Range<LocalDate> dateRange, GridSettings settings) {
        if (dateRange == null) {
            return false;
        }
        LocalDate maxDate = LocalDate.now().minusDays(1);
        if (dateRange.getMin() != null && dateRange.getMin().isAfter(maxDate)) {
            return false;
        } else if (dateRange.getMax() == null || dateRange.getMax().isAfter(maxDate)) {
            settings.setFields(setNewMaxDateInRangeFields(settings.getFields(), Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME, maxDate));
        }
        return true;
    }

    public static GridSettings changeLookupForDay8AndDay57Report(GridSettings settings) throws IOException {
        Map<String, Object> fieldsMap = new HashMap<>();

        if (StringUtils.isBlank(settings.getFields())) {
            settings.setFields("{}");
        }

        if (StringUtils.isBlank(settings.getLookup())) {
            settings.setLookup("Find By Type Set");
            fieldsMap.put(Visit.VISIT_TYPE_PROPERTY_NAME, new HashSet<>(
                    Arrays.asList(VisitType.PRIME_VACCINATION_FIRST_FOLLOW_UP_VISIT.toString(),
                            VisitType.BOOST_VACCINATION_DAY.toString())
            ));
        } else {
            fieldsMap = getFields(settings.getFields());
            String type = (String) fieldsMap.get(Visit.VISIT_TYPE_PROPERTY_NAME);
            List<String> availableVisitTypes = Arrays.asList(VisitType.PRIME_VACCINATION_FIRST_FOLLOW_UP_VISIT.toString(),
                    VisitType.BOOST_VACCINATION_DAY.toString());
            if (StringUtils.isBlank(type) || !availableVisitTypes.contains(type)) {
                fieldsMap.put(Visit.VISIT_TYPE_PROPERTY_NAME, null);
            }
        }

        settings.setFields(OBJECT_MAPPER.writeValueAsString(fieldsMap));
        return settings;
    }

    public static List<LookupDto> changeLookupFieldsForDay8AndDay57Report(List<LookupDto> lookups) {

        List<String> availableVisitTypes = Arrays.asList(VisitType.PRIME_VACCINATION_FIRST_FOLLOW_UP_VISIT.toString() + ":"  + VisitType.PRIME_VACCINATION_FIRST_FOLLOW_UP_VISIT.getValue(),
                VisitType.BOOST_VACCINATION_DAY.toString() + ":"  + VisitType.BOOST_VACCINATION_DAY.getValue());
        for (LookupDto lookup : lookups) {
            for (LookupFieldDto lookupFieldDto : lookup.getLookupFields()) {

                if ("Visit Type".equals(lookupFieldDto.getDisplayName())) {
                    for (SettingDto settingDto : lookupFieldDto.getSettings()) {
                        if ("mds.form.label.values".equals(settingDto.getName())) {
                            settingDto.setValue(availableVisitTypes);
                            break;
                        }
                    }
                    break;
                }
            }
        }

        return lookups;
    }

    private static void changeOrderForFollowupsMissedClinicVisitsReport(GridSettings settings)
    {
        if (StringUtils.isNotBlank(settings.getSortColumn())) {
            String sortColumn = settings.getSortColumn();
            if ("planedVisitDate".equals(sortColumn) || "noOfDaysExceededVisit".equals(sortColumn)) {
                settings.setSortColumn(Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME);
                if ("noOfDaysExceededVisit".equals(sortColumn)) {
                    if ("asc".equals(settings.getSortDirection())) {
                        settings.setSortDirection("desc");
                    } else {
                        settings.setSortDirection("asc");
                    }
                }
            }
        }
        if (StringUtils.isBlank(settings.getFields())) {
            settings.setFields("{}");
        }
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
        return (String) getObjectFromLookupFields(lookupFields, "subject.address");
    }

    private static Range<LocalDate> getDateRangeFromLookupFields(String lookupFields, String dateName) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        LocalDate min = null;
        LocalDate max = null;

        @SuppressWarnings("unchecked")
        Map<String, String> rangeMap = (Map<String, String>) getObjectFromLookupFields(lookupFields, dateName);
        if (StringUtils.isNotBlank(rangeMap.get("min"))) {
            min = formatter.parseLocalDate(rangeMap.get("min"));
        }
        if (StringUtils.isNotBlank(rangeMap.get("max"))) {
            max = formatter.parseLocalDate(rangeMap.get("max"));
        }
        if (max != null && min != null && max.isBefore(min)) {
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
            throw new EbodacLookupException("Invalid lookup params", e);
        }

        @SuppressWarnings("unchecked")
        Map<String, String> rangeMap = (Map<String, String>) fieldsMap.get(dateName);
        rangeMap.remove("max");
        rangeMap.put("max", date.toString(formatter));

        try {
            return OBJECT_MAPPER.writeValueAsString(fieldsMap);
        } catch (IOException e) {
            throw new EbodacLookupException("Invalid lookup params", e);
        }
    }

    private static Range<Long> getLongRangeFromLookupFields(String lookupFields, String name) {
        Long min = null;
        Long max = null;

        @SuppressWarnings("unchecked")
        Map<String, String> rangeMap = (Map<String, String>) getObjectFromLookupFields(lookupFields, name);

        try {
            if (StringUtils.isNotBlank(rangeMap.get("min"))) {
                min = Long.valueOf(rangeMap.get("min"));
            }
            if (StringUtils.isNotBlank(rangeMap.get("max"))) {
                max = Long.valueOf(rangeMap.get("max"));
            }
        } catch (NumberFormatException e) {
            throw new EbodacLookupException("Invalid lookup params", e);
        }

        return new Range<>(min, max);
    }

    private static Map<String, Object> getFields(String lookupFields) throws IOException {
        return OBJECT_MAPPER.readValue(lookupFields, new TypeReference<HashMap>() {}); //NO CHECKSTYLE WhitespaceAround
    }

    private static Map<String, String> getFieldsMap(String lookupFields) throws IOException {
        return OBJECT_MAPPER.readValue(lookupFields, new TypeReference<HashMap>() {}); //NO CHECKSTYLE WhitespaceAround
    }
}
