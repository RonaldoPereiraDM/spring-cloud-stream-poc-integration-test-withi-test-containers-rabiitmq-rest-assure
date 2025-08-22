package com.user.producer.service;

import com.user.producer.controller.dto.UserRecordDto;
import com.user.producer.models.UserModel;

public interface UserService {

    UserModel save(UserRecordDto userRecordDto);

}
