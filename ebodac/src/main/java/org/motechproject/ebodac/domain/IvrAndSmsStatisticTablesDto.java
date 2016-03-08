package org.motechproject.ebodac.domain;

import java.util.Arrays;
import java.util.List;

public class IvrAndSmsStatisticTablesDto {

    private List<String> headers = Arrays.asList("totalAmount", "totalPending", "totalFailed", "totalSucceed", "sendToMen", "sendToWomen");

    private IvrAndSmsStatistic data;

    public IvrAndSmsStatisticTablesDto() {
    }

    public IvrAndSmsStatisticTablesDto(IvrAndSmsStatistic data) {
        this.data = data;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public IvrAndSmsStatistic getData() {
        return data;
    }

    public void setData(IvrAndSmsStatistic data) {
        this.data = data;
    }
}
