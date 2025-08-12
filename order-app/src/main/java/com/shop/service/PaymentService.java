package com.shop.service;

import org.jboss.logging.Logger;

import com.shop.model.CardPayment;
import com.shop.model.PaymentStatus;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PaymentService {

    private static final Logger LOG = Logger.getLogger(PaymentService.class);

    public String processCard(CardPayment cardPayment) {
        LOG.infof("Processing card payment for card number: %s", cardPayment.getCardNumber());
        PaymentStatus paymentStatus;
        if (cardPayment.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            LOG.error("Invalid payment amount");
            paymentStatus = new PaymentStatus(
                    PaymentStatus.Status.FAILED,
                    generateTransactionId(),
                    "Invalid payment amount"
            );
            return paymentStatus.getStatus().name();
        }

        
        if (cardPayment.getCardNumber().startsWith("0000")) {
            LOG.error("Invalid card number");
            paymentStatus = new PaymentStatus(
                    PaymentStatus.Status.FAILED,
                    generateTransactionId(),
                    "Invalid card number"
            );
            return paymentStatus.getStatus().name();
        }

        LOG.info("Valid payment amount");
        paymentStatus = new PaymentStatus(
                PaymentStatus.Status.SUCCESS,
                generateTransactionId(),
                "Payment successful"
        );
        return paymentStatus.getStatus().name();
    }

    private String generateTransactionId() {
        return java.util.UUID.randomUUID().toString(); // Generate a unique UUID for the transaction
    }

}
