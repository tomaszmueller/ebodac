package org.motechproject.bookingapp.service;

import org.motechproject.bookingapp.domain.VisitScheduleOffset;
import org.motechproject.ebodac.domain.enums.VisitType;

import java.util.List;
import java.util.Map;

public interface VisitScheduleOffsetService {

    VisitScheduleOffset findByVisitTypeAndStageId(VisitType visitType, Long stageId);

    List<VisitScheduleOffset> getAll();

    Map<VisitType, VisitScheduleOffset> getAsMapByStageId(Long stageId);

    Map<Long, Map<VisitType, VisitScheduleOffset>> getAllAsMap();
}
