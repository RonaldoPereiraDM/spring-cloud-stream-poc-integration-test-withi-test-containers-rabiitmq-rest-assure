package com.user.consumer.it.listener;

public enum Queues {

    USER_EVENT_MS_COURSE("ead.userevent.ms.course");

    private String queueName;

    private Queues(String queueName){
        this.queueName = queueName;
    }

    public String getQueueName() {
        return queueName;
    }
}
