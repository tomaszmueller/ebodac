package org.motechproject.ebodac.service.impl.csv;

import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.mds.service.DefaultCsvExportCustomizer;
import org.motechproject.mds.util.PropertyUtil;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class VisitCsvExportCustomizer extends DefaultCsvExportCustomizer {

    @Override
    public String formatRelationship(Object object) {
        if (object instanceof Collection) {
            int i = 0;
            StringBuilder sb = new StringBuilder();
            for (Object item : (Collection) object) {
                if (i++ != 0) {
                    sb.append(',');
                }
                sb.append(PropertyUtil.safeGetProperty(item, EbodacConstants.SUBJECT_ID_FIELD_NAME));
            }
            return sb.toString();
        } else if (object != null) {
            return String.valueOf(PropertyUtil.safeGetProperty(object, EbodacConstants.SUBJECT_ID_FIELD_NAME));
        } else {
            return "";
        }
    }
}
