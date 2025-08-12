package com.shop.model;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardPayment {
    private String cardNumber;
    private String expDate;
    private String nameOnCard;
    private double amount;
    private CardType cardType;
    
    public enum CardType {
        VISA("Visa"),
        MASTER("Master"),
        AMEX("Amex"),
        DISCOVER("Discover");

        private final String value;

        CardType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
    
}
