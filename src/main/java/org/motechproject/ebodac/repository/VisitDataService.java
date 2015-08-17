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
    List<Visit> findVisitByDateOfFollowupVisit(@LookupField(name = "date") LocalDate date);

    @Lookup
    List<Visit> findVisitByDateOfFollowupVisit(@LookupField(name = "date") LocalDate date,
                                               QueryParams queryParams);

    long countFindVisitByDateOfFollowupVisit(@LookupField(name = "date") LocalDate date);

    @Lookup
    List<Visit> findVisitByDateOfFollowupVisitTypeAndAddress(@LookupField(name = "date") LocalDate date,
                                                             @LookupField(name = "type") VisitType visitType,
                                                             @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup
    List<Visit> findVisitByDateOfFollowupVisitAndTypeAndAddress(@LookupField(name = "date") LocalDate date,
                                                                @LookupField(name = "type") VisitType visitType,
                                                                @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address,
                                                                QueryParams queryParams);

    long countFindVisitByDateOfFollowupVisitAndTypeAndAddress(@LookupField(name = "date") LocalDate date,
                                                              @LookupField(name = "type") VisitType visitType,
                                                              @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

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
    List<Visit> findVisitsByDateRangeOfFollowupVisit(@LookupField(name = "date") Range<LocalDate> dateRange);

    @Lookup
    List<Visit> findVisitsByDateRangeOfFollowupVisit(@LookupField(name = "date") Range<LocalDate> dateRange, QueryParams queryParams);

    long countFindVisitsByDateRangeOfFollowupVisit(@LookupField(name = "date") Range<LocalDate> dateRange);

    @Lookup
    List<Visit> findVisitsByDateRangeOfFollowupVisitTypeAndAddress(@LookupField(name = "date") Range<LocalDate> dateRange,
                                                                   @LookupField(name = "type") VisitType visitType,
                                                                   @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup
    List<Visit> findVisitsByDateRangeOfFollowupVisitTypeAndAddress(@LookupField(name = "date") Range<LocalDate> dateRange,
                                                                   @LookupField(name = "type") VisitType visitType,
                                                                   @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address,
                                                                   QueryParams queryParams);

    long countFindVisitsByDateRangeOfFollowupVisitTypeAndAddress(@LookupField(name = "date") Range<LocalDate> dateRange,
                                                                 @LookupField(name = "type") VisitType visitType,
                                                                 @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup
    List<Visit> findVisitsByActualDateRangeAndType(@LookupField(name = "date") Range<LocalDate> dateRange,
                                             @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitsByActualDateRangeAndType(@LookupField(name = "date") Range<LocalDate> dateRange,
                                             @LookupField(name = "type") VisitType visitType, QueryParams queryParams);

    long countFindVisitsByActualDateRangeAndType(@LookupField(name = "date") Range<LocalDate> dateRange,
                                           @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitByType(@LookupField(name = "type") VisitType visitType, QueryParams queryParams);

    @Lookup
    List<Visit> findVisitByType(@LookupField(name = "type") VisitType visitType);

    long countFindVisitByType(@LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitByTypeAndAddress(@LookupField(name = "type") VisitType visitType,
                                          @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address,
                                          QueryParams queryParams);

    @Lookup
    List<Visit> findVisitByTypeAndAddress(@LookupField(name = "type") VisitType visitType,
                                          @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    long countFindVisitByTypeAndAddress(@LookupField(name = "type") VisitType visitType,
                                        @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find Visit By Participant Id")
    List<Visit> findVisitBySubjectId(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId, QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Id")
    List<Visit> findVisitBySubjectId(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId);

    long countFindVisitBySubjectId(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId);

    @Lookup(name = "Find Visit By Participant Name")
    List<Visit> findVisitBySubjectName(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name, QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Name")
    List<Visit> findVisitBySubjectName(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name);

    long countFindVisitBySubjectName(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name);

    @Lookup(name = "Find Visit By Participant Community")
    List<Visit> findVisitBySubjectCommunity(@LookupField(name = "subject.community",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community, QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Community")
    List<Visit> findVisitBySubjectCommunity(@LookupField(name = "subject.community",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community);

    long countFindVisitBySubjectCommunity(@LookupField(name = "subject.community",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community);

    @Lookup(name = "Find Visit By Participant Address")
    List<Visit> findVisitBySubjectAddress(@LookupField(name = "subject.address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address, QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Address")
    List<Visit> findVisitBySubjectAddress(@LookupField(name = "subject.address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address);

    long countFindVisitBySubjectAddress(@LookupField(name = "subject.address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address);

    @Lookup(name = "Find Visit By Participant Name Type And Address")
    List<Visit> findVisitBySubjectNameTypeAndAddress(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                                     @LookupField(name = "type") VisitType visitType,
                                                     @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address,
                                                     QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Name Type And Address")
    List<Visit> findVisitBySubjectNameTypeAndAddress(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                                     @LookupField(name = "type") VisitType visitType,
                                                     @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    long countFindVisitBySubjectNameTypeAndAddress(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                                   @LookupField(name = "type") VisitType visitType,
                                                   @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find Visit By Participant Community Type And Address")
    List<Visit> findVisitBySubjectCommunityTypeAndAddress(@LookupField(name = "subject.community",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community,
                                                          @LookupField(name = "type") VisitType visitType,
                                                          @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address,
                                                          QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Community Type And Address")
    List<Visit> findVisitBySubjectCommunityTypeAndAddress(@LookupField(name = "subject.community",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community,
                                                          @LookupField(name = "type") VisitType visitType,
                                                          @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    long countFindVisitBySubjectCommunityTypeAndAddress(@LookupField(name = "subject.community",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community,
                                                        @LookupField(name = "type") VisitType visitType,
                                                        @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find Visit By Participant Address And Type")
    List<Visit> findVisitBySubjectAddressAndType(@LookupField(name = "subject.address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address, @LookupField(name = "type") VisitType visitType, QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Address And Type")
    List<Visit> findVisitBySubjectAddressAndType(@LookupField(name = "subject.address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address, @LookupField(name = "type") VisitType visitType);

    long countFindVisitBySubjectAddresAndType(@LookupField(name = "subject.address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address, @LookupField(name = "type") VisitType visitType);

    @Lookup(name = "Find Visit By Participant Primer Vaccination Date")
    List<Visit> findVisitBySubjectPrimerVaccinationDate(@LookupField(name = "subject.primerVaccinationDate") LocalDate primerVaccinationDate, QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Primer Vaccination Date")
    List<Visit> findVisitBySubjectPrimerVaccinationDate(@LookupField(name = "subject.primerVaccinationDate") LocalDate primerVaccinationDate);

    long countFindVisitBySubjectPrimerVaccinationDate(@LookupField(name = "subject.primerVaccinationDate") LocalDate primerVaccinationDate);

    @Lookup(name = "Find Visit By Participant Primer Vaccination Date Type And Address")
    List<Visit> findVisitBySubjectPrimerVaccinationDateTypeAndAddress(@LookupField(name = "subject.primerVaccinationDate") LocalDate primerVaccinationDate,
                                                                      @LookupField(name = "type") VisitType visitType,
                                                                      @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address,
                                                                      QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Primer Vaccination Date Type And Address")
    List<Visit> findVisitBySubjectPrimerVaccinationDateTypeAndAddress(@LookupField(name = "subject.primerVaccinationDate") LocalDate primerVaccinationDate,
                                                                      @LookupField(name = "type") VisitType visitType,
                                                                      @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    long countFindVisitBySubjectPrimerVaccinationDateATypeAndAddress(@LookupField(name = "subject.primerVaccinationDate") LocalDate primerVaccinationDate,
                                                                     @LookupField(name = "type") VisitType visitType,
                                                                     @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find Visit By Participant Primer Vaccination Date Range")
    List<Visit> findVisitsBySubjectPrimerVaccinationDateRange(@LookupField(name = "subject.primerVaccinationDate")  Range<LocalDate> primerVaccinationDateRange, QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Primer Vaccination Date Range")
    List<Visit> findVisitsBySubjectPrimerVaccinationDateRange(@LookupField(name = "subject.primerVaccinationDate")  Range<LocalDate> primerVaccinationDateRange);

    long countFindVisitsBySubjectPrimerVaccinationDateRange(@LookupField(name = "subject.primerVaccinationDate")  Range<LocalDate> primerVaccinationDateRange);

    @Lookup(name = "Find Visit By Participant Primer Vaccination Date Range Type And Address")
    List<Visit> findVisitsBySubjectPrimerVaccinationDateRangeTypeAndAddress(@LookupField(name = "subject.primerVaccinationDate")  Range<LocalDate> primerVaccinationDateRange,
                                                                            @LookupField(name = "type") VisitType visitType,
                                                                            @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address,
                                                                            QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Primer Vaccination Date Range Type And Address")
    List<Visit> findVisitsBySubjectPrimerVaccinationDateRangeTypeAndAddress(@LookupField(name = "subject.primerVaccinationDate") Range<LocalDate> primerVaccinationDateRange,
                                                                            @LookupField(name = "type") VisitType visitType,
                                                                            @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    long countFindVisitsBySubjectPrimerVaccinationDateRangeTypeAndAddress(@LookupField(name = "subject.primerVaccinationDate") Range<LocalDate> primerVaccinationDateRange,
                                                                          @LookupField(name = "type") VisitType visitType,
                                                                          @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find Visit By Participant Booster Vaccination Date")
    List<Visit> findVisitBySubjectBoosterVaccinationDate(@LookupField(name = "subject.boosterVaccinationDate") LocalDate boosterVaccinationDate, QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Booster Vaccination Date")
    List<Visit> findVisitBySubjectBoosterVaccinationDate(@LookupField(name = "subject.boosterVaccinationDate") LocalDate boosterVaccinationDate);

    long countFindVisitBySubjectBoosterVaccinationDate(@LookupField(name = "subject.boosterVaccinationDate") LocalDate boosterVaccinationDate);

    @Lookup(name = "Find Visit By Participant Booster Vaccination Date Type And Address")
    List<Visit> findVisitBySubjectBoosterVaccinationDateTypeAndAddress(@LookupField(name = "subject.boosterVaccinationDate") LocalDate boosterVaccinationDate,
                                                                       @LookupField(name = "type") VisitType visitType,
                                                                       @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address,
                                                                       QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Booster Vaccination Date Type And Address")
    List<Visit> findVisitBySubjectBoosterVaccinationDateTypeAndAddress(@LookupField(name = "subject.boosterVaccinationDate") LocalDate boosterVaccinationDate,
                                                                       @LookupField(name = "type") VisitType visitType,
                                                                       @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    long countFindVisitBySubjectBoosterVaccinationDateTypeAndAddress(@LookupField(name = "subject.boosterVaccinationDate") LocalDate boosterVaccinationDate,
                                                                     @LookupField(name = "type") VisitType visitType,
                                                                     @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find Visit By Participant Booster Vaccination Date Range")
    List<Visit> findVisitsBySubjectBoosterVaccinationDateRange(@LookupField(name = "subject.boosterVaccinationDate")  Range<LocalDate> boosterVaccinationDateRange, QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Booster Vaccination Date Range")
    List<Visit> findVisitsBySubjectBoosterVaccinationDateRange(@LookupField(name = "subject.boosterVaccinationDate")  Range<LocalDate> boosterVaccinationDateRange);

    long countFindVisitsBySubjectBoosterVaccinationDateRange(@LookupField(name = "subject.boosterVaccinationDate")  Range<LocalDate> boosterVaccinationDateRange);

    @Lookup(name = "Find Visit By Participant Booster Vaccination Date Range Type And Address")
    List<Visit> findVisitsBySubjectBoosterVaccinationDateRangeTypeAndAddress(@LookupField(name = "subject.boosterVaccinationDate")  Range<LocalDate> boosterVaccinationDateRange,
                                                                             @LookupField(name = "type") VisitType visitType,
                                                                             @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address,
                                                                             QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Booster Vaccination Date Range Type And Address")
    List<Visit> findVisitsBySubjectBoosterVaccinationDateRangeTypeAndAddress(@LookupField(name = "subject.boosterVaccinationDate") Range<LocalDate> boosterVaccinationDateRange,
                                                                             @LookupField(name = "type") VisitType visitType,
                                                                             @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    long countFindVisitsBySubjectBoosterVaccinationDateRangeTypeAndAddress(@LookupField(name = "subject.boosterVaccinationDate") Range<LocalDate> boosterVaccinationDateRange,
                                                                           @LookupField(name = "type") VisitType visitType,
                                                                           @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup
    List<Visit> findVisitByPlannedVisitDate(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate);

    @Lookup
    List<Visit> findVisitByPlannedVisitDate(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                               QueryParams queryParams);

    long countFindVisitByPlannedVisitDate(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate);

    @Lookup
    List<Visit> findVisitByPlannedVisitDateAndType(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                                      @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitByPlannedVisitDateAndType(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                                      @LookupField(name = "type") VisitType visitType, QueryParams queryParams);

    long countFindVisitByPlannedVisitDateAndType(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                                    @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitsByPlannedVisitDateRange(@LookupField(name = "motechProjectedDate") Range<LocalDate> dateRange);

    @Lookup
    List<Visit> findVisitsByPlannedVisitDateRange(@LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                     QueryParams queryParams);

    long countFindVisitsByPlannedVisitDateRange(@LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange);

    @Lookup
    List<Visit> findVisitsByPlannedVisitDateRangeAndType(@LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                            @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitsByPlannedVisitDateRangeAndType(@LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                            @LookupField(name = "type") VisitType visitType, QueryParams queryParams);

    long countFindVisitsByPlannedVisitDateRangeAndType(@LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                          @LookupField(name = "type") VisitType visitType);
    /**/
    @Lookup
    List<Visit> findVisitByPlannedVisitDateLess(@LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                                @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup
    List<Visit> findVisitByPlannedVisitDateLess(@LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                                @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date,
                                                QueryParams queryParams);

    long countFindVisitByPlannedVisitDateLess(@LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                              @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup(name = "Find Visit By Participant Name Less")
    List<Visit> findVisitBySubjectNameLess(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                           @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                           @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date,
                                           QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Name Less")
    List<Visit> findVisitBySubjectNameLess(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                           @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                           @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    long countFindVisitBySubjectNameLess(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                         @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                         @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup(name = "Find Visit By Participant Community Less")
    List<Visit> findVisitBySubjectCommunityLess(@LookupField(name = "subject.community",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community,
                                                @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                                @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date,
                                                QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Community Less")
    List<Visit> findVisitBySubjectCommunityLess(@LookupField(name = "subject.community",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community,
                                                @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                                @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    long countFindVisitBySubjectCommunityLess(@LookupField(name = "subject.community",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community,
                                              @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                              @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup(name = "Find Visit By Participant Address Less")
    List<Visit> findVisitBySubjectAddressLess(@LookupField(name = "subject.address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address,
                                              @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                              @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date,
                                              QueryParams queryParams);

    @Lookup(name = "Find Visit By Participant Address Less")
    List<Visit> findVisitBySubjectAddressLess(@LookupField(name = "subject.address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address,
                                              @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                              @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    long countFindVisitBySubjectAddressLess(@LookupField(name = "subject.address", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address,
                                            @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                            @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);
    /**/
    @Lookup
    List<Visit> findVisitByPlannedVisitDateEq(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                               @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup
    List<Visit> findVisitByPlannedVisitDateEq(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                               @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date,
                                               QueryParams queryParams);

    long countFindVisitByPlannedVisitDateEq(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                             @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup
    List<Visit> findVisitByPlannedVisitDateAndTypeEq(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                                      @LookupField(name = "type") VisitType visitType,
                                                      @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup
    List<Visit> findVisitByPlannedVisitDateAndTypeEq(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                                      @LookupField(name = "type") VisitType visitType,
                                                      @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date,
                                                      QueryParams queryParams);

    long countFindVisitByPlannedVisitDateAndTypeEq(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                                    @LookupField(name = "type") VisitType visitType,
                                                    @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup
    List<Visit> findVisitsByPlannedVisitDateRangeEq(@LookupField(name = "motechProjectedDate") Range<LocalDate> dateRange,
                                                     @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup
    List<Visit> findVisitsByPlannedVisitDateRangeEq(@LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                     @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date,
                                                     QueryParams queryParams);

    long countFindVisitsByPlannedVisitDateRangeEq(@LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                   @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup
    List<Visit> findVisitsByPlannedVisitDateRangeAndTypeEq(@LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                            @LookupField(name = "type") VisitType visitType,
                                                            @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup
    List<Visit> findVisitsByPlannedVisitDateRangeAndTypeEq(@LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                            @LookupField(name = "type") VisitType visitType,
                                                            @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date,
                                                            QueryParams queryParams);

    long countFindVisitsByPlannedVisitDateRangeAndTypeEq(@LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                          @LookupField(name = "type") VisitType visitType,
                                                          @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);
}
