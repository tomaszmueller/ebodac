package org.motechproject.ebodac.domain;

import java.util.Arrays;
import java.util.List;

public class IvrAndSmsStatisticTablesDto {

    private List<String> headers = Arrays.asList("date", "totalAmount", "totalPending", "totalFailed", "totalSucceed",
            "sendToMen", "sendToWomen", "successfulSendToMen", "successfulSendToWomen");

    private List<IvrAndSmsStatistic> data;

    private IvrAndSmsStatistic dataSum;

    private String sumHeader = "date";

    public IvrAndSmsStatisticTablesDto() {
    }

    public IvrAndSmsStatisticTablesDto(List<IvrAndSmsStatistic> data) {
        this.data = data;
        this.dataSum = new IvrAndSmsStatistic(data);
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public List<IvrAndSmsStatistic> getData() {
        return data;
    }

    public void setData(List<IvrAndSmsStatistic> data) {
        this.data = data;
    }

    public IvrAndSmsStatistic getDataSum() {
        return dataSum;
    }

    public void setDataSum(IvrAndSmsStatistic dataSum) {
        this.dataSum = dataSum;
    }

    public String getSumHeader() {
        return sumHeader;
    }

    public void setSumHeader(String sumHeader) {
        this.sumHeader = sumHeader;
    }
}
