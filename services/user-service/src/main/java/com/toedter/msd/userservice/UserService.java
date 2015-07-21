package com.toedter.msd.userservice;

import com.toedter.msd.userservice.user.UserTestDataLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UserService {

    @Bean(initMethod = "loadData")
    UserTestDataLoader RepositoryTestData() {
        return new UserTestDataLoader();
    }

    public static void main(String[] args) {
        SpringApplication.run(UserService.class, args);
    }
}
