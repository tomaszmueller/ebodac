package org.motechproject.ebodac.dto;

public class IvrEngagementStatisticDto {

    private String subjectId;

    private Long callsExpected;

    private Long pushedSuccessfully;

    private Long received;

    private Long activelyListened;

    private Long failed;

    public IvrEngagementStatisticDto() {
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public Long getCallsExpected() {
        return callsExpected;
    }

    public void setCallsExpected(Long callsExpected) {
        this.callsExpected = callsExpected;
    }

    public Long getPushedSuccessfully() {
        return pushedSuccessfully;
    }

    public void setPushedSuccessfully(Long pushedSuccessfully) {
        this.pushedSuccessfully = pushedSuccessfully;
    }

    public Long getReceived() {
        return received;
    }

    public void setReceived(Long received) {
        this.received = received;
    }

    public Long getActivelyListened() {
        return activelyListened;
    }

    public void setActivelyListened(Long activelyListened) {
        this.activelyListened = activelyListened;
    }

    public Long getFailed() {
        return failed;
    }

    public void setFailed(Long failed) {
        this.failed = failed;
    }
}
