package org.motechproject.ebodac.repository;

import org.joda.time.LocalDate;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;

import java.util.List;

/**
 * Interface for repository that persists simple records and allows CRUD.
 * MotechDataService base class will provide the implementation of this class as well
 * as methods for adding, deleting, saving and finding all instances.  In this class we
 * define and custom lookups we may need.
 */
public interface VisitDataService extends MotechDataService<Visit> {

    @Lookup
    List<Visit> findVisitByActualDate(@LookupField(name = "date") LocalDate date);

    @Lookup
    List<Visit> findVisitByActualDate(@LookupField(name = "date") LocalDate date, QueryParams queryParams);

    long countFindVisitByActualDate(@LookupField(name = "date") LocalDate date);

    @Lookup
    List<Visit> findVisitByActualDateAndType(@LookupField(name = "date") LocalDate date,
                                             @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitByActualDateAndType(@LookupField(name = "date") LocalDate date,
                                             @LookupField(name = "type") VisitType visitType, QueryParams queryParams);

    long countFindVisitByActualDateAndType(@LookupField(name = "date") LocalDate date,
                                           @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitsByActualDateRange(@LookupField(name = "date") Range<LocalDate> dateRange);

    @Lookup
    List<Visit> findVisitsByActualDateRange(@LookupField(name = "date") Range<LocalDate> dateRange, QueryParams queryParams);

    long countFindVisitsByActualDateRange(@LookupField(name = "date") Range<LocalDate> dateRange);

    @Lookup
    List<Visit> findVisitsByActualDateRangeAndType(@LookupField(name = "date") Range<LocalDate> dateRange,
                                                   @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitsByActualDateRangeAndType(@LookupField(name = "date") Range<LocalDate> dateRange,
                                                   @LookupField(name = "type") VisitType visitType, QueryParams queryParams);

    long countFindVisitsByActualDateRangeAndType(@LookupField(name = "date") Range<LocalDate> dateRange,
                                                 @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitByPlannedDate(@LookupField(name = "motechProjectedDate") LocalDate date);

    @Lookup
    List<Visit> findVisitByPlannedDate(@LookupField(name = "motechProjectedDate") LocalDate date, QueryParams queryParams);

    long countFindVisitByPlannedDate(@LookupField(name = "motechProjectedDate") LocalDate date);

    @Lookup
    List<Visit> findVisitByPlannedDateAndType(@LookupField(name = "motechProjectedDate") LocalDate date,
                                             @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitByPlannedDateAndType(@LookupField(name = "motechProjectedDate") LocalDate date,
                                             @LookupField(name = "type") VisitType visitType, QueryParams queryParams);

    long countFindVisitByPlannedDateAndType(@LookupField(name = "motechProjectedDate") LocalDate date,
                                           @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitsByPlannedDateRange(@LookupField(name = "motechProjectedDate") Range<LocalDate> dateRange);

    @Lookup
    List<Visit> findVisitsByPlannedDateRange(@LookupField(name = "motechProjectedDate") Range<LocalDate> dateRange, QueryParams queryParams);

    long countFindVisitsByPlannedDateRange(@LookupField(name = "motechProjectedDate") Range<LocalDate> dateRange);

    @Lookup
    List<Visit> findVisitsByPlannedDateRangeAndType(@LookupField(name = "motechProjectedDate") Range<LocalDate> dateRange,
                                                   @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitsByPlannedDateRangeAndType(@LookupField(name = "motechProjectedDate") Range<LocalDate> dateRange,
                                                   @LookupField(name = "type") VisitType visitType, QueryParams queryParams);

    long countFindVisitsByPlannedDateRangeAndType(@LookupField(name = "motechProjectedDate") Range<LocalDate> dateRange,
                                                 @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitByType(@LookupField(name = "type") VisitType visitType, QueryParams queryParams);

    @Lookup
    List<Visit> findVisitByType(@LookupField(name = "type") VisitType visitType);

    long countFindVisitByType(@LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitBySubjectId(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId, QueryParams queryParams);

    @Lookup
    List<Visit> findVisitBySubjectId(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId);

    long countFindVisitBySubjectId(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId);

    @Lookup
    List<Visit> findVisitBySubjectName(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name, QueryParams queryParams);

    @Lookup
    List<Visit> findVisitBySubjectName(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name);

    long countFindVisitBySubjectName(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name);
    
    @Lookup
    List<Visit> findVisitBySubjectAddress(@LookupField(name = "subject.address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address, QueryParams queryParams);

    @Lookup
    List<Visit> findVisitBySubjectAddress(@LookupField(name = "subject.address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address);

    long countFindVisitBySubjectAddress(@LookupField(name = "subject.address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address);
}
