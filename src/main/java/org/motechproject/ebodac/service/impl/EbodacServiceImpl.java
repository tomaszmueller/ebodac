package org.motechproject.ebodac.service.impl;

import org.motechproject.ebodac.service.EbodacService;
import org.springframework.stereotype.Service;

/**
 * Simple implementation of the {@link org.motechproject.ebodac.service.EbodacService} interface.
 */
@Service("ebodacService")
public class EbodacServiceImpl implements EbodacService {

    @Override
    public String sayHello() {
        return "Hello World";
    }

}
