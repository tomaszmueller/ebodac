package org.motechproject.ebodac.repository;

import org.joda.time.LocalDate;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;

/**
 * Interface for repository that persists simple records and allows CRUD.
 * MotechDataService base class will provide the implementation of this class as well
 * as methods for adding, deleting, saving and finding all instances.  In this class we
 * define and custom lookups we may need.
 */
public interface VisitDataService extends MotechDataService<Visit> {

    @Lookup
    List<Visit> findVisitByDate(@LookupField(name = "date") LocalDate date);

    @Lookup
    List<Visit> findVisitByDate(@LookupField(name = "date") LocalDate date, QueryParams queryParams);

    long countFindVisitByDate(@LookupField(name = "date") LocalDate date);

    @Lookup
    List<Visit> findVisitByDateAndType(@LookupField(name = "date") LocalDate date,
                                       @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitByDateAndType(@LookupField(name = "date") LocalDate date,
                                       @LookupField(name = "type") VisitType visitType, QueryParams queryParams);

    long countFindVisitByDateAndType(@LookupField(name = "date") LocalDate date,
                                     @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitsByDateRange(@LookupField(name = "date") Range<LocalDate> dateRange);

    @Lookup
    List<Visit> findVisitsByDateRange(@LookupField(name = "date") Range<LocalDate> dateRange, QueryParams queryParams);

    long countFindVisitsByDateRange(@LookupField(name = "date") Range<LocalDate> dateRange);

    @Lookup
    List<Visit> findVisitsByDateRangeAndType(@LookupField(name = "date") Range<LocalDate> dateRange,
                                             @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitsByDateRangeAndType(@LookupField(name = "date") Range<LocalDate> dateRange,
                                             @LookupField(name = "type") VisitType visitType, QueryParams queryParams);

    long countFindVisitsByDateRangeAndType(@LookupField(name = "date") Range<LocalDate> dateRange,
                                           @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitByType(@LookupField(name = "type") VisitType visitType, QueryParams queryParams);

    @Lookup
    List<Visit> findVisitByType(@LookupField(name = "type") VisitType visitType);

    long countFindVisitByType(@LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitBySubjectId(@LookupField(name = "subject.subjectId") String subjectId, QueryParams queryParams);

    @Lookup
    List<Visit> findVisitBySubjectId(@LookupField(name = "subject.subjectId") String subjectId);

    long countFindVisitBySubjectId(@LookupField(name = "subject.subjectId") String subjectId);

    @Lookup
    List<Visit> findVisitBySubjectName(@LookupField(name = "subject.name") String name, QueryParams queryParams);

    @Lookup
    List<Visit> findVisitBySubjectName(@LookupField(name = "subject.name") String name);

    long countFindVisitBySubjectName(@LookupField(name = "subject.name") String name);

    @Lookup
    List<Visit> findVisitBySubjectAddress(@LookupField(name = "subject.address") String address, QueryParams queryParams);

    @Lookup
    List<Visit> findVisitBySubjectAddress(@LookupField(name = "subject.address") String address);

    long countFindVisitBySubjectAddress(@LookupField(name = "subject.address") String address);
}
