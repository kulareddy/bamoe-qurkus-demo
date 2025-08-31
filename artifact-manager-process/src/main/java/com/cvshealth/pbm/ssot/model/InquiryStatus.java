package com.cvshealth.pbm.ssot.model;

public class InquiryStatus {
    public enum Status {
        NEW,
        ON_HOLD,
        IN_PROGRESS,
        ASSIGNED,
        CLOSED
    }

    private Status status;
    private String reason;

    public InquiryStatus() {}

    public InquiryStatus(Status status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "{\n" +
                "  \"status\": \"" + status + "\",\n" +
                "  \"reason\": \"" + reason + "\"\n" +
                "}";
    }
}
