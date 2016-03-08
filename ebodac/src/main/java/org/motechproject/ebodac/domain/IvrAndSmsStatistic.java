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
        if (totalAmount == null) {
            return 0L;
        }
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getTotalPending() {
        if (totalPending == null) {
            return 0L;
        }
        return totalPending;
    }

    public void setTotalPending(Long totalPending) {
        this.totalPending = totalPending;
    }

    public Long getTotalFailed() {
        if (totalFailed == null) {
            return 0L;
        }
        return totalFailed;
    }

    public void setTotalFailed(Long totalFailed) {
        this.totalFailed = totalFailed;
    }

    public Long getTotalSucceed() {
        if (totalSucceed == null) {
            return 0L;
        }
        return totalSucceed;
    }

    public void setTotalSucceed(Long totalSucceed) {
        this.totalSucceed = totalSucceed;
    }

    public Long getSendToMen() {
        if (sendToMen == null) {
            return 0L;
        }
        return sendToMen;
    }

    public void setSendToMen(Long sendToMen) {
        this.sendToMen = sendToMen;
    }

    public Long getSendToWomen() {
        if (sendToWomen == null) {
            return 0L;
        }
        return sendToWomen;
    }

    public void setSendToWomen(Long sendToWomen) {
        this.sendToWomen = sendToWomen;
    }
}
