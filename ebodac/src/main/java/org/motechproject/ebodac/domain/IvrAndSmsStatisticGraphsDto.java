package org.motechproject.ebodac.domain;

import java.util.Arrays;
import java.util.List;

public class IvrAndSmsStatisticGraphsDto {

    private List<String> graphs = Arrays.asList("statusGraph", "genderGraph");

    private List<List<String>> headers = Arrays.asList(Arrays.asList("totalPending", "totalFailed", "totalSucceed"),
            Arrays.asList("sendToMen", "sendToWomen"));

    private IvrAndSmsStatistic data;

    public IvrAndSmsStatisticGraphsDto() {
    }

    public IvrAndSmsStatisticGraphsDto(IvrAndSmsStatistic data) {
        this.data = data;
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

    public IvrAndSmsStatistic getData() {
        return data;
    }

    public void setData(IvrAndSmsStatistic data) {
        this.data = data;
    }
}
