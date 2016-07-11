package org.motechproject.ebodac.domain.enums;

import org.joda.time.LocalDate;
import org.motechproject.commons.api.Range;

public enum DateFilter {

    YESTERDAY {
        @Override
        public Range<LocalDate> getRange() {
            LocalDate startDate = LocalDate.now().minusDays(1);
            return new Range<>(startDate, startDate);
        }
    },

    LAST_7_DAYS {
        @Override
        public Range<LocalDate> getRange() {
            LocalDate endDate = LocalDate.now().minusDays(1);
            LocalDate startDate = endDate.minusDays(6); //NO CHECKSTYLE MagicNumber
            return new Range<>(startDate, endDate);
        }
    },

    LAST_WEEK {
        @Override
        public Range<LocalDate> getRange() {
            LocalDate endDate = LocalDate.now().withDayOfWeek(1).minusDays(1);
            LocalDate startDate = endDate.minusDays(6); //NO CHECKSTYLE MagicNumber
            return new Range<>(startDate, endDate);
        }
    },

    LAST_30_DAYS {
        @Override
        public Range<LocalDate> getRange() {
            LocalDate endDate = LocalDate.now().minusDays(1);
            LocalDate startDate = endDate.minusDays(30); //NO CHECKSTYLE MagicNumber
            return new Range<>(startDate, endDate);
        }
    },

    LAST_MONTH {
        @Override
        public Range<LocalDate> getRange() {
            LocalDate endDate = LocalDate.now().withDayOfMonth(1).minusDays(1);
            LocalDate startDate = endDate.withDayOfMonth(1);
            return new Range<>(startDate, endDate);
        }
    },

    DATE_RANGE {
        @Override
        public Range<LocalDate> getRange() {
            return null;
        }
    };

    public abstract Range<LocalDate> getRange();

}
