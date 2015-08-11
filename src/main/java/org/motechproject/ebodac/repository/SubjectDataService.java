package org.motechproject.ebodac.repository;

import org.joda.time.LocalDate;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;

import java.util.List;

/**
 * Interface for repository that persists simple records and allows CRUD.
 * MotechDataService base class will provide the implementation of this class as well
 * as methods for adding, deleting, saving and finding all instances.  In this class we
 * define and custom lookups we may need.
 */
public interface SubjectDataService extends MotechDataService<Subject> {

    @Lookup
    List<Subject> findSubjectsByName(@LookupField(name = "name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name);

    @Lookup(name="Find unique Subject by SubjectId")
    Subject findSubjectBySubjectId(@LookupField(name = "subjectId") String subjectId);

    @Lookup
    List<Subject> findSubjectsByModified(@LookupField(name = "changed") Boolean modified);

    @Lookup
    List<Subject> findSubjectsByPrimerVaccinationDateRange(@LookupField(name = "primerVaccinationDate")
                                                      Range<LocalDate> dateRange);

    @Lookup
    List<Subject> findSubjectsByBoosterVaccinationDateRange(@LookupField(name = "boosterVaccinationDate")
                                                       Range<LocalDate> dateRange);

    @Lookup
    List<Subject> findSubjectsByPrimerVaccinationDate(@LookupField(name = "primerVaccinationDate") LocalDate dateRange);

    @Lookup
    List<Subject> findSubjectsByBoosterVaccinationDate(@LookupField(name = "boosterVaccinationDate") LocalDate dateRange);

    @Lookup
    List<Subject> findSubjectsByAddress(@LookupField(name = "address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address);

    @Lookup(name="Find Subjects by SubjectId")
    List<Subject> findSubjectsByMatchesCaseInsensitiveSubjectId(@LookupField(name = "subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId);
}
