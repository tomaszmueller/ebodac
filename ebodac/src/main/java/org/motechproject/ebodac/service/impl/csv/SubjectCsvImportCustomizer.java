package org.motechproject.ebodac.service.impl.csv;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.mds.service.DefaultCsvImportCustomizer;
import org.motechproject.mds.service.MotechDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SubjectCsvImportCustomizer extends DefaultCsvImportCustomizer {

    private SubjectService subjectService;

    private EbodacEnrollmentService ebodacEnrollmentService;

    private Subject oldSubject;

    @Override
    public Object findExistingInstance(Map<String, String> row, MotechDataService motechDataService) {
        oldSubject = null;
        String subjectId = row.get(EbodacConstants.SUBJECT_ID_FIELD_NAME);

        if (StringUtils.isNotBlank(subjectId)) {
            Subject subject = subjectService.findSubjectBySubjectId(subjectId);
            if (subject != null) {
                oldSubject = new Subject(subject);
            }
            return subject;
        }

        subjectId = row.get(EbodacConstants.SUBJECT_ID_FIELD_DISPLAY_NAME);

        if (StringUtils.isNotBlank(subjectId)) {
            Subject subject = subjectService.findSubjectBySubjectId(subjectId);
            if (subject != null) {
                oldSubject = new Subject(subject);
            }
            return subject;
        }

        return null;
    }

    @Override
    public Object doCreate(Object instance, MotechDataService motechDataService) {
        return subjectService.create((Subject) instance);
    }

    @Override
    public Object doUpdate(Object instance, MotechDataService motechDataService) {
        if (oldSubject != null && oldSubject.getSubjectId().equals(((Subject) instance).getSubjectId())) {
            ebodacEnrollmentService.updateEnrollmentsWhenSubjectDataChanged((Subject) instance, oldSubject, true);
        }
        return subjectService.update((Subject) instance);
    }

    public SubjectService getSubjectService() {
        return subjectService;
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @Autowired
    public void setEbodacEnrollmentService(EbodacEnrollmentService ebodacEnrollmentService) {
        this.ebodacEnrollmentService = ebodacEnrollmentService;
    }
}
