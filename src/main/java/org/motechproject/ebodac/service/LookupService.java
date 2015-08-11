package org.motechproject.ebodac.service;


import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.query.QueryParams;

import java.io.IOException;
import java.util.List;

public interface LookupService {
    
    <T> Records<T> getEntities(Class<T> entityType, String lookup,
                               String lookupFields, QueryParams queryParams) throws IOException;

    List<LookupDto> getAvailableLookups(String entityName);
}
