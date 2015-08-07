package org.motechproject.ebodac.service.impl;

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
import java.util.HashMap;
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
        if (lookup != null && lookup != "" && queryParams != null) {
            Map<Class<?>, String> mappedFields = getlookupFieldsMappedByType(lookupFields);

            if(mappedFields.size() == 0) {
                return getEntitiesWithoutLookup(dataService, queryParams);
            }

            Method lookupMethod = dataService.getClass().getMethod(getMethodNameFromLookup(lookup),
                    buildTypesArrayWithQuerryParams(mappedFields, queryParams));

            Method countMethod = dataService.getClass().getMethod(getCountMethodNameFromLookup(lookup),
                    buildTypesArray(mappedFields));

            entities = (List<T>) lookupMethod.invoke(dataService, buildParamsArrayWithQuerryParams(mappedFields, queryParams));
            recordCount = (long) countMethod.invoke(dataService, buildParamsArray(mappedFields));
            rowCount = (int) Math.ceil(recordCount / (double) queryParams.getPageSize());

            return new Records<>(queryParams.getPage(), rowCount, (int) recordCount, entities);
        }

        return getEntitiesWithoutLookup(dataService, queryParams);
    }

    private <T> Records<T> getEntitiesWithoutLookup(MotechDataService<T> dataService, QueryParams queryParams)
            throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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

    private Object[] buildParamsArrayWithQuerryParams(Map<Class<?>, String> mappedFields, QueryParams queryParams) throws IOException {
        Object[] params = new Object[mappedFields.size() + 1];
        int i = 0;
        for(Map.Entry<Class<?>, String> entry : mappedFields.entrySet()) {
            params[i] = getFieldValueFromString(entry.getKey(),entry.getValue());
            i++;
        }
        params[i] = queryParams;
        return params;
    }

    private Class<?>[] buildTypesArrayWithQuerryParams(Map<Class<?>, String> mappedFields, QueryParams queryParams) {
        Set<Class<?>> typesSet = mappedFields.keySet();
        Class<?>[] types = new Class<?>[typesSet.size() + 1];
        int i = 0;
        for(Class<?> type : typesSet) {
            types[i] = type;
            i++;
        }
        types[i] = QueryParams.class;
        return types;
    }

    private Object[] buildParamsArray(Map<Class<?>, String> mappedFields) throws IOException {
        Object[] params = new Object[mappedFields.size()];
        int i = 0;
        for(Map.Entry<Class<?>, String> entry : mappedFields.entrySet()) {
            params[i] = getFieldValueFromString(entry.getKey(),entry.getValue());
            i++;
        }
        return params;
    }

    private Class<?>[] buildTypesArray(Map<Class<?>, String> mappedFields) {
        Set<Class<?>> typesSet = mappedFields.keySet();
        Class<?>[] types = new Class<?>[typesSet.size()];
        int i = 0;
        for(Class<?> type : typesSet) {
            types[i] = type;
            i++;
        }
        return types;
    }

    private Map<Class<?>, String> getlookupFieldsMappedByType(String lookupFields) throws IOException {
        Map<Class<?>, String> ret = new HashMap<>();
        Map<String, Object> fields = getFields(lookupFields);
        for(Map.Entry<String, Object> entry : fields.entrySet()) {
            if (entry.getKey().equals("Date")) {
                ret.put(LocalDate.class, (String) entry.getValue());
            } else if(entry.getKey().equals("Visit Type")) {
                ret.put(VisitType.class, (String) entry.getValue());
            } else if(entry.getKey().equals("Date Range")) {
                Map<String, Object> rangeMap = (Map<String, Object>) entry.getValue();
                String stringValue =objectMapper.writeValueAsString(rangeMap);
                ret.put(Range.class, stringValue);
            } else {
                ret.put(String.class, (String) entry.getValue());
            }
        }
        return ret;
    }

    private <T> T getFieldValueFromString(Class<T> fieldType, String value) throws IOException {
        DateTimeFormatter lookupDateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
        if(fieldType.equals(LocalDate.class)) {
            return (T) LocalDate.parse(value, lookupDateTimeFormat);
        } else if(fieldType.equals(VisitType.class)) {
            return (T) VisitType.getByValue(value);
        } else if(fieldType.equals(Range.class)) {
            Map<String, Object> rangeMap = getFields(value);
            LocalDate min = LocalDate.parse((String) rangeMap.get("min"), lookupDateTimeFormat);
            LocalDate max = LocalDate.parse((String) rangeMap.get("max"), lookupDateTimeFormat);
            return (T) new Range<>(min, max);
        } else {
            return (T) value;
        }
    }

    private Map<String, Object> getFields(String lookupFields) throws IOException {
        return objectMapper.readValue(lookupFields, new TypeReference<HashMap>() {});
    }
}
