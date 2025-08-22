package com.user.consumer;

import com.user.consumer.dto.UserEventDto;
import com.user.consumer.models.User;
import com.user.consumer.service.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.context.annotation.Bean;

import java.util.function.Consumer;

@SpringBootApplication
public class UserConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserConsumerApplication.class, args);
	}

    @Bean
    public Consumer<UserEventDto> userEventListener(UserService userService) {
        return dto -> {
            User user = dto.convertToUserModel();
            try {
                userService.save(user);
                //log.info("[LISTENER] user {} saved.", user.getUserId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Bean
    ApplicationRunner logFunctions(FunctionCatalog catalog) {
        return args -> {
            var names = catalog.getNames(null); // todas as Function/Consumer/Supplier
            System.out.println(" ----------------------- Funções disponíveis: " + names);
        };
    }

}
