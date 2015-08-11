package org.motechproject.ebodac.service.impl;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.MDSLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("lookupService")
public class LookupServiceImpl implements LookupService {

    @Autowired
    private EntityService entityService;

    @Autowired
    private MDSLookupService mdsLookupService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> Records<T> getEntities(Class<T> entityType, String lookup,
                                      String lookupFields, QueryParams queryParams) throws IOException {
        List<T> entities;
        long recordCount;
        int rowCount;
        if (StringUtils.isNotBlank(lookup) && queryParams != null) {

            entities = mdsLookupService.findMany(entityType.getName(), lookup, getFields(lookupFields), queryParams);
            recordCount = mdsLookupService.count(entityType.getName(), lookup, getFields(lookupFields));

            if (queryParams.getPageSize() != null) {
                rowCount = (int) Math.ceil(recordCount / (double) queryParams.getPageSize());
            } else {
                rowCount = (int) recordCount;
            }

            return new Records<>(queryParams.getPage(), rowCount, (int) recordCount, entities);
        }

        recordCount = mdsLookupService.countAll(entityType.getName());

        int page;
        if(queryParams.getPageSize() != null && queryParams.getPage() != null) {
            rowCount = (int) Math.ceil(recordCount / (double) queryParams.getPageSize());
            page = queryParams.getPage();
            entities = mdsLookupService.retrieveAll(entityType.getName(), queryParams);
        } else {
            rowCount = (int) recordCount;
            page = 1;
            entities = mdsLookupService.retrieveAll(entityType.getName(), queryParams);
        }
        return new Records<>(page, rowCount, (int) recordCount, entities);
    }

    @Override
    public List<LookupDto> getAvailableLookups(String entityName) {
        EntityDto entity = getEntityByEntityClassName(entityName);
        AdvancedSettingsDto settingsDto = entityService.getAdvancedSettings(entity.getId(), true);
        return settingsDto.getIndexes();
    }

    private EntityDto getEntityByEntityClassName(String entityName) {
        List<EntityDto> entities = entityService.listEntities();
        String moduleName = EbodacConstants.EBODAC_MODULE;

        for (EntityDto entity : entities) {
            if (entity.getModule() != null && entity.getModule().equals(moduleName) &&
                    entity.getName().equals(entityName)) {
                return entity;
            }
        }
        return null;
    }

    private Map<String, Object> getFields(String lookupFields) throws IOException {
        return objectMapper.readValue(lookupFields, new TypeReference<HashMap>() {});
    }
}
