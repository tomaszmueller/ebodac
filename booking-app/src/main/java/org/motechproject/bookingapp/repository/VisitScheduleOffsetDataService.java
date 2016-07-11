package org.motechproject.bookingapp.repository;

import org.motechproject.bookingapp.domain.VisitScheduleOffset;
import org.motechproject.ebodac.domain.enums.VisitType;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;

public interface VisitScheduleOffsetDataService extends MotechDataService<VisitScheduleOffset> {

    @Lookup
    VisitScheduleOffset findByVisitTypeAndStageId(@LookupField(name = "visitType") VisitType visitType,
                                                  @LookupField(name = "stageId") Long stageId);

    @Lookup
    List<VisitScheduleOffset> findByStageId(@LookupField(name = "stageId") Long stageId);
}
