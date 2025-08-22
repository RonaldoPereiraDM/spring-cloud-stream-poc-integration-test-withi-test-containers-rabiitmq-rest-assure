package com.user.consumer.dto;

import com.user.consumer.enums.UserStatus;
import com.user.consumer.enums.UserType;
import com.user.consumer.models.User;

import java.util.UUID;

public class UserEventDto {

    private UUID userId;
    private String userName;
    private String email;
    private String fullName;
    private String userStatus;
    private String userType;
    private String phoneNumber;
    private String imageUrl;
    private String actionType;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public User convertToUserModel() {
        var userModel = new User();
        userModel.setUserId(userId);
        userModel.setUserName(userName);
        userModel.setEmail(email);
        userModel.setFullName(fullName);
        userModel.setUserStatus(UserStatus.valueOf(userStatus));
        userModel.setUserType(UserType.valueOf(userType));
        userModel.setPhoneNumber(phoneNumber);
        userModel.setImageUrl(imageUrl);
        return userModel;
    }
}