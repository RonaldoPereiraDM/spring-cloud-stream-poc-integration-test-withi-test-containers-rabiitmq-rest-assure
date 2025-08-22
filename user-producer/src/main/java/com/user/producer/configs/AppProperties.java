package com.user.producer.configs;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppProperties {

    private String userEventDestination = "userEvent-out-0";
    private String paymentEventDestination = "";

    public String getUserEventDestination() {
        return userEventDestination;
    }

    public String getPaymentEventDestination() {
        return paymentEventDestination;
    }
}