package org.motechproject.ebodac.dto;

import java.util.Arrays;
import java.util.List;

public class IvrAndSmsStatisticGraphsDto {

    private List<String> graphs = Arrays.asList("statusGraph", "genderGraph", "successfulGenderGraph");

    private List<List<String>> headers = Arrays.asList(Arrays.asList("totalPending", "totalFailed", "totalSucceed"),
            Arrays.asList("sendToMen", "sendToWomen"), Arrays.asList("successfulSendToMen", "successfulSendToWomen"));

    private List<IvrAndSmsStatisticDto> data;

    private IvrAndSmsStatisticDto dataSum;

    private String sumHeader = "date";

    public IvrAndSmsStatisticGraphsDto() {
    }

    public IvrAndSmsStatisticGraphsDto(List<IvrAndSmsStatisticDto> data) {
        this.data = data;
        this.dataSum = new IvrAndSmsStatisticDto(data);
    }

    public List<String> getGraphs() {
        return graphs;
    }

    public void setGraphs(List<String> graphs) {
        this.graphs = graphs;
    }

    public List<List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(List<List<String>> headers) {
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
