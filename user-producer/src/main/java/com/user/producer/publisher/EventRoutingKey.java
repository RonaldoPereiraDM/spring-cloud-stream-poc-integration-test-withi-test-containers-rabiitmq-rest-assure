package com.user.producer.publisher;

public enum EventRoutingKey {
    USER_CREATED("ead.user.created"),
    USER_UPDATED("ead.user.updated"),
    USER_DELETED("ead.user.deleted");

    private final String key;

    EventRoutingKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
