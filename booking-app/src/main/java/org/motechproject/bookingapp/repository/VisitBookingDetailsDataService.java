package org.motechproject.bookingapp.repository;

import org.joda.time.LocalDate;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.enums.VisitType;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;

import java.util.List;
import java.util.Set;

public interface VisitBookingDetailsDataService extends MotechDataService<VisitBookingDetails> {

    @Lookup(name = "Find By exact Participant Id")
    List<VisitBookingDetails> findBySubjectId(@LookupField(name = "subject.subjectId") String subjectId);

    @Lookup
    VisitBookingDetails findByVisitId(@LookupField(name = "visit.id") Long visitId);

    @Lookup
    VisitBookingDetails findByParticipantIdAndVisitType(@LookupField(name = "subject.subjectId") String subjectId,
                                                        @LookupField(name = "visit.type") VisitType type);

    @Lookup
    List<VisitBookingDetails> findByBookingPlannedDateClinicIdAndVisitType(@LookupField(name = "bookingPlannedDate", customOperator = Constants.Operators.EQ) LocalDate bookingPlannedDate,
                                                                           @LookupField(name = "clinic.id") Long clinicId,
                                                                           @LookupField(name = "visit.type") VisitType type);

    @Lookup
    List<VisitBookingDetails> findByBookingPlannedDateAndClinicIdAndVisitId(@LookupField(name = "bookingPlannedDate", customOperator = Constants.Operators.EQ) LocalDate bookingPlannedDate,
                                                                            @LookupField(name = "clinic.id") Long clinicId,
                                                                            @LookupField(name = "id", customOperator = Constants.Operators.NEQ) Long id);

    long countFindByBookingPlannedDateAndClinicIdAndVisitId(@LookupField(name = "bookingPlannedDate", customOperator = Constants.Operators.EQ) LocalDate bookingPlannedDate,
                                                            @LookupField(name = "clinic.id") Long clinicId,
                                                            @LookupField(name = "id", customOperator = Constants.Operators.NEQ) Long id);

    @Lookup
    List<VisitBookingDetails> findByClinicIdVisitPlannedDateAndType(@LookupField(name = "clinic.id") Long clinicId,
                                                                    @LookupField(name = "visit.motechProjectedDate") LocalDate plannedDate,
                                                                    @LookupField(name = "visit.type") VisitType type);

    @Lookup
    List<VisitBookingDetails> findByParticipantNamePrimeVaccinationDateAndVisitTypeAndBookingPlannedDateEq(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                                                                                           @LookupField(name = "subject.primerVaccinationDate",
                                                                                                                   customOperator = Constants.Operators.EQ) LocalDate primerVaccinationDate,
                                                                                                           @LookupField(name = "visit.type") VisitType visitType,
                                                                                                           @LookupField(name = "bookingPlannedDate",
                                                                                                                   customOperator = Constants.Operators.EQ) LocalDate bookingPlannedDate);

    @Lookup
    List<VisitBookingDetails> findByClinicIdAndBookingPlannedDateRange(@LookupField(name = "clinic.id") Long clinicId,
                                                                       @LookupField(name = "bookingPlannedDate") Range<LocalDate> date);

    long countFindByClinicIdAndBookingPlannedDateRange(@LookupField(name = "clinic.id") Long clinicId,
                                                       @LookupField(name = "bookingPlannedDate") Range<LocalDate> date);

    @Lookup
    List<VisitBookingDetails> findByClinicIdVisitTypeAndBookingPlannedDateRange(@LookupField(name = "clinic.id") Long clinicId,
                                                                                @LookupField(name = "visit.type") VisitType type,
                                                                                @LookupField(name = "bookingPlannedDate") Range<LocalDate> date);

    long countFindByClinicIdVisitTypeAndBookingPlannedDateRange(@LookupField(name = "clinic.id") Long clinicId,
                                                                @LookupField(name = "visit.type") VisitType type,
                                                                @LookupField(name = "bookingPlannedDate") Range<LocalDate> date);

    @Lookup
    List<VisitBookingDetails> findByClinicIdAndBookingPlannedDate(@LookupField(name = "clinic.id") Long clinicId,
                                                                  @LookupField(name = "bookingPlannedDate") LocalDate date);

    long countFindByClinicIdAndBookingPlannedDate(@LookupField(name = "clinic.id") Long clinicId,
                                                  @LookupField(name = "bookingPlannedDate") LocalDate date);

    @Lookup
    List<VisitBookingDetails> findByClinicIdVisitTypeAndBookingPlannedDate(@LookupField(name = "clinic.id") Long clinicId,
                                                                           @LookupField(name = "visit.type") VisitType type,
                                                                           @LookupField(name = "bookingPlannedDate") LocalDate date);

    long countFindByClinicIdVisitTypeAndBookingPlannedDate(@LookupField(name = "clinic.id") Long clinicId,
                                                           @LookupField(name = "visit.type") VisitType type,
                                                           @LookupField(name = "bookingPlannedDate") LocalDate date);

    @Lookup
    List<VisitBookingDetails> findByExactParticipantSiteId(@LookupField(name = "subject.siteId") String siteId);

    /**
     *  UI Lookups
     */

    @Lookup
    List<VisitBookingDetails> findByParticipantId(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId);

    @Lookup
    List<VisitBookingDetails> findByParticipantName(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name);

    @Lookup
    List<VisitBookingDetails> findByStageId(@LookupField(name = "subject.stageId") Long stageId);

    @Lookup
    List<VisitBookingDetails> findByVisitType(@LookupField(name = "visit.type") VisitType type);

    @Lookup
    List<VisitBookingDetails> findByClinicLocation(@LookupField(name = "clinic.location",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String location);

    @Lookup
    List<VisitBookingDetails> findByVisitActualDate(@LookupField(name = "visit.date") LocalDate date);

    @Lookup
    List<VisitBookingDetails> findByVisitActualDateRange(@LookupField(name = "visit.date") Range<LocalDate> date);

    @Lookup
    List<VisitBookingDetails> findByVisitPlannedDate(@LookupField(name = "visit.motechProjectedDate") LocalDate date);

    @Lookup
    List<VisitBookingDetails> findByVisitPlannedDateRange(@LookupField(name = "visit.motechProjectedDate") Range<LocalDate> date);

    @Lookup
    List<VisitBookingDetails> findByVisitTypeAndParticipantPrimeVaccinationDateAndName(@LookupField(name = "visit.type") VisitType type,
                                                                                       @LookupField(name = "subject.primerVaccinationDate",
                                                                                               customOperator = Constants.Operators.EQ) LocalDate primerVaccinationDate,
                                                                                       @LookupField(name = "subject.name",
                                                                                               customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name);

    /**
     *  Prime Vaccination Screen Lookups
     */

    @Lookup
    List<VisitBookingDetails> findByParticipantNamePrimeVaccinationDateAndVisitTypeAndBookingPlannedDate(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                                                                                         @LookupField(name = "subject.primerVaccinationDate",
                                                                                                                 customOperator = Constants.Operators.EQ) LocalDate primerVaccinationDate,
                                                                                                         @LookupField(name = "visit.type") VisitType visitType,
                                                                                                         @LookupField(name = "bookingPlannedDate",
                                                                                                                 customOperator = Constants.Operators.NEQ) LocalDate bookingPlannedDate);

    @Lookup
    List<VisitBookingDetails> findByParticipantIdVisitTypeAndParticipantPrimeVaccinationDateAndNameAndBookingPlannedDate(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId,
                                                                                                                         @LookupField(name = "visit.type") VisitType visitType,
                                                                                                                         @LookupField(name = "subject.primerVaccinationDate",
                                                                                                                                 customOperator = Constants.Operators.EQ) LocalDate primerVaccinationDate,
                                                                                                                         @LookupField(name = "subject.name",
                                                                                                                                 customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                                                                                                         @LookupField(name = "bookingPlannedDate",
                                                                                                                                 customOperator = Constants.Operators.NEQ) LocalDate bookingPlannedDate);

    @Lookup
    List<VisitBookingDetails> findByParticipantNamePrimeVaccinationDateAndVisitTypeAndBookingPlannedDateRange(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                                                                                              @LookupField(name = "subject.primerVaccinationDate",
                                                                                                                      customOperator = Constants.Operators.EQ) LocalDate primerVaccinationDate,
                                                                                                              @LookupField(name = "visit.type") VisitType visitType,
                                                                                                              @LookupField(name = "bookingPlannedDate") Range<LocalDate> bookingPlannedDate);

    @Lookup
    List<VisitBookingDetails> findByParticipantIdVisitTypeAndParticipantPrimeVaccinationDateAndNameAndBookingPlannedDateRange(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId,
                                                                                                                              @LookupField(name = "visit.type") VisitType visitType,
                                                                                                                              @LookupField(name = "subject.primerVaccinationDate",
                                                                                                                                      customOperator = Constants.Operators.EQ) LocalDate primerVaccinationDate,
                                                                                                                              @LookupField(name = "subject.name",
                                                                                                                                      customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                                                                                                              @LookupField(name = "bookingPlannedDate") Range<LocalDate> bookingPlannedDate);

    /**
     *  Reschedule Screen Lookups
     */

    @Lookup
    List<VisitBookingDetails> findByVisitTypeSetAndPlannedDate(@LookupField(name = "visit.type") Set<VisitType> typeSet,
                                                               @LookupField(name = "visit.motechProjectedDate",
                                                               customOperator = Constants.Operators.NEQ) LocalDate plannedDate);

    @Lookup
    List<VisitBookingDetails> findByVisitTypeSetAndPlannedDateRange(@LookupField(name = "visit.type") Set<VisitType> typeSet,
                                                                    @LookupField(name = "visit.motechProjectedDate") Range<LocalDate> date);

    @Lookup
    List<VisitBookingDetails> findByParticipantIdAndVisitTypeSetAndPlannedDate(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId,
                                                                               @LookupField(name = "visit.type") Set<VisitType> typeSet,
                                                                               @LookupField(name = "visit.motechProjectedDate",
                                                                               customOperator = Constants.Operators.NEQ) LocalDate plannedDate);

    @Lookup
    List<VisitBookingDetails> findByParticipantIdAndVisitTypeSetAndPlannedDateRange(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId,
                                                                               @LookupField(name = "visit.type") Set<VisitType> typeSet,
                                                                               @LookupField(name = "visit.motechProjectedDate") Range<LocalDate> plannedDate);

    @Lookup
    List<VisitBookingDetails> findByParticipantNameAndVisitTypeSetAndPlannedDate(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                                                                 @LookupField(name = "visit.type") Set<VisitType> typeSet,
                                                                                 @LookupField(name = "visit.motechProjectedDate",
                                                                                         customOperator = Constants.Operators.NEQ) LocalDate plannedDate);

    @Lookup
    List<VisitBookingDetails> findByParticipantNameAndVisitTypeSetAndPlannedDateRange(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                                                                 @LookupField(name = "visit.type") Set<VisitType> typeSet,
                                                                                 @LookupField(name = "visit.motechProjectedDate") Range<LocalDate> plannedDate);

    @Lookup
    List<VisitBookingDetails> findByStageIdAndVisitTypeSetAndPlannedDate(@LookupField(name = "subject.stageId") Long stageId,
                                                                                 @LookupField(name = "visit.type") Set<VisitType> typeSet,
                                                                                 @LookupField(name = "visit.motechProjectedDate",
                                                                                         customOperator = Constants.Operators.NEQ) LocalDate plannedDate);

    @Lookup
    List<VisitBookingDetails> findByStageIdAndVisitTypeSetAndPlannedDateRange(@LookupField(name = "subject.stageId") Long stageId,
                                                                                      @LookupField(name = "visit.type") Set<VisitType> typeSet,
                                                                                      @LookupField(name = "visit.motechProjectedDate") Range<LocalDate> plannedDate);

    @Lookup
    List<VisitBookingDetails> findByVisitTypeAndPlannedDate(@LookupField(name = "visit.type") VisitType type,
                                                            @LookupField(name = "visit.motechProjectedDate",
                                                                    customOperator = Constants.Operators.NEQ) LocalDate plannedDate);

    @Lookup
    List<VisitBookingDetails> findByVisitTypeAndPlannedDateRange(@LookupField(name = "visit.type") VisitType type,
                                                            @LookupField(name = "visit.motechProjectedDate") Range<LocalDate> plannedDate);

    @Lookup
    List<VisitBookingDetails> findByClinicLocationAndVisitTypeSetAndPlannedDate(@LookupField(name = "clinic.location",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String location,
                                                                                @LookupField(name = "visit.type") Set<VisitType> typeSet,
                                                                                @LookupField(name = "visit.motechProjectedDate",
                                                                                        customOperator = Constants.Operators.NEQ) LocalDate plannedDate);

    @Lookup
    List<VisitBookingDetails> findByClinicLocationAndVisitTypeSetAndPlannedDateRange(@LookupField(name = "clinic.location",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String location,
                                                                                @LookupField(name = "visit.type") Set<VisitType> typeSet,
                                                                                @LookupField(name = "visit.motechProjectedDate") Range<LocalDate> plannedDate);

    @Lookup
    List<VisitBookingDetails> findByVisitActualDateAndVisitTypeSetAndPlannedDate(@LookupField(name = "visit.date") LocalDate date,
                                                                                 @LookupField(name = "visit.type") Set<VisitType> typeSet,
                                                                                 @LookupField(name = "visit.motechProjectedDate",
                                                                                         customOperator = Constants.Operators.NEQ) LocalDate plannedDate);

    @Lookup
    List<VisitBookingDetails> findByVisitActualDateAndVisitTypeSetAndPlannedDateRange(@LookupField(name = "visit.date") LocalDate date,
                                                                                 @LookupField(name = "visit.type") Set<VisitType> typeSet,
                                                                                 @LookupField(name = "visit.motechProjectedDate") Range<LocalDate> plannedDate);


    @Lookup
    List<VisitBookingDetails> findByVisitActualDateRangeAndVisitTypeSetAndPlannedDate(@LookupField(name = "visit.date") Range<LocalDate> date,
                                                                                      @LookupField(name = "visit.type") Set<VisitType> typeSet,
                                                                                      @LookupField(name = "visit.motechProjectedDate",
                                                                                              customOperator = Constants.Operators.NEQ) LocalDate plannedDate);

    @Lookup
    List<VisitBookingDetails> findByVisitActualDateRangeAndVisitTypeSetAndPlannedDateRange(@LookupField(name = "visit.date") Range<LocalDate> date,
                                                                                      @LookupField(name = "visit.type") Set<VisitType> typeSet,
                                                                                      @LookupField(name = "visit.motechProjectedDate") Range<LocalDate> plannedDate);

}
