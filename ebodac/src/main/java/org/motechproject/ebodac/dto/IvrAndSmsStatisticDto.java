package org.motechproject.ebodac.dto;

import java.sql.Date;
import java.util.List;

public class IvrAndSmsStatisticDto {

    private Date date;

    private Long totalAmount;

    private Long totalPending;

    private Long totalFailed;

    private Long totalSucceed;

    private Long sendToMen;

    private Long sendToWomen;

    private Long successfulSendToMen;

    private Long successfulSendToWomen;

    public IvrAndSmsStatisticDto() {
    }

    public IvrAndSmsStatisticDto(List<IvrAndSmsStatisticDto> statisticList) {
        totalAmount = 0L;
        totalPending = 0L;
        totalFailed = 0L;
        totalSucceed = 0L;
        sendToMen = 0L;
        sendToWomen = 0L;
        successfulSendToMen = 0L;
        successfulSendToWomen = 0L;

        if (statisticList != null) {
            for (IvrAndSmsStatisticDto statistic : statisticList) {
                totalAmount += statistic.getTotalAmount();
                totalPending += statistic.getTotalPending();
                totalFailed += statistic.getTotalFailed();
                totalSucceed += statistic.getTotalSucceed();
                sendToMen += statistic.getSendToMen();
                sendToWomen += statistic.getSendToWomen();
                successfulSendToMen += statistic.getSuccessfulSendToMen();
                successfulSendToWomen += statistic.getSuccessfulSendToWomen();
            }
        }
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public Long getSuccessfulSendToMen() {
        if (successfulSendToMen == null) {
            return 0L;
        }
        return successfulSendToMen;
    }

    public void setSuccessfulSendToMen(Long successfulSendToMen) {
        this.successfulSendToMen = successfulSendToMen;
    }

    public Long getSuccessfulSendToWomen() {
        if (successfulSendToWomen == null) {
            return 0L;
        }
        return successfulSendToWomen;
    }

    public void setSuccessfulSendToWomen(Long successfulSentToWomen) {
        this.successfulSendToWomen = successfulSentToWomen;
    }
}
