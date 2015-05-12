package org.motechproject.ebodac.service.impl;

import org.apache.commons.httpclient.HttpStatus;
import org.joda.time.DateTime;
import org.motechproject.ebodac.client.EbodacHttpClient;
import org.motechproject.ebodac.client.JsonResponse;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.service.EbodacService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.ebodac.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Simple implementation of the {@link org.motechproject.ebodac.service.EbodacService} interface.
 */
@Service("ebodacService")
public class EbodacServiceImpl implements EbodacService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EbodacServiceImpl.class);

    private SubjectService subjectService;

    private EbodacHttpClient ebodacHttpClient;

    @Autowired
    public EbodacServiceImpl(SubjectService subjectService, EbodacHttpClient ebodacHttpClient) {
        this.subjectService = subjectService;
        this.ebodacHttpClient = ebodacHttpClient;
    }

    @Override
    public void sendUpdatedSubjects(String zetesUrl, String username, String password) {
        LOGGER.info("Sending updated subjects to zetes. Job started at {}", DateTime.now());

        List<Subject> modifiedSubjects = subjectService.findModifiedSubjects();
        for (Subject s : modifiedSubjects) {
            String json = JsonUtils.convertSubjectForZetes(s);
            if (json != null) {
                JsonResponse response = ebodacHttpClient.sendJson(zetesUrl, json, username, password);
                if (response == null) {
                    LOGGER.error("Skipping subject due to HttpClient failure. Subject id: {}", s.getSubjectId());
                } else if (response.getStatus() != HttpStatus.SC_OK) {
                    LOGGER.error("Failed to update the subject. Subject id: {}, response from Zetes (status {}):\n{}",
                            s.getSubjectId(), response.getStatus(), response.getJson());
                } else {
                    // the subject has been updated successfully
                    s.setChanged(false);
                    subjectService.update(s, true);
                }
            } else {
                LOGGER.error("Skipping subject due to json processing exception. Subject id: {}", s.getSubjectId());
            }
        }
        LOGGER.info("Zetes update job finished at {}", DateTime.now());
    }

}
