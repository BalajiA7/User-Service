package org.example.user_service.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class UserServiceConfigs {

    @Bean
    public BCryptPasswordEncoder getBcryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}