package org.motechproject.ebodac.scheduler;

import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.service.EbodacService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EbodacEventListener {

    private EbodacService ebodacService;

    @Autowired
    public EbodacEventListener(EbodacService ebodacService) {
        this.ebodacService = ebodacService;
    }

    @MotechListener(subjects = {EbodacConstants.ZETES_UPDATE_EVENT})
    public void zetesUpdate(MotechEvent event) {
        Object zetesUrl = event.getParameters().get(EbodacConstants.ZETES_URL);
        Object username = event.getParameters().get(EbodacConstants.ZETES_USERNAME);
        Object password = event.getParameters().get(EbodacConstants.ZETES_PASSWORD);
        ebodacService.sendUpdatedSubjects(zetesUrl.toString(), username.toString(), password.toString());
    }
}
