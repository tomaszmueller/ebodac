package org.motechproject.ebodac.repository;

import org.joda.time.LocalDate;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;

import java.util.List;
import java.util.Set;

/**
 * Interface for repository that persists simple records and allows CRUD.
 * MotechDataService base class will provide the implementation of this class as well
 * as methods for adding, deleting, saving and finding all instances.  In this class we
 * define and custom lookups we may need.
 */
public interface VisitDataService extends MotechDataService<Visit> {

    @Lookup
    List<Visit> findByActualDate(@LookupField(name = "date") LocalDate date);

    @Lookup
    List<Visit> findByActualDateAndType(@LookupField(name = "date") LocalDate date,
                                        @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findByActualDateRange(@LookupField(name = "date") Range<LocalDate> dateRange);

    @Lookup
    List<Visit> findByActualDateRangeAndType(@LookupField(name = "date") Range<LocalDate> dateRange,
                                             @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findByType(@LookupField(name = "type") VisitType visitType);

    @Lookup(name = "Find By Participant Id")
    List<Visit> findBySubjectId(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId);

    @Lookup(name = "Find By Participant Name")
    List<Visit> findBySubjectName(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name);

    @Lookup(name = "Find By Participant Community")
    List<Visit> findBySubjectCommunity(@LookupField(name = "subject.community",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community);

    @Lookup(name = "Find By Participant Address")
    List<Visit> findBySubjectAddress(@LookupField(name = "subject.address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address);

    @Lookup(name = "Find By Participant Primer Vaccination Date")
    List<Visit> findBySubjectPrimerVaccinationDate(@LookupField(name = "subject.primerVaccinationDate") LocalDate primerVaccinationDate);

    @Lookup(name = "Find By Participant Primer Vaccination Date Range")
    List<Visit> findBySubjectPrimerVaccinationDateRange(@LookupField(name = "subject.primerVaccinationDate")  Range<LocalDate> primerVaccinationDateRange);

    @Lookup(name = "Find By Participant Primer Vaccination Date Range And Site Name")
    List<Visit> findBySubjectPrimerVaccinationDateRangeAndSiteName(@LookupField(name = "subject.primerVaccinationDate")  Range<LocalDate> primerVaccinationDateRange,
                                                                   @LookupField(name = "subject.siteName", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String siteName);

    @Lookup(name = "Find By Participant Booster Vaccination Date")
    List<Visit> findBySubjectBoosterVaccinationDate(@LookupField(name = "subject.boosterVaccinationDate") LocalDate boosterVaccinationDate);

    @Lookup(name = "Find By Participant Booster Vaccination Date Range")
    List<Visit> findBySubjectBoosterVaccinationDateRange(@LookupField(name = "subject.boosterVaccinationDate")  Range<LocalDate> boosterVaccinationDateRange);

    @Lookup(name = "Find By Participant Booster Vaccination Date Range And Site Name")
    List<Visit> findBySubjectBoosterVaccinationDateRangeAndSiteName(@LookupField(name = "subject.boosterVaccinationDate")  Range<LocalDate> boosterVaccinationDateRange,
                                                                    @LookupField(name = "subject.siteName", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String siteName);

    @Lookup
    List<Visit> findByPlannedVisitDate(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate);

    @Lookup
    List<Visit> findByPlannedVisitDateAndType(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                              @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findByPlannedVisitDateRange(@LookupField(name = "motechProjectedDate") Range<LocalDate> dateRange);

    @Lookup
    List<Visit> findByPlannedVisitDateRangeAndType(@LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                   @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findByPlannedVisitDateRangeAndSiteName(@LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                   @LookupField(name = "subject.siteName", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String siteName);
    @Lookup
    List<Visit> findByActualDateRangeAndSiteName(@LookupField(name = "date") Range<LocalDate> dateRange,
                                                     @LookupField(name = "subject.siteName") String siteName);

    @Lookup
    List<Visit> findByActualDateRangeAndStageId(@LookupField(name = "date") Range<LocalDate> dateRange,
                                                    @LookupField(name = "subject.stageId") Long stageId);

    @Lookup
    List<Visit> findByActualDateRangeStageIdAndSiteName(@LookupField(name = "date") Range<LocalDate> dateRange,
                                                            @LookupField(name = "subject.stageId") Long stageId,
                                                            @LookupField(name = "subject.siteName") String siteName);

    /**
     *  Day 8 and Day 58 Report Lookups
     */

    @Lookup
    List<Visit> findByTypeSet(@LookupField(name = "type") Set<VisitType> visitType);

    @Lookup
    List<Visit> findByVisitTypeActualVisitDateAndSiteName(@LookupField(name = "type") VisitType visitType,
                                                               @LookupField(name = "date") Range<LocalDate> motechProjectedDateRange,
                                                               @LookupField(name = "subject.siteName", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String siteName);

    @Lookup
    List<Visit> findByVisitTypeActualVisitDateAndStageId(@LookupField(name = "type") VisitType visitType,
                                                               @LookupField(name = "date") Range<LocalDate> motechProjectedDateRange,
                                                               @LookupField(name = "subject.stageId") Long stageId);

    @Lookup
    List<Visit> findByVisitTypeAndActualVisitDate(@LookupField(name = "type") VisitType visitType,
                                                               @LookupField(name = "date") Range<LocalDate> motechProjectedDateRange);

    @Lookup
    List<Visit> findByVisitTypeAndSiteName(@LookupField(name = "type") VisitType visitType,
                                           @LookupField(name = "subject.siteName", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String siteName);

    @Lookup
    List<Visit> findByVisitTypeAndStageId(@LookupField(name = "type") VisitType visitType,
                                          @LookupField(name = "subject.stageId") Long stageId);

    @Lookup
    List<Visit> findByVisitTypePlannedVisitDateAndSiteName(@LookupField(name = "type") VisitType visitType,
                                                               @LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                               @LookupField(name = "subject.siteName", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String siteName);

    @Lookup
    List<Visit> findByVisitTypePlannedVisitDateAndStageId(@LookupField(name = "type") VisitType visitType,
                                                               @LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                               @LookupField(name = "subject.stageId") Long stageId);

    /**
     *  Followups After Prime Injection Report Lookups
     */

    @Lookup
    List<Visit> findByTypePhoneNumberAndAddress(@LookupField(name = "type") Set<VisitType> visitType,
                                                @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find By Participant Address Phone Number And Type")
    List<Visit> findBySubjectAddressPhoneNumberAndType(@LookupField(name = "subject.address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address,
                                                       @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                       @LookupField(name = "type") Set<VisitType> visitType);

    @Lookup(name = "Find By Participant Name Type Phone Number And Address")
    List<Visit> findBySubjectNameTypePhoneNumberAndAddress(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                                           @LookupField(name = "type") Set<VisitType> visitType,
                                                           @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                           @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find By Participant Community Type Phone Number And Address")
    List<Visit> findBySubjectCommunityTypePhoneNumberAndAddress(@LookupField(name = "subject.community",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community,
                                                                @LookupField(name = "type") Set<VisitType> visitType,
                                                                @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                                @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find By Participant Primer Vaccination Date Type Phone Number And Address")
    List<Visit> findBySubjectPrimerVaccinationDateTypePhoneNumberAndAddress(@LookupField(name = "subject.primerVaccinationDate") LocalDate primerVaccinationDate,
                                                                            @LookupField(name = "type") Set<VisitType> visitType,
                                                                            @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                                            @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find By Participant Primer Vaccination Date Range Type Phone Number And Address")
    List<Visit> findBySubjectPrimerVaccinationDateRangeTypePhoneNumberAndAddress(@LookupField(name = "subject.primerVaccinationDate") Range<LocalDate> primerVaccinationDateRange,
                                                                                 @LookupField(name = "type") Set<VisitType> visitType,
                                                                                 @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                                                 @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find By Participant Booster Vaccination Date Type Phone Number And Address")
    List<Visit> findBySubjectBoosterVaccinationDateTypePhoneNumberAndAddress(@LookupField(name = "subject.boosterVaccinationDate") LocalDate boosterVaccinationDate,
                                                                             @LookupField(name = "type") Set<VisitType> visitType,
                                                                             @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                                             @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find By Participant Booster Vaccination Date Range Type Phone Number And Address")
    List<Visit> findBySubjectBoosterVaccinationDateRangeTypePhoneNumberAndAddress(@LookupField(name = "subject.boosterVaccinationDate") Range<LocalDate> boosterVaccinationDateRange,
                                                                                  @LookupField(name = "type") Set<VisitType> visitType,
                                                                                  @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                                                  @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find By Participant Primer Vaccination Date Range Site Name Type Phone Number And Address")
    List<Visit> findBySubjectPrimerVaccinationDateRangeSiteNameTypePhoneNumberAndAddress(@LookupField(name = "subject.primerVaccinationDate") Range<LocalDate> primerVaccinationDateRange,
                                                                                 @LookupField(name = "subject.siteName", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String siteName,
                                                                                 @LookupField(name = "type") Set<VisitType> visitType,
                                                                                 @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                                                 @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find By Participant Booster Vaccination Date Range And Site Name Type Phone Number And Address")
    List<Visit> findBySubjectBoosterVaccinationDateRangeSiteNameTypePhoneNumberAndAddress(@LookupField(name = "subject.boosterVaccinationDate") Range<LocalDate> boosterVaccinationDateRange,
                                                                                  @LookupField(name = "subject.siteName", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String siteName,
                                                                                  @LookupField(name = "type") Set<VisitType> visitType,
                                                                                  @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                                                  @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);


    /**
    *  Followups Missed Clinic Visits Report Lookups
    */

    @Lookup
    List<Visit> findByPlannedDateLessAndActualDateEqAndSubjectPhoneNumberEq(@LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                                            @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                                                            @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    // TODO: rename this accordingly to new phone number parameter
    @Lookup(name = "Find By Participant Name Less")
    List<Visit> findBySubjectNameLess(@LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                      @LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                      @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                      @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    // TODO: rename this accordingly to new phone number parameter
    @Lookup(name = "Find By Participant Community Less")
    List<Visit> findBySubjectCommunityLess(@LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                           @LookupField(name = "subject.community",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community,
                                           @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                           @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    // TODO: rename this accordingly to new phone number parameter
    @Lookup(name = "Find By Participant Address Less")
    List<Visit> findBySubjectAddressLess(@LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                         @LookupField(name = "subject.address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address,
                                         @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                         @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    // TODO: rename this accordingly to new phone number parameter
    @Lookup
    List<Visit> findByPlannedVisitDateEq(@LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                         @LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                         @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    // TODO: rename this accordingly to new phone number parameter
    @Lookup
    List<Visit> findByPlannedVisitDateAndTypeEq(@LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                @LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                                @LookupField(name = "type") VisitType visitType,
                                                @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    // TODO: rename this accordingly to new phone number parameter
    @Lookup
    List<Visit> findByPlannedVisitDateRangeEq(@LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                              @LookupField(name = "motechProjectedDate") Range<LocalDate> dateRange,
                                              @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    // TODO: rename this accordingly to new phone number parameter
    @Lookup
    List<Visit> findByPlannedVisitDateRangeAndTypeEq(@LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                     @LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                     @LookupField(name = "type") VisitType visitType,
                                                     @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    // TODO: rename this accordingly to new phone number parameter
    @Lookup
    List<Visit> findByPlannedVisitDateRangeAndSiteNameEq(@LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                     @LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                     @LookupField(name = "subject.siteName", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String siteName,
                                                     @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);


    /**
     *  M&E Missed Clinic Visits Report Lookups
     */

    @Lookup
    List<Visit> findByPlannedVisitDateLessAndActualVisitDate(@LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                                             @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup(name = "Find By Participant Id And Planned Visit Date And Actual Visit Date")
    List<Visit> findBySubjectIdAndPlannedVisitDateAndActualVisitDate(@LookupField(name = "subject.subjectId", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId,
                                                                     @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                                                     @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup(name = "Find By Participant Name And Planned Visit Date And Actual Visit Date")
    List<Visit> findBySubjectNameAndPlannedVisitDateAndActualVisitDate(@LookupField(name = "subject.name", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                                                       @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                                                       @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup(name = "Find By Participant Community And Planned Visit Date And Actual Visit Date")
    List<Visit> findBySubjectCommunityAndPlannedVisitDateAndActualVisitDate(@LookupField(name = "subject.community", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community,
                                                                            @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                                                            @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup(name = "Find By Participant Address And Planned Visit Date And Actual Visit Date")
    List<Visit> findBySubjectAddressAndPlannedVisitDateAndActualVisitDate(@LookupField(name = "subject.address", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address,
                                                                          @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                                                          @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup
    List<Visit> findByPlannedVisitDateAndActualVisitDate(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                                         @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup
    List<Visit> findByPlannedVisitDateAndTypeAndActualVisitDate(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                                                @LookupField(name = "type") VisitType visitType,
                                                                @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup
    List<Visit> findByPlannedVisitDateRangeAndActualVisitDate(@LookupField(name = "motechProjectedDate") Range<LocalDate> dateRange,
                                                              @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup
    List<Visit> findByPlannedVisitDateRangeAndTypeAndActualVisitDate(@LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                                     @LookupField(name = "type") VisitType visitType,
                                                                     @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup
    List<Visit> findByPlannedVisitDateRangeAndSiteNameAndActualVisitDate(@LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                                     @LookupField(name = "subject.siteName", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String siteName,
                                                                     @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    /**
     * Screening Visits Report Lookups
     */

    @Lookup
    List<Visit> findByActualDateRangeAndSiteNameAndType(@LookupField(name = "date") Range<LocalDate> dateRange,
                                                     @LookupField(name = "subject.siteName", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String siteName,
                                                     @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findByActualDateRangeAndStageIdAndType(@LookupField(name = "date") Range<LocalDate> dateRange,
                                                    @LookupField(name = "subject.stageId") Long stageId,
                                                    @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findByActualDateRangeStageIdAndSiteNameAndType(@LookupField(name = "date") Range<LocalDate> dateRange,
                                                            @LookupField(name = "subject.stageId") Long stageId,
                                                            @LookupField(name = "subject.siteName", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String siteName,
                                                            @LookupField(name = "type") VisitType visitType);

    /**
     *  Booking App Lookups
     */

    @Lookup
    Visit findBySubjectIdAndType(@LookupField(name = "subject.subjectId") String subjectId,
                                 @LookupField(name = "type") VisitType type);

    @Lookup
    List<Visit> findByVisitTypeAndActualDateLess(@LookupField(name = "type") VisitType type,
                                                 @LookupField(name = "date", customOperator = Constants.Operators.LT) LocalDate date);
}
