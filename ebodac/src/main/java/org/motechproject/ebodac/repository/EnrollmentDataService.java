package org.motechproject.ebodac.repository;

import org.joda.time.LocalDate;
import org.motechproject.ebodac.domain.Enrollment;
import org.motechproject.ebodac.domain.EnrollmentStatus;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;

import java.util.List;
import java.util.Set;

public interface EnrollmentDataService extends MotechDataService<Enrollment> {

    @Lookup(name = "Find By Participant Id")
    List<Enrollment> findBySubjectId(@LookupField(name = "externalId") String externalId);

    @Lookup(name = "Find By Participant Id")
    List<Enrollment> findBySubjectId(@LookupField(name = "externalId") String externalId, QueryParams queryParams);

    long countFindBySubjectId(@LookupField(name = "externalId") String externalId);

    @Lookup
    List<Enrollment> findByCampaignName(@LookupField(name = "campaignName") String campaignName);

    @Lookup(name = "Find By Status Reference Date Campaign Name And Participant Ids")
    List<Enrollment> findByStatusReferenceDateCampaignNameAndSubjectIds(@LookupField(name = "status") EnrollmentStatus status,
                                                                        @LookupField(name = "referenceDate") LocalDate referenceDate,
                                                                        @LookupField(name = "campaignName", customOperator = Constants.Operators.MATCHES) String campaignName,
                                                                        @LookupField(name = "externalId") Set<String> externalIds);

    @Lookup(name = "Find By Participant Id And Campaign Name")
    Enrollment findBySubjectIdAndCampaignName(@LookupField(name = "externalId") String externalId,
                                              @LookupField(name = "campaignName") String campaignName);
}
