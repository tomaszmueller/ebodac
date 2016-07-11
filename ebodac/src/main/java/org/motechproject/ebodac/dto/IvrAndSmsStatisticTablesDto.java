package org.motechproject.ebodac.dto;

import java.util.Arrays;
import java.util.List;

public class IvrAndSmsStatisticTablesDto {

    private List<String> headers = Arrays.asList("date", "totalAmount", "totalPending", "totalFailed", "totalSucceed",
            "sendToMen", "sendToWomen", "successfulSendToMen", "successfulSendToWomen");

    private List<IvrAndSmsStatisticDto> data;

    private IvrAndSmsStatisticDto dataSum;

    private String sumHeader = "date";

    public IvrAndSmsStatisticTablesDto() {
    }

    public IvrAndSmsStatisticTablesDto(List<IvrAndSmsStatisticDto> data) {
        this.data = data;
        this.dataSum = new IvrAndSmsStatisticDto(data);
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public List<IvrAndSmsStatisticDto> getData() {
        return data;
    }

    public void setData(List<IvrAndSmsStatisticDto> data) {
        this.data = data;
    }

    public IvrAndSmsStatisticDto getDataSum() {
        return dataSum;
    }

    public void setDataSum(IvrAndSmsStatisticDto dataSum) {
        this.dataSum = dataSum;
    }

    public String getSumHeader() {
        return sumHeader;
    }

    public void setSumHeader(String sumHeader) {
        this.sumHeader = sumHeader;
    }
}
