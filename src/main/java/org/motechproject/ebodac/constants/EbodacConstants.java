package org.motechproject.ebodac.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class EbodacConstants {

    public static final String UI_CONFIG = "custom-ui.js";

    public static final String SUBJECT_ID_FIELD_NAME = "subjectId";

    public static final String SITE_ID_FOR_STAGE_I = "B05-SL10001";

    public static final String ZETES_UPDATE_EVENT = "zetes_update";
    public static final String EMAIL_CHECK_EVENT = "email_check";

    public static final String ZETES_URL = "zetes_url";
    public static final String ZETES_USERNAME = "zetes_username";
    public static final String ZETES_PASSWORD = "zetes_password";
    public static final String START_TIME = "start_time";

    public static final Pattern CSV_FILENAME_PATTERN = Pattern.compile(".*_(.*_.*)\\.csv");
    public static final String CSV_DATE_FORMAT = "yyyyMMdd_HHmmss";

    public static final String FETCHED_EMAIL_FLAG = "fetched";
    public static final String JOB_SUCCESS_STATUS = "COMPLETION";
    public static final String JOB_FAILURE_STATUS = "FAILURE";

    public static final String REPORT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String REPORT_START_DATE_FORMAT = "yyyy-MM-ddHH:mm";
    public static final String DAILY_REPORT_EVENT = "daily_report_event";
    public static final String DAILY_REPORT_EVENT_START_DATE = "daily_report_event_start_date";
    public static final String DAILY_REPORT_EVENT_START_HOUR = "00:01";

    public static final String MIDPOINT_MESSAGE = "Mid-point message";

    public static final String PDF_EXPORT_FORMAT="pdf";
    public static final String CSV_EXPORT_FORMAT="csv";

    public static final List<String> DAYS_OF_WEEK = new ArrayList<>(Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"));

    public static final List<String> AVAILABLE_CAMPAIGNS = new ArrayList<>(Arrays.asList("Screening", "Prime Vaccination Day",
            "Mid-point message", "Prime Vaccination Follow-up visit", "Boost Vaccination Day", "Boost Vaccination First Follow-up visit",
            "Boost Vaccination Second Follow-up visit", "Boost Vaccination Third Follow-up visit", "First Long-term Follow-up visit",
            "Second Long-term Follow-up visit", "Third Long-term Follow-up visit"));

    public static final Map<String, String> DAILY_CLINIC_VISIT_SCHEDULE_REPORT_MAP = new HashMap<String, String>() {
        {
            put("Date",         "date");
            put("Subject ID",   "subjectId");
            put("Name",         "name");
            put("Phone Number", "phoneNumber");
            put("Address",      "address");
            put("Visit type",   "type");
        }
    };
}
