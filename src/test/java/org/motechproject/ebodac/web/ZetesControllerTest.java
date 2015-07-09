package org.motechproject.ebodac.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.service.SubjectService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


public class ZetesControllerTest {

    @Mock
    private SubjectService subjectService;

    private ZetesController zetesController;

    @Before
    public void setUp(){
        initMocks(this);
        zetesController=new ZetesController();
        zetesController.subjectService = subjectService;
    }

    @Test
    public void shouldGenerateSubjectFromZetes()
    {
        SubmitSubjectRequest submitSubjectRequest = new SubmitSubjectRequest("123456789", "Kasia",
                "Kowalska", "123", "Warszawa 19", "eng", "community", "Nowak");

        zetesController.submitSubjectRequest(submitSubjectRequest);

        Subject subject = new Subject("123", "Kasia", "Kowalska",
                "Nowak", "123456789", "Warszawa 19", Language.English, "community", "");

        verify(subjectService).createOrUpdateForZetes(subject);
    }

}
