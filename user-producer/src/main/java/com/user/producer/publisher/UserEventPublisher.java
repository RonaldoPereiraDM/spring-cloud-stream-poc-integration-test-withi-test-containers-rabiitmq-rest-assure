package com.user.producer.publisher;

import com.user.producer.publisher.dto.UserEventDto;

public interface UserEventPublisher {

    void publishUserEventCreate(UserEventDto userEventDto);

    void publishUserEventUpdate(UserEventDto userEventDto);

    void publishUserEventDelete(UserEventDto userEventDto);

}
