package com.shop.model;

import java.math.BigDecimal;

public class CardPayment {
    private String cardNumber;
    private String expDate;
    private String nameOnCard;
    private BigDecimal amount;
    private CardType cardType;
    
    // Default constructor
    public CardPayment() {}
    
    // Constructor with all fields
    public CardPayment(String cardNumber, String expDate, String nameOnCard, 
                      BigDecimal amount, CardType cardType) {
        this.cardNumber = cardNumber;
        this.expDate = expDate;
        this.nameOnCard = nameOnCard;
        this.amount = amount;
        this.cardType = cardType;
    }
    
    // Getters
    public String getCardNumber() {
        return cardNumber;
    }
    
    public String getExpDate() {
        return expDate;
    }
    
    public String getNameOnCard() {
        return nameOnCard;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public CardType getCardType() {
        return cardType;
    }
    
    // Setters
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }
    
    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }
    
    @Override
    public String toString() {
        return "CardPayment{" +
                "cardNumber='" + (cardNumber != null ? "**** **** **** " + cardNumber.substring(Math.max(0, cardNumber.length() - 4)) : "null") + '\'' +
                ", expDate='" + expDate + '\'' +
                ", nameOnCard='" + nameOnCard + '\'' +
                ", amount=" + amount +
                ", cardType=" + cardType +
                '}';
    }
    
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