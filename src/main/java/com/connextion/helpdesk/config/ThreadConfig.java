package com.connextion.helpdesk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadConfig {

    @Bean
    public ExecutorService executorService() {
        // Create a fixed thread pool of 5 threads to handle background routing, classification and logging
        return Executors.newFixedThreadPool(5);
    }
}
