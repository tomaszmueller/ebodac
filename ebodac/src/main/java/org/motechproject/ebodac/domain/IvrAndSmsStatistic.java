package org.motechproject.ebodac.domain;

public class IvrAndSmsStatistic {

    private Long totalAmount;

    private Long totalPending;

    private Long totalFailed;

    private Long totalSucceed;

    private Long sendToMen;

    private Long sendToWomen;

    public IvrAndSmsStatistic() {
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getTotalPending() {
        return totalPending;
    }

    public void setTotalPending(Long totalPending) {
        this.totalPending = totalPending;
    }

    public Long getTotalFailed() {
        return totalFailed;
    }

    public void setTotalFailed(Long totalFailed) {
        this.totalFailed = totalFailed;
    }

    public Long getTotalSucceed() {
        return totalSucceed;
    }

    public void setTotalSucceed(Long totalSucceed) {
        this.totalSucceed = totalSucceed;
    }

    public Long getSendToMen() {
        return sendToMen;
    }

    public void setSendToMen(Long sendToMen) {
        this.sendToMen = sendToMen;
    }

    public Long getSendToWomen() {
        return sendToWomen;
    }

    public void setSendToWomen(Long sendToWomen) {
        this.sendToWomen = sendToWomen;
    }
}
