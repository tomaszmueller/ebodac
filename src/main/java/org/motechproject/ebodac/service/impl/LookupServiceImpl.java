package org.motechproject.ebodac.service.impl;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.MotechDataService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("lookupService")
public class LookupServiceImpl implements LookupService {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> Records<T> getEntities(MotechDataService<T> dataService, String lookup,
                                      String lookupFields, QueryParams queryParams)
            throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<T> entities;
        long recordCount;
        int rowCount;
        if (StringUtils.isNotBlank(lookup) && queryParams != null) {
            Map<Class<?>, Object> mappedFields = getLookupFieldsMappedByType(lookupFields);

            if(mappedFields.size() == 0) {
                return getEntitiesWithoutLookup(dataService, queryParams);
            }

            List<Class<?>> typesList = buildTypesList(mappedFields);
            List<Object> paramsList = buildParamsList(mappedFields);

            Method countMethod = dataService.getClass().getMethod(getCountMethodNameFromLookup(lookup),
                    typesListToArray(typesList));
            typesList.add(QueryParams.class);
            Method lookupMethod = dataService.getClass().getMethod(getMethodNameFromLookup(lookup),
                    typesListToArray(typesList));

            recordCount = (long) countMethod.invoke(dataService, paramsList.toArray());
            paramsList.add(queryParams);
            entities = (List<T>) lookupMethod.invoke(dataService, paramsList.toArray());
            if (queryParams.getPageSize() != null) {
                rowCount = (int) Math.ceil(recordCount / (double) queryParams.getPageSize());
            } else {
                rowCount = (int) recordCount;
            }

            return new Records<>(queryParams.getPage(), rowCount, (int) recordCount, entities);
        }

        return getEntitiesWithoutLookup(dataService, queryParams);
    }

    private <T> Records<T> getEntitiesWithoutLookup(MotechDataService<T> dataService, QueryParams queryParams) {
        List<T> entities;
        long recordCount;
        int rowCount;

        recordCount = dataService.count();
        int page;
        if(queryParams.getPageSize() != null && queryParams.getPage() != null) {
            rowCount = (int) Math.ceil(recordCount / (double) queryParams.getPageSize());
            page = queryParams.getPage();
            entities = dataService.retrieveAll(queryParams);
        } else {
            rowCount = (int) recordCount;
            page = 1;
            entities = dataService.retrieveAll(queryParams);
        }
        return new Records<>(page, rowCount, (int) recordCount, entities);
    }

    private String getCountMethodNameFromLookup(String lookup) {
        String methodName = lookup;
        methodName = methodName.replaceAll("\\s", "");
        return "count" + methodName;
    }

    private String getMethodNameFromLookup(String lookup) {
        String methodName = lookup;
        methodName = methodName.replaceAll("\\s", "");
        char c[] = methodName.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }

    private Class<?>[] typesListToArray(List<Class<?>> typesList) {
        Class<?>[] ret = new Class<?>[typesList.size()];
        int i = 0;
        for(Class<?> type : typesList) {
            ret[i] = type;
            i++;
        }
        return ret;
    }

    private List<Object> buildParamsList(Map<Class<?>, Object> mappedFields) throws IOException {
        List<Object> params = new ArrayList<>();
        for(Map.Entry<Class<?>, Object> entry : mappedFields.entrySet()) {
            params.add(entry.getValue());
        }
        return params;
    }

    private List<Class<?>> buildTypesList(Map<Class<?>, Object> mappedFields) {
        Set<Class<?>> typesSet = mappedFields.keySet();
        List<Class<?>> types = new ArrayList<>();
        for(Class<?> type : typesSet) {
            types.add(type);
        }
        return types;
    }

    private Map<Class<?>, Object> getLookupFieldsMappedByType(String lookupFields) throws IOException {
        DateTimeFormatter lookupDateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
        Map<Class<?>, Object> ret = new LinkedHashMap<>();
        Map<String, Object> fields = getFields(lookupFields);
        for(Map.Entry<String, Object> entry : fields.entrySet()) {
            switch (entry.getKey()) {
                case "Date":
                    ret.put(LocalDate.class, LocalDate.parse((String) entry.getValue(), lookupDateTimeFormat));
                    break;
                case "Visit Type":
                    ret.put(VisitType.class, VisitType.getByValue((String) entry.getValue()));
                    break;
                case "Date Range":
                    Map<String, Object> rangeMap = (Map<String, Object>) entry.getValue();
                    LocalDate min = LocalDate.parse((String) rangeMap.get("min"), lookupDateTimeFormat);
                    LocalDate max = LocalDate.parse((String) rangeMap.get("max"), lookupDateTimeFormat);
                    ret.put(Range.class, new Range<>(min, max));
                    break;
                default:
                    ret.put(String.class, entry.getValue());
                    break;
            }
        }
        return ret;
    }

    private Map<String, Object> getFields(String lookupFields) throws IOException {
        return objectMapper.readValue(lookupFields, new TypeReference<HashMap>() {});
    }
}
