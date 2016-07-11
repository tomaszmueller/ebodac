package org.motechproject.ebodac.repository;

import org.joda.time.LocalDate;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.enums.EnrollmentStatus;
import org.motechproject.ebodac.domain.enums.Gender;
import org.motechproject.ebodac.domain.SubjectEnrollments;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;

import java.util.List;

public interface SubjectEnrollmentsDataService extends MotechDataService<SubjectEnrollments> {

    @Lookup(name = "Find unique By Participant Id")
    SubjectEnrollments findBySubjectId(@LookupField(name = "subject.subjectId") String subjectId);

    @Lookup(name = "Find By Participant Id")
    List<SubjectEnrollments> findByMatchesCaseInsensitiveSubjectId(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId);

    @Lookup(name = "Find By Participant Name")
    List<SubjectEnrollments> findBySubjectName(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name);

    @Lookup(name = "Find By Participant Gender")
    List<SubjectEnrollments> findBySubjectGender(@LookupField(name = "subject.gender") Gender gender);

    @Lookup
    List<SubjectEnrollments> findByDateOfUnenrollment(@LookupField(name = "dateOfUnenrollment") Range<LocalDate> dateOfUnenrollment);

    @Lookup
    List<SubjectEnrollments> findByDateOfUnenrollmentAndSiteName(@LookupField(name = "dateOfUnenrollment") Range<LocalDate> dateOfUnenrollment,
                                                                @LookupField(name = "subject.siteName", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String siteName);

    @Lookup
    List<SubjectEnrollments> findByStatus(@LookupField(name = "status") EnrollmentStatus status);

    @Lookup(name = "Find By Participant Id And Status")
    List<SubjectEnrollments> findBySubjectIdAndStatus(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId,
                                                      @LookupField(name = "status") EnrollmentStatus status);

    @Lookup(name = "Find By Participant Gender And Status")
    List<SubjectEnrollments> findBySubjectGenderAndStatus(@LookupField(name = "subject.gender") Gender gender,
                                                          @LookupField(name = "status") EnrollmentStatus status);

    @Lookup
    List<SubjectEnrollments> findByDateOfUnenrollmentAndStatus(@LookupField(name = "dateOfUnenrollment") Range<LocalDate> dateOfUnenrollment,
                                                               @LookupField(name = "status") EnrollmentStatus status);

    @Lookup(name = "Find By Participant Date Of Birth Range And Status")
    List<SubjectEnrollments> findBySubjectDateOfBirthRangeAndStatus(@LookupField(name = "subject.dateOfBirth") Range<LocalDate> dateOfBirth,
                                                                    @LookupField(name = "status") EnrollmentStatus status);

    @Lookup
    List<SubjectEnrollments> findByDateOfUnenrollmentAndSiteNameAndStatus(@LookupField(name = "dateOfUnenrollment") Range<LocalDate> dateOfUnenrollment,
                                                                          @LookupField(name = "subject.siteName", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String siteName,
                                                                          @LookupField(name = "status") EnrollmentStatus status);
}
