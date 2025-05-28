package com.mytrainer.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class AppConfig {
    @Bean
    public Clock appClock() {
        return Clock.system(ZoneId.of("Europe/Belgrade"));
    }
}
