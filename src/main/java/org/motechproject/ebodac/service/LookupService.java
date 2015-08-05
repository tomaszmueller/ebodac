package org.motechproject.ebodac.service;


import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.MotechDataService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface LookupService {
    
    <T> Records<T> getEntities(MotechDataService<T> dataService, String lookup,
                               String lookupFields, QueryParams queryParams) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException;
}
