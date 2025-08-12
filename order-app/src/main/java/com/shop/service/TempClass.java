package com.shop.service;
import com.shop.model.DrinkOrder;
import com.shop.model.PaymentStatus;

public class TempClass {
    public static void main(String[] args) {
        DrinkOrder drinkOrder = new DrinkOrder();
        drinkOrder.setOrderId("12345"); 

        drinkOrder.setOrderId("12345");

        drinkOrder.getDrinkType().name();
        drinkOrder.getDrinkSize().name();

        // com.shop.model.DrinkOrder.OrderStatus orderStatus = com.shop.model.DrinkOrder.OrderStatus.RECEIVED;
        // drinkOrder.setOrderStatus(orderStatus);
        // System.out.println("Order" + drinkOrder);

        // com.shop.model.DrinkOrder.OrderStatus orderStatus = com.shop.model.DrinkOrder.OrderStatus.PAYMENT;
        // drinkOrder.setOrderStatus(orderStatus);

//         com.shop.model.DrinkOrder.OrderStatus orderStatus = com.shop.model.DrinkOrder.OrderStatus.INPROGRESS;
// drinkOrder.setOrderStatus(orderStatus);

// com.shop.model.DrinkOrder.OrderStatus orderStatus = com.shop.model.DrinkOrder.OrderStatus.PAYMENT;
// drinkOrder.setOrderStatus(orderStatus);

// com.shop.model.DrinkOrder.OrderStatus orderStatus = com.shop.model.DrinkOrder.OrderStatus.INPROGRESS;
// drinkOrder.setOrderStatus(orderStatus);

com.shop.model.DrinkOrder.OrderStatus orderStatus = com.shop.model.DrinkOrder.OrderStatus.CANCELLED;
drinkOrder.setOrderStatus(orderStatus);

    }
} 
