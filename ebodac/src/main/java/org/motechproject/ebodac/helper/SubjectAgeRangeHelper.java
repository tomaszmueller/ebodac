package org.motechproject.ebodac.helper;

import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.SubjectAgeRange;

import java.util.List;

public final class SubjectAgeRangeHelper {
    private static final String AGE = "-age:";

    private SubjectAgeRangeHelper() {
    }

    public static String getAgeRangeMessageCode(LocalDate dateOfBirth, LocalDate referenceDate,
                                                Long stageId, List<SubjectAgeRange> subjectAgeRangeList) {
        SubjectAgeRange ageRange = getSubjectAgeRange(dateOfBirth, referenceDate, stageId, subjectAgeRangeList);

        if (ageRange != null && (ageRange.getMinAge() != null || ageRange.getMaxAge() != null)) {
            return AGE + (ageRange.getMinAge() == null ? "" : ageRange.getMinAge())
                    + "-" + (ageRange.getMaxAge() == null ? "" : ageRange.getMaxAge());
        }

        return "";
    }

    public static Range<LocalDate> calculateDateOfBirthRange(LocalDate dateOfBirth, LocalDate referenceDate,
                                                             Long stageId, List<SubjectAgeRange> subjectAgeRangeList) {
        Range<LocalDate> dateOfBirthRange = new Range<>(null, null);
        SubjectAgeRange subjectAgeRange = getSubjectAgeRange(dateOfBirth, referenceDate, stageId, subjectAgeRangeList);

        if (subjectAgeRange != null) {
            LocalDate minDate = subjectAgeRange.getMaxAge() == null ? null : referenceDate.minusYears(subjectAgeRange.getMaxAge() + 1).plusDays(1);
            LocalDate maxDate = subjectAgeRange.getMinAge() == null ? null : referenceDate.minusYears(subjectAgeRange.getMinAge());

            dateOfBirthRange = new Range<>(minDate, maxDate);
        }

        return dateOfBirthRange;
    }

    private static SubjectAgeRange getSubjectAgeRange(LocalDate dateOfBirth, LocalDate referenceDate,
                                                      Long stageId, List<SubjectAgeRange> subjectAgeRangeList) {
        if (dateOfBirth == null || !dateOfBirth.isBefore(referenceDate)) {
            return null;
        }

        if (stageId == null) {
            return null;
        }

        Integer subjectAge = Years.yearsBetween(dateOfBirth, referenceDate).getYears();

        for (SubjectAgeRange ageRange : subjectAgeRangeList) {
            if (stageId.equals(ageRange.getStageId())
                    && (ageRange.getMinAge() != null ? subjectAge >= ageRange.getMinAge() : ageRange.getMaxAge() != null)
                    && (ageRange.getMaxAge() != null ? subjectAge <= ageRange.getMaxAge() : ageRange.getMinAge() != null)) {
                return ageRange;
            }
        }

        return null;
    }
}
