package com.shop.model;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrinkOrder {
    private String orderId;
    private DrinkType drinkType;
    private DrinkSize drinkSize;
    private OrderStatus orderStatus;
    private CardPayment cardPayment;
    private PaymentType paymentType;
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
