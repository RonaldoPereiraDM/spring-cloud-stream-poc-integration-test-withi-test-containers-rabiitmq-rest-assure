package com.user.consumer.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.user.consumer.dto.UserEventDto;
import com.user.consumer.enums.ActionType;
import com.user.consumer.enums.UserStatus;
import com.user.consumer.enums.UserType;
import jakarta.persistence.*;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "tb_users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private UUID userId;

    @Column(nullable = false, length = 50)
    private String userName;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(length = 255)
    private String phoneNumber;

    @Column(length = 255)
    private String imageUrl;

    public UserEventDto convertToUserEventDto(ActionType actionType) {
        UserEventDto userEventDto = new UserEventDto();
        BeanUtils.copyProperties(this, userEventDto);
        userEventDto.setUserType(this.getUserType().toString());
        userEventDto.setUserStatus(this.getUserStatus().toString());
        userEventDto.setActionType(actionType.toString());
        return userEventDto;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}