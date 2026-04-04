package com.financeapp.config.ratelimit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "ratelimit")
public class RateLimitProperties {

    private Strategy ip;
    private Strategy login;

    @Getter
    @Setter
    public static class Strategy {
        private int maxRequests;
        private long windowMs;
    }
}