package org.motechproject.ebodac.service.impl;


import org.apache.commons.lang.StringUtils;
import org.motechproject.ebodac.domain.Gender;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.service.RaveImportService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.ebodac.service.VisitService;
import org.motechproject.ebodac.service.impl.csv.RaveSubjectField;
import org.motechproject.ebodac.service.impl.csv.RaveVisitField;
import org.motechproject.mds.ex.csv.CsvImportException;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Service("raveImportService")
public class RaveImportServiceImpl implements RaveImportService {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private VisitService visitService;

    @Override
    public void importCsv(Reader reader) {
        try (CsvMapReader csvMapReader = new CsvMapReader(reader, CsvPreference.STANDARD_PREFERENCE)) {
            Map<String, String> row;
            final String headers[] = csvMapReader.getHeader(true);

            while ((row = csvMapReader.read(headers)) != null) {
                importRow(row);
            }
        } catch (IOException e) {
            throw new CsvImportException("IO Error when importing CSV", e);
        }
    }

    private void importRow(Map<String, String> row) {
        Subject subject = new Subject();
        if (row.containsKey(RaveSubjectField.Subject.name())) {
            Subject existingSubject = subjectService.findSubjectBySubjectId(row.get(RaveSubjectField.Subject.name()));
            if (existingSubject != null) {
                subject = existingSubject;
            }
        }
        for (RaveSubjectField subjectField : RaveSubjectField.values()) {
            String header = subjectField.name();
            String fieldName = subjectField.getValue();
            String csvValue = getValue(row, header);
            if (subjectField.equals(RaveSubjectField.SEX_STD)) {
                Gender gender = Gender.getByValue(csvValue);
                subject.setGender(gender);
            } else {
                setProperty(subject, fieldName, csvValue);
            }
        }
        Visit visit = new Visit();
        for (RaveVisitField visitField : RaveVisitField.values()) {
            String header = visitField.name();
            String fieldName = visitField.getValue();
            String csvValue = getValue(row, header);
            if (visitField.equals(RaveVisitField.Visit)) {
                VisitType visitType = VisitType.getByValue(csvValue);
                visit.setType(visitType);
            } else {
                setProperty(visit, fieldName, csvValue);
            }
        }
        Subject updatedSubject = subjectService.createOrUpdate(subject);
        if (updatedSubject != null) {
            visit.setSubject(updatedSubject);
            visitService.create(visit);
        } else {
            throw new CsvImportException("Could not store subject imported from row " + row);
        }
    }

    private void setProperty(Object o, String fieldName, String csvValue) {
        try {
            Field f = o.getClass().getDeclaredField(fieldName);
            Object parsedValue = TypeHelper.parse(csvValue, f.getType());
            PropertyUtil.setProperty(o, StringUtils.uncapitalize(f.getName()), parsedValue);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            String msg = String.format("Error when processing field: %s, value in CSV file is %s",
                    fieldName, csvValue);
            throw new CsvImportException(msg, e);
        }
    }

    private String getValue(Map<String, String> row, String header) {
        if (!row.containsKey(header)) {
            throw new CsvImportException("The row " + row + " does not contain required " + header + " property.");
        }
        return row.get(header);
    }

}
