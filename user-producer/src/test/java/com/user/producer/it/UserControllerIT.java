package com.user.producer.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.producer.models.UserModel;
import com.user.producer.publisher.dto.UserEventDto;
import com.user.producer.testcontainers.BaseIT;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.messaging.Message;

import java.io.IOException;

import static com.user.producer.enums.UserStatus.ACTIVE;
import static com.user.producer.enums.UserType.USER;
import static com.user.producer.utils.ResourceUtils.getContentFromResource;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.CREATED;

@DisplayName("User Controller - Integration Test")
public class UserControllerIT extends BaseIT {

    private final String correctUserToRegisterJson = getContentFromResource("/json/correct/register_user.json");
    private static final String USER_EVENT_BINDING_NAME = "ead.userevent";

    @Autowired
    private OutputDestination outputDestination;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should save a new user and send event")
    void testUserCreated() throws IOException {

        UserModel userModel =
        given()
                .body(correctUserToRegisterJson)
                .contentType(JSON)
                .accept(JSON)
        .when()
                .post("/user")
        .then()
                .statusCode(CREATED.value())
                .body("userId", notNullValue())
                .body("userName", equalTo("ronaldomelo"))
                .body("email", equalTo("ronaldo@pereiramelo.com"))
                .body("fullName", equalTo("Ronaldo Melo"))
                .body("userStatus", equalTo(ACTIVE.name()))
                .body("userType", equalTo(USER.name()))
                .body("phoneNumber", equalTo("93 99124 9586"))
                .body("imageUrl", equalTo("www.aws.domain.com/13254345.png"))
                .body("creationDate", notNullValue())
                .body("lastUpdateDate", notNullValue())
        .extract()
                .as(UserModel.class);

        Message<byte[]> outputMessage = outputDestination.receive(0, USER_EVENT_BINDING_NAME);
        assertThat(outputMessage).isNotNull();
        UserEventDto userEventDto = objectMapper.readValue(outputMessage.getPayload(), UserEventDto.class);

        assertThat(userEventDto.getUserId()).isNotNull();
        assertThat(userEventDto.getUsername()).isEqualTo(userModel.getUserName());
        assertThat(userEventDto.getEmail()).isEqualTo(userModel.getEmail());
        assertThat(userEventDto.getFullName()).isEqualTo(userModel.getFullName());
        assertThat(userEventDto.getUserStatus()).isEqualTo(userModel.getUserStatus().name());
        assertThat(userEventDto.getUserType()).isEqualTo(userModel.getUserType().name());
        assertThat(userEventDto.getPhoneNumber()).isEqualTo(userModel.getPhoneNumber());
        assertThat(userEventDto.getImageUrl()).isEqualTo(userModel.getImageUrl());
        assertThat(userEventDto.getActionType()).isEqualTo("CREATE");

    }

}