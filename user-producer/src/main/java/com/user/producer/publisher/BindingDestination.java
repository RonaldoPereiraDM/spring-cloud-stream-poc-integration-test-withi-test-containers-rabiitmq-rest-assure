package com.user.producer.publisher;

public enum BindingDestination {

    USER_EVENT_DESTINATION("userEvent-out-0");

    private final String bindingName;

    BindingDestination(String bindingName) {
        this.bindingName = bindingName;
    }

    public String getBindingName() {
        return bindingName;
    }

}
