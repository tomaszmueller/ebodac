package org.motechproject.bookingapp.helper;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.LocalDate;
import org.motechproject.bookingapp.constants.BookingAppConstants;
import org.motechproject.bookingapp.domain.DateFilter;
import org.motechproject.bookingapp.domain.Screening;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class DtoLookupHelper {

    private static final String NOT_BLANK_REGEX = ".";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private DtoLookupHelper() {
    }

    public static BookingGridSettings changeLookupForScreeningAndUnscheduled(BookingGridSettings settings) throws IOException {
        Map<String, Object> fieldsMap = new HashMap<>();
        DateFilter dateFilter = settings.getDateFilter();

        if (dateFilter != null) {

            if (StringUtils.isBlank(settings.getFields())) {
                settings.setFields("{}");
            }

            if (StringUtils.isBlank(settings.getLookup())) {
                settings.setLookup("Find By Date");
            } else {
                fieldsMap = getFields(settings.getFields());
                settings.setLookup(settings.getLookup() + " And Date");
            }

            Map<String, String> rangeMap = getDateRangeFromFilter(settings);

            fieldsMap.put(Screening.DATE_PROPERTY_NAME, rangeMap);
            settings.setFields(OBJECT_MAPPER.writeValueAsString(fieldsMap));
        }
        return settings;
    }

    public static BookingGridSettings changeLookupForPrimeVaccinationSchedule(BookingGridSettings settings) throws IOException {
        Map<String, Object> fieldsMap = new HashMap<>();

        if (StringUtils.isBlank(settings.getFields())) {
            settings.setFields("{}");
        }

        if (StringUtils.isBlank(settings.getLookup())) {
            settings.setLookup("Find By Participant Name Prime Vaccination Date And Visit Type And Booking Planned Date");
            fieldsMap.put(VisitBookingDetails.SUBJECT_NAME_PROPERTY_NAME, NOT_BLANK_REGEX);
        } else {
            fieldsMap = getFields(settings.getFields());
            if ("Find By Participant Name".equals(settings.getLookup())) {
                settings.setLookup(settings.getLookup() + " Prime Vaccination Date And Visit Type And Booking Planned Date");
            } else {
                settings.setLookup(settings.getLookup() + " Visit Type And Participant Prime Vaccination Date And Name And Booking Planned Date");
                fieldsMap.put(Visit.SUBJECT_NAME_PROPERTY_NAME, NOT_BLANK_REGEX);
            }
        }

        Map<String, String> rangeMap = getDateRangeFromFilter(settings);

        if (rangeMap != null && (StringUtils.isNotBlank(rangeMap.get("min")) || StringUtils.isNotBlank(rangeMap.get("max")))) {
            settings.setLookup(settings.getLookup() + " Range");
            fieldsMap.put(VisitBookingDetails.BOOKING_PLANNED_DATE_PROPERTY_NAME, rangeMap);
        } else {
            fieldsMap.put(VisitBookingDetails.BOOKING_PLANNED_DATE_PROPERTY_NAME, null);
        }

        fieldsMap.put(VisitBookingDetails.VISIT_TYPE_PROPERTY_NAME, VisitType.PRIME_VACCINATION_DAY.toString());
        fieldsMap.put(VisitBookingDetails.SUBJECT_PRIME_VACCINATION_DATE_PROPERTY_NAME, null);
        settings.setFields(OBJECT_MAPPER.writeValueAsString(fieldsMap));
        return settings;
    }

    public static BookingGridSettings changeLookupForVisitReschedule(BookingGridSettings settings) throws IOException {  //NO CHECKSTYLE CyclomaticComplexity
        Map<String, Object> fieldsMap = new HashMap<>();

        if (StringUtils.isBlank(settings.getFields())) {
            settings.setFields("{}");
        }

        if (StringUtils.isBlank(settings.getLookup())) {
            settings.setLookup("Find By Planned Date And Visit Type Set");
            fieldsMap.put(VisitBookingDetails.VISIT_PLANNED_DATE_PROPERTY_NAME, null);
            fieldsMap.put(VisitBookingDetails.VISIT_TYPE_PROPERTY_NAME, BookingAppConstants.AVAILABLE_VISIT_TYPES_FOR_RESCHEDULE_SCREEN);
        } else {
            fieldsMap = getFields(settings.getFields());
            switch (settings.getLookup()) {
                case "Find By Visit Type":
                    String type = (String) fieldsMap.get(VisitBookingDetails.VISIT_TYPE_PROPERTY_NAME);
                    if (StringUtils.isBlank(type) || !BookingAppConstants.AVAILABLE_VISIT_TYPES_FOR_RESCHEDULE_SCREEN.contains(VisitType.valueOf(type))) {
                        fieldsMap.put(VisitBookingDetails.VISIT_TYPE_PROPERTY_NAME, null);
                    }
                    fieldsMap.put(VisitBookingDetails.VISIT_PLANNED_DATE_PROPERTY_NAME, null);
                    settings.setLookup(settings.getLookup() + " And Planned Date");
                    break;
                case "Find By Visit Planned Date":
                    String plannedDate = (String) fieldsMap.get(VisitBookingDetails.VISIT_PLANNED_DATE_PROPERTY_NAME);
                    if (StringUtils.isBlank(plannedDate)) {
                        settings.setLookup("Find By Planned Date And Visit Type Set");
                        fieldsMap.put(VisitBookingDetails.VISIT_PLANNED_DATE_PROPERTY_NAME, null);
                    } else {
                        settings.setLookup(settings.getLookup() + " And Visit Type Set");
                    }
                    fieldsMap.put(VisitBookingDetails.VISIT_TYPE_PROPERTY_NAME, BookingAppConstants.AVAILABLE_VISIT_TYPES_FOR_RESCHEDULE_SCREEN);
                    break;
                case "Find By Visit Planned Date Range":
                    Map<String, String> dateRange = (Map<String, String>) fieldsMap.get(VisitBookingDetails.VISIT_PLANNED_DATE_PROPERTY_NAME);
                    if (dateRange == null || (StringUtils.isBlank(dateRange.get("min")) && StringUtils.isBlank(dateRange.get("max")))) {
                        settings.setLookup("Find By Planned Date And Visit Type Set");
                        fieldsMap.put(VisitBookingDetails.VISIT_PLANNED_DATE_PROPERTY_NAME, null);
                    } else {
                        settings.setLookup(settings.getLookup() + " And Visit Type Set");
                    }
                    fieldsMap.put(VisitBookingDetails.VISIT_TYPE_PROPERTY_NAME, BookingAppConstants.AVAILABLE_VISIT_TYPES_FOR_RESCHEDULE_SCREEN);
                    break;
                default:
                    fieldsMap.put(VisitBookingDetails.VISIT_PLANNED_DATE_PROPERTY_NAME, null);
                    fieldsMap.put(VisitBookingDetails.VISIT_TYPE_PROPERTY_NAME, BookingAppConstants.AVAILABLE_VISIT_TYPES_FOR_RESCHEDULE_SCREEN);
                    settings.setLookup(settings.getLookup() + " And Planned Date And Visit Type Set");
                    break;
            }
        }

        settings.setFields(OBJECT_MAPPER.writeValueAsString(fieldsMap));
        return settings;
    }

    private static Map<String, String> getDateRangeFromFilter(BookingGridSettings settings) {
        DateFilter dateFilter = settings.getDateFilter();

        if (dateFilter == null) {
            return null;
        }

        Map<String, String> rangeMap = new HashMap<>();

        if (DateFilter.DATE_RANGE.equals(dateFilter)) {
            rangeMap.put("min", settings.getStartDate());
            rangeMap.put("max", settings.getEndDate());
        } else {
            Range<LocalDate> dateRange = dateFilter.getRange();
            rangeMap.put("min", dateRange.getMin().toString(BookingAppConstants.SIMPLE_DATE_FORMAT));
            rangeMap.put("max", dateRange.getMax().toString(BookingAppConstants.SIMPLE_DATE_FORMAT));
        }

        return rangeMap;
    }

    private static Map<String, Object> getFields(String lookupFields) throws IOException {
        return OBJECT_MAPPER.readValue(lookupFields, new TypeReference<HashMap>() {}); //NO CHECKSTYLE WhitespaceAround
    }

    private static Map<String, String> getFieldsMap(String lookupFields) throws IOException {
        return OBJECT_MAPPER.readValue(lookupFields, new TypeReference<HashMap>() {}); //NO CHECKSTYLE WhitespaceAround
    }
}
