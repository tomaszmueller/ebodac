package org.motechproject.ebodac.web;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Enrollment;
import org.motechproject.ebodac.domain.SubjectEnrollments;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.exception.EbodacEnrollmentException;
import org.motechproject.ebodac.repository.EnrollmentDataService;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.service.VisitService;
import org.motechproject.ebodac.web.domain.GridSettings;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
public class EbodacEnrollmentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EbodacEnrollmentController.class);

    @Autowired
    private EbodacEnrollmentService ebodacEnrollmentService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    @Autowired
    private EnrollmentDataService enrollmentDataService;

    @Autowired
    private LookupService lookupService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/reenrollSubject", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> reenrollVisit(@RequestBody Visit visit) {

        if (visit == null) {
            return new ResponseEntity<>("Visit cannot be null", HttpStatus.BAD_REQUEST);
        } else if(visit.getSubject() == null) {
            return new ResponseEntity<>("Visit must have Subject to be enrolled", HttpStatus.BAD_REQUEST);
        } else {
            Visit existingVisit = visitService.findVisitBySubjectIdAndVisitType(visit.getSubject().getSubjectId(), visit.getType());

            if (existingVisit == null) {
                return new ResponseEntity<>("Cannot find visit in the database", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (existingVisit.getDate() != null) {
                return new ResponseEntity<>("Cannot re-enroll Subject for that Visit, because visit already took place", HttpStatus.BAD_REQUEST);
            } else if (visit.getMotechProjectedDate() == null) {
                return new ResponseEntity<>("Cannot re-enroll Subject for that Visit, because motech projected date is null", HttpStatus.BAD_REQUEST);
            } else if (visit.getMotechProjectedDate().equals(existingVisit.getMotechProjectedDate())) {
                return new ResponseEntity<>("Cannot re-enroll Subject for that Visit, because motech projected date wasn't changed", HttpStatus.BAD_REQUEST);
            } else if (visit.getMotechProjectedDate().isBefore(LocalDate.now())) {
                return new ResponseEntity<>("Cannot re-enroll Subject for that Visit, because motech projected date is in the past", HttpStatus.BAD_REQUEST);
            } else {

                try {
                    ebodacEnrollmentService.reenrollSubject(visit);
                } catch (EbodacEnrollmentException e) {
                    LOGGER.debug(e.getMessage(), e);
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/availableCampaigns", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getAvailableCampaigns() {
        return EbodacConstants.AVAILABLE_CAMPAIGNS;
    }

    @PreAuthorize("hasAnyRole('mdsDataAccess', 'manageEbodac')")
    @RequestMapping(value = "/getEnrollments", method = RequestMethod.POST)
    @ResponseBody
    public Records<?> getEnrollments(GridSettings settings) throws IOException {
        Order order = null;
        if (!settings.getSortColumn().isEmpty()) {
            order = new Order(settings.getSortColumn(), settings.getSortDirection());
        }

        QueryParams queryParams = new QueryParams(settings.getPage(), settings.getRows(), order);

        try {
            return lookupService.getEntities(SubjectEnrollments.class, settings.getLookup(), settings.getFields(), queryParams);
        } catch (IOException e) {
            LOGGER.debug(e.getMessage(), e);
            return null;
        }
    }

    @PreAuthorize("hasAnyRole('mdsDataAccess', 'manageEbodac')")
    @RequestMapping(value = "/getLookupsForEnrollments", method = RequestMethod.GET)
    @ResponseBody
    public List<LookupDto> getLookupsForEnrollments() {
        return lookupService.getAvailableLookups("SubjectEnrollments");
    }

    @PreAuthorize("hasAnyRole('mdsDataAccess', 'manageEbodac')")
    @RequestMapping(value = "/getEnrollmentAdvanced/{subjectId}", method = RequestMethod.POST)
    @ResponseBody
    public Records<?> getEnrollmentAdvanced(@PathVariable String subjectId, GridSettings settings) throws IOException {
        Order order = null;
        if (!settings.getSortColumn().isEmpty()) {
            order = new Order(settings.getSortColumn(), settings.getSortDirection());
        }

        QueryParams queryParams = new QueryParams(null, null, order);

        long recordCount;
        int rowCount;

        recordCount = enrollmentDataService.countFindEnrollmentsBySubjectId(subjectId);
        rowCount = (int) Math.ceil(recordCount / (double) settings.getRows());

        List<Enrollment> enrollments = enrollmentDataService.findEnrollmentsBySubjectId(subjectId, queryParams);

        return new Records<>(settings.getPage(), rowCount, (int) recordCount, enrollments);
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/enrollSubject", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> enrollSubject(@RequestBody String subjectId) {
        if (StringUtils.isBlank(subjectId)) {
            return new ResponseEntity<>("Subject id cannot be empty", HttpStatus.BAD_REQUEST);
        }

        try {
            ebodacEnrollmentService.enrollSubject(subjectId);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/unenrollSubject", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> unenrollSubject(@RequestBody String subjectId) {
        if (StringUtils.isBlank(subjectId)) {
            return new ResponseEntity<>("Subject id cannot be empty", HttpStatus.BAD_REQUEST);
        }

        try {
            ebodacEnrollmentService.unenrollSubject(subjectId);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/enrollCampaign/{subjectId}/{campaignName}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> enrollCampaign(@PathVariable String subjectId, @PathVariable String campaignName) throws IOException {

        if (StringUtils.isBlank(subjectId)) {
            return new ResponseEntity<>("Subject id cannot be empty", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isBlank(campaignName)) {
            return new ResponseEntity<>("Campaign name cannot be empty", HttpStatus.BAD_REQUEST);
        }

        try {
            ebodacEnrollmentService.enrollSubjectToCampaign(subjectId, campaignName);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/unenrollCampaign/{subjectId}/{campaignName}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> unenrollCampaign(@PathVariable String subjectId, @PathVariable String campaignName) throws IOException {

        if (StringUtils.isBlank(subjectId)) {
            return new ResponseEntity<>("Subject id cannot be empty", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isBlank(campaignName)) {
            return new ResponseEntity<>("Campaign name cannot be empty", HttpStatus.BAD_REQUEST);
        }

        try {
            ebodacEnrollmentService.unenrollSubject(subjectId, campaignName);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/enrollCampaignWithNewDate/{subjectId}/{campaignName}/{date}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> enrollCampaignWithNewDate(@PathVariable String subjectId, @PathVariable String campaignName,
                                                            @PathVariable String date) throws IOException {

        if (StringUtils.isBlank(subjectId)) {
            return new ResponseEntity<>("Subject id cannot be empty", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isBlank(campaignName)) {
            return new ResponseEntity<>("Campaign name cannot be empty", HttpStatus.BAD_REQUEST);
        }

        try {
            LocalDate referenceDate = LocalDate.parse(date, DateTimeFormat.forPattern(EbodacConstants.ENROLLMENT_DATE_FORMAT));
            ebodacEnrollmentService.enrollSubjectToCampaignWithNewDate(subjectId, campaignName, referenceDate);
            updateVisit(subjectId, campaignName, referenceDate);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('manageEbodac')")
    @RequestMapping(value = "/reenrollCampaign/{subjectId}/{campaignName}/{date}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> reenrollCampaign(@PathVariable String subjectId, @PathVariable String campaignName,
                                                   @PathVariable String date) throws IOException {

        if (StringUtils.isBlank(subjectId)) {
            return new ResponseEntity<>("Subject id cannot be empty", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isBlank(campaignName)) {
            return new ResponseEntity<>("Campaign name cannot be empty", HttpStatus.BAD_REQUEST);
        }

        try {
            LocalDate referenceDate = LocalDate.parse(date, DateTimeFormat.forPattern(EbodacConstants.ENROLLMENT_DATE_FORMAT));
            ebodacEnrollmentService.reenrollSubjectWithNewDate(subjectId, campaignName, referenceDate);
            updateVisit(subjectId, campaignName, referenceDate);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EbodacEnrollmentException e) {
            LOGGER.debug(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void updateVisit(String subjectId, String campaignName, LocalDate date) {
        if (!EbodacConstants.BOOSTER_RELATED_MESSAGES.equals(campaignName)) {
            VisitType visitType = null;
            if (campaignName.startsWith(VisitType.BOOST_VACCINATION_DAY.getValue())) {
                visitType = VisitType.BOOST_VACCINATION_DAY;
            } else {
                visitType = VisitType.getByValue(campaignName);
            }
            if (visitType != null) {
                Visit visit = visitService.findVisitBySubjectIdAndVisitType(subjectId, visitType);
                if (visit != null) {
                    visit.setMotechProjectedDate(date);
                    visitService.update(visit);
                }
            }
        }
    }
}
