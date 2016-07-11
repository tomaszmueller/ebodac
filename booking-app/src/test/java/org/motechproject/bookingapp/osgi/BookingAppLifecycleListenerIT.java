package org.motechproject.bookingapp.osgi;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.bookingapp.constants.BookingAppConstants;
import org.motechproject.bookingapp.domain.SubjectBookingDetails;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.repository.SubjectBookingDetailsDataService;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.ebodac.domain.enums.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.enums.VisitType;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class BookingAppLifecycleListenerIT extends BasePaxIT {

    @Inject
    private SubjectDataService subjectDataService;

    @Inject
    private VisitDataService visitDataService;

    @Inject
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Inject
    private SubjectBookingDetailsDataService subjectBookingDetailsDataService;

    private DateTimeFormatter formatter = DateTimeFormat.forPattern(BookingAppConstants.SIMPLE_DATE_FORMAT);

    @Before
    public void cleanBefore() {
        visitBookingDetailsDataService.deleteAll();
        subjectBookingDetailsDataService.deleteAll();
        visitDataService.deleteAll();
        subjectDataService.deleteAll();
    }

    @After
    public void cleanAfter() {
        visitBookingDetailsDataService.deleteAll();
        subjectBookingDetailsDataService.deleteAll();
        visitDataService.deleteAll();
        subjectDataService.deleteAll();
    }

    @Test
    public void shouldCreateVisitBookingDetails() {
        List<VisitBookingDetails> visitBookingDetailsList = visitBookingDetailsDataService.retrieveAll();
        List<SubjectBookingDetails> subjectBookingDetailsList = subjectBookingDetailsDataService.retrieveAll();
        assertEquals(0, visitBookingDetailsList.size());
        assertEquals(0, subjectBookingDetailsList.size());

        Subject firstSubject = new Subject("1000000161", "Michal", "Abacki", "Cabacki",
                "729402018364", "address", Language.English, "community", "B05-SL10001", "siteName", "chiefdom", "section", "district");

        Subject secondSubject = new Subject("1000000162", "Rafal", "Dabacki", "Ebacki",
                "44443333222", "address", Language.Susu, "community", "B05-SL10001", "siteName", "chiefdom", "section", "district");

        Visit firstVisit = createVisit(secondSubject, VisitType.SCREENING, LocalDate.parse("2014-10-17", formatter),
                LocalDate.parse("2014-10-18", formatter));

        Visit secondVisit = createVisit(firstSubject, VisitType.PRIME_VACCINATION_FIRST_FOLLOW_UP_VISIT, LocalDate.parse("2014-10-19", formatter),
                LocalDate.parse("2014-10-20", formatter));

        Visit thirdVisit = createVisit(firstSubject, VisitType.FIRST_LONG_TERM_FOLLOW_UP_VISIT, LocalDate.parse("2014-10-19", formatter),
                LocalDate.parse("2014-10-20", formatter));

        subjectDataService.create(firstSubject);
        subjectDataService.create(secondSubject);
        visitDataService.create(firstVisit);
        visitDataService.create(secondVisit);
        visitDataService.create(thirdVisit);

        visitBookingDetailsList = visitBookingDetailsDataService.retrieveAll();
        subjectBookingDetailsList = subjectBookingDetailsDataService.retrieveAll();

        assertEquals(3, visitBookingDetailsList.size());
        assertEquals(2, subjectBookingDetailsList.size());
    }

    private Visit createVisit(Subject subject, VisitType visitType, LocalDate date, LocalDate projectedDate) {
        Visit visit = new Visit();
        visit.setSubject(subject);
        visit.setType(visitType);
        visit.setDate(date);
        visit.setDateProjected(projectedDate);
        visit.setMotechProjectedDate(projectedDate);
        return visit;
    }
}
