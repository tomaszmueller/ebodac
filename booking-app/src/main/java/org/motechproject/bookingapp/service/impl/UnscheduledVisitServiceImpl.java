package org.motechproject.bookingapp.service.impl;


import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.motechproject.bookingapp.constants.BookingAppConstants;
import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.bookingapp.domain.UnscheduledVisit;
import org.motechproject.bookingapp.domain.UnscheduledVisitDto;
import org.motechproject.bookingapp.helper.VisitLimitationHelper;
import org.motechproject.bookingapp.repository.ClinicDataService;
import org.motechproject.bookingapp.repository.UnscheduledVisitDataService;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.bookingapp.service.UnscheduledVisitService;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.commons.date.model.Time;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.util.QueryParamsBuilder;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.query.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Service("unscheduledVisitService")
public class UnscheduledVisitServiceImpl implements UnscheduledVisitService {

    @Autowired
    private LookupService lookupService;

    @Autowired
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Autowired
    private UnscheduledVisitDataService unscheduledVisitDataService;

    @Autowired
    private SubjectDataService subjectDataService;

    @Autowired
    private VisitLimitationHelper visitLimitationHelper;

    @Autowired
    private ClinicDataService clinicDataService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Records<UnscheduledVisitDto> getUnscheduledVisitsRecords(BookingGridSettings settings) throws IOException {
        QueryParams queryParams = QueryParamsBuilder.buildQueryParams(settings, getFields(settings.getFields()));

        return lookupService.getEntities(UnscheduledVisitDto.class, UnscheduledVisit.class,
                settings.getLookup(), settings.getFields(), queryParams);
    }

    @Override
    public UnscheduledVisitDto addOrUpdate(UnscheduledVisitDto dto, Boolean ignoreLimitation) {
        Subject subject = subjectDataService.findBySubjectId(dto.getParticipantId());
        Clinic clinic = clinicDataService.findByExactSiteId(subject.getSiteId());

        if (clinic != null && !ignoreLimitation) {
            if (StringUtils.isNotBlank(dto.getId())) {
                visitLimitationHelper.checkCapacityForUnscheduleVisit(dto.getDate(), clinic, Long.parseLong(dto.getId()));
            } else {
                visitLimitationHelper.checkCapacityForUnscheduleVisit(dto.getDate(), clinic, null);
            }
        }
        if (StringUtils.isEmpty(dto.getId())) {
            return add(dto, subject, clinic);
        } else {
            return update(dto, subject, clinic);
        }
    }

    private UnscheduledVisitDto add(UnscheduledVisitDto dto, Subject subject, Clinic clinic) {

        UnscheduledVisit unscheduledVisit = new UnscheduledVisit();

        unscheduledVisit.setSubject(subject);
        unscheduledVisit.setClinic(clinic);
        unscheduledVisit.setDate(dto.getDate());
        unscheduledVisit.setStartTime(dto.getStartTime());
        unscheduledVisit.setEndTime(calculateEndTime(dto.getStartTime()));
        unscheduledVisit.setPurpose(dto.getPurpose());

        return new UnscheduledVisitDto(unscheduledVisitDataService.create(unscheduledVisit));
    }

    private UnscheduledVisitDto update(UnscheduledVisitDto dto, Subject subject, Clinic clinic) {

        UnscheduledVisit unscheduledVisit = unscheduledVisitDataService.findById(Long.valueOf(dto.getId()));

        unscheduledVisit.setSubject(subject);
        unscheduledVisit.setClinic(clinic);
        unscheduledVisit.setDate(dto.getDate());
        unscheduledVisit.setStartTime(dto.getStartTime());
        unscheduledVisit.setEndTime(calculateEndTime(dto.getStartTime()));
        unscheduledVisit.setPurpose(dto.getPurpose());

        return new UnscheduledVisitDto(unscheduledVisitDataService.create(unscheduledVisit));
    }

    private Map<String, Object> getFields(String json) throws IOException {
        if (json == null) {
            return null;
        } else {
            return objectMapper.readValue(json, new TypeReference<LinkedHashMap>() {}); //NO CHECKSTYLE WhitespaceAround
        }
    }

    private Time calculateEndTime(Time startTime) {
        int endTimeHour = (startTime.getHour() + BookingAppConstants.TIME_OF_THE_VISIT) % BookingAppConstants.MAX_TIME_HOUR;
        return new Time(endTimeHour, startTime.getMinute());
    }
}
