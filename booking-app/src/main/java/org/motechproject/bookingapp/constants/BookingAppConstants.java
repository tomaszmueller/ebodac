package org.motechproject.bookingapp.constants;

import org.motechproject.ebodac.domain.VisitType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class BookingAppConstants {

    public static final String UI_CONFIG = "custom-ui.js";

    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";

    public static final String PDF_EXPORT_FORMAT = "pdf";
    public static final String CSV_EXPORT_FORMAT = "csv";
    public static final String XLS_EXPORT_FORMAT = "xls";

    public static final int MAX_TIME_HOUR = 24;
    public static final int TIME_OF_THE_VISIT = 1;

    public static final int EARLIEST_DATE = 1;
    public static final int EARLIEST_DATE_IF_FEMALE_CHILD_BEARING_AGE = 14;
    public static final int LATEST_DATE = 28;

    public static final String SCREENING_NAME = "Screening";
    public static final String PRIME_VACCINATION_SCHEDULE_NAME = "PrimeVaccinationSchedule";
    public static final String UNSCHEDULED_VISITS_NAME = "Unscheduled_Visits";
    public static final String VISIT_RESCHEDULE_NAME = "VisitReschedule";

    public static final Set<VisitType> AVAILABLE_VISIT_TYPES_FOR_RESCHEDULE_SCREEN = new HashSet<>(Arrays.asList(VisitType.BOOST_VACCINATION_DAY,
            VisitType.PRIME_VACCINATION_FOLLOW_UP_VISIT, VisitType.BOOST_VACCINATION_FIRST_FOLLOW_UP_VISIT,
            VisitType.BOOST_VACCINATION_SECOND_FOLLOW_UP_VISIT, VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT,
            VisitType.FIRST_LONG_TERM_FOLLOW_UP_VISIT, VisitType.SECOND_LONG_TERM_FOLLOW_UP_VISIT, VisitType.THIRD_LONG_TERM_FOLLOW_UP_VISIT));

    public static final String SCREENING_TAB_PERMISSION = "screeningBookingTab";
    public static final String PRIME_VAC_TAB_PERMISSION = "primeVaccinationBookingTab";
    public static final String CLINIC_VISIT_SCHEDULE_TAB_PERMISSION = "clinicVisitBookingTab";
    public static final String VISIT_RESCHEDULE_TAB_PERMISSION = "visitRescheduleBookingTab";
    public static final String ADVANCED_SETTINGS_TAB_PERMISSION = "bookingAdvancedSettings";
    public static final String UNSCHEDULED_VISITS_TAB_PERMISSION = "unscheduledVisitsTab";

    public static final String HAS_SCREENING_TAB_ROLE = "hasRole('" + SCREENING_TAB_PERMISSION + "')";
    public static final String HAS_PRIME_VAC_TAB_ROLE = "hasRole('" + PRIME_VAC_TAB_PERMISSION + "')";
    public static final String HAS_CLINIC_VISIT_SCHEDULE_TAB_ROLE = "hasRole('" + CLINIC_VISIT_SCHEDULE_TAB_PERMISSION + "')";
    public static final String HAS_VISIT_RESCHEDULE_TAB_ROLE = "hasRole('" + VISIT_RESCHEDULE_TAB_PERMISSION + "')";
    public static final String HAS_ADVANCED_SETTINGS_TAB_ROLE = "hasRole('" + ADVANCED_SETTINGS_TAB_PERMISSION + "')";
    public static final String HAS_UNSCHEDULED_VISITS_TAB_ROLE = "hasRole('" + UNSCHEDULED_VISITS_TAB_PERMISSION + "')";

    public static final Map<String, String> SCREENING_FIELDS_MAP = new LinkedHashMap<String, String>() {
        {
            put("Booking Id",       "volunteer.id");
            put("Location",         "clinic.location");
            put("Screening Date",   "date");
            put("Start Time",       "startTime");
        }
    };

    public static final Map<String, String> PRIME_VACCINATION_SCHEDULE_FIELDS_MAP = new LinkedHashMap<String, String>() {
        {
            put("Location",                 "location");
            put("Participant Id",           "participantId");
            put("Participant Name",         "participantName");
            put("Female Child Bearing Age", "femaleChildBearingAge");
            put("Actual Screening Date",    "actualScreeningDate");
            put("Prime Vac. Date",          "date");
            put("Start Time",               "startTime");
        }
    };

    public static final Map<String, String> VISIT_RESCHEDULE_FIELDS_MAP = new LinkedHashMap<String, String>() {
        {
            put("Location",         "location");
            put("Participant Id",   "participantId");
            put("Participant Name", "participantName");
            put("Visit Type",       "visitType");
            put("Actual Date",      "actualDate");
            put("Planned Date",     "plannedDate");
            put("Start Time",       "startTime");
        }
    };

    public static final Map<String, String> UNSCHEDULED_VISIT_FIELDS_MAP = new LinkedHashMap<String, String>() {
        {
            put("Participant Id",          "participantId");
            put("Location",                "clinicName");
            put("Date",                    "date");
            put("Start Time",              "startTime");
            put("Purpose of the visit",    "purpose");
        }
    };

    public static final List<String> AVAILABLE_LOOKUPS_FOR_PRIME_VACCINATION_SCHEDULE = new ArrayList<>(Arrays.asList(
            "Find By Participant Id", "Find By Participant Name"));

    public static final List<String> AVAILABLE_LOOKUPS_FOR_SCREENINGS = new ArrayList<>(Arrays.asList(
            "Find By Clinic Location", "Find By Volunteer Name", "Find By Booking Id"));

    public static final List<String> AVAILABLE_LOOKUPS_FOR_VISIT_RESCHEDULE = new ArrayList<>(Arrays.asList("Find By Participant Id",
            "Find By Participant Name", "Find By Visit Type", "Find By Clinic Location", "Find By Visit Actual Date",
            "Find By Visit Actual Date Range", "Find By Visit Planned Date", "Find By Visit Planned Date Range"));

    public static final List<String> AVAILABLE_LOOKUPS_FOR_UNSCHEDULED = new ArrayList<>(Arrays.asList(
            "Find By Participant Id", "Find By Clinic Location"));

    private BookingAppConstants() {
    }
}
