package com.decryptor.config;

import com.decryptor.service.EncryptionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.decryptor")
public class Config {

    @Bean
    public EncryptionService encryptionService() {
        return new EncryptionService();
    }
}
