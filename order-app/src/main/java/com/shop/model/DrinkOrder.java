package com.shop.model;

public class DrinkOrder {
    private String orderId;
    private DrinkType drinkType;
    private DrinkSize drinkSize;
    private OrderStatus orderStatus;
    private CardPayment cardPayment;
    private PaymentType paymentType;
    private String processInstanceId;
    private String businessKey;
    
    // Default constructor
    public DrinkOrder() {}
    
    // Constructor with main fields
    public DrinkOrder(String orderId, DrinkType drinkType, DrinkSize drinkSize, 
                     OrderStatus orderStatus, PaymentType paymentType) {
        this.orderId = orderId;
        this.drinkType = drinkType;
        this.drinkSize = drinkSize;
        this.orderStatus = orderStatus;
        this.paymentType = paymentType;
    }
    
    // Full constructor
    public DrinkOrder(String orderId, DrinkType drinkType, DrinkSize drinkSize,
                     OrderStatus orderStatus, CardPayment cardPayment, 
                     PaymentType paymentType, String processInstanceId, String businessKey) {
        this.orderId = orderId;
        this.drinkType = drinkType;
        this.drinkSize = drinkSize;
        this.orderStatus = orderStatus;
        this.cardPayment = cardPayment;
        this.paymentType = paymentType;
        this.processInstanceId = processInstanceId;
        this.businessKey = businessKey;
    }
    
    // Getters
    public String getOrderId() {
        return orderId;
    }
    
    public DrinkType getDrinkType() {
        return drinkType;
    }
    
    public DrinkSize getDrinkSize() {
        return drinkSize;
    }
    
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    
    public CardPayment getCardPayment() {
        return cardPayment;
    }
    
    public PaymentType getPaymentType() {
        return paymentType;
    }
    
    public String getProcessInstanceId() {
        return processInstanceId;
    }
    
    public String getBusinessKey() {
        return businessKey;
    }
    
    // Setters
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public void setDrinkType(DrinkType drinkType) {
        this.drinkType = drinkType;
    }
    
    public void setDrinkSize(DrinkSize drinkSize) {
        this.drinkSize = drinkSize;
    }
    
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    public void setCardPayment(CardPayment cardPayment) {
        this.cardPayment = cardPayment;
    }
    
    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
    
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
    
    @Override
    public String toString() {
        return "DrinkOrder{" +
                "orderId='" + orderId + '\'' +
                ", drinkType=" + drinkType +
                ", drinkSize=" + drinkSize +
                ", orderStatus=" + orderStatus +
                ", cardPayment=" + cardPayment +
                ", paymentType=" + paymentType +
                ", processInstanceId='" + processInstanceId + '\'' +
                ", businessKey='" + businessKey + '\'' +
                '}';
    }
    
    public enum DrinkType {
        COFFEE("COFFEE"),
        LATTE("LATTE"),
        CAPPUCCINO("CAPPUCCINO");

        private final String value;

        DrinkType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum DrinkSize {
        SMALL("SMALL"),
        MEDIUM("MEDIUM"),
        LARGE("LARGE");

        private final String value;

        DrinkSize(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum OrderStatus {
        ORDERED("ORDERED"),
        RECEIVED("RECEIVED"),
        PAYMENT("PAYMENT"),
        INPROGRESS("INPROGRESS"),
        READY("READY"),
        CANCELLED("CANCELLED");

        private final String value;

        OrderStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum PaymentType {
        CASH("CASH"),
        CARD("CARD");

        private final String value;

        PaymentType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}