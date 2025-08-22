package com.user.consumer.it.listener;

public enum Exchanges {

    USER_EVENT_EXCHANGE("ead.user.event");

    private String exchangeName;

    private Exchanges(String exchangeName){
        this.exchangeName = exchangeName;
    }

    public String getExchangeName() {
        return exchangeName;
    }
}
