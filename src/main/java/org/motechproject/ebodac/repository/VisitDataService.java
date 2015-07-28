package org.motechproject.ebodac.repository;

import org.joda.time.LocalDate;
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
    List<Visit> findVisitByType(@LookupField(name = "type") VisitType visitType, QueryParams queryParams);

    @Lookup
    List<Visit> findVisitByType(@LookupField(name = "type") VisitType visitType);

    long countFindVisitByType(@LookupField(name = "type") VisitType visitType);
}
