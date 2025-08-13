package com.shop.model;

public class PaymentStatus {
    private Status status;
    private String message;
    private String transactionId;
    
    // Default constructor
    public PaymentStatus() {}
    
    // Constructor with all fields
    public PaymentStatus(Status status, String message, String transactionId) {
        this.status = status;
        this.message = message;
        this.transactionId = transactionId;
    }
    
    // Getters
    public Status getStatus() {
        return status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    // Setters
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    @Override
    public String toString() {
        return "PaymentStatus{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
    
    public enum Status {
        SUCCESS("SUCCESS"),
        FAILED("FAILED"),
        PENDING("PENDING"),
        CANCELLED("CANCELLED");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}