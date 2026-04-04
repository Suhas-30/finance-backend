package com.financeapp.config.ratelimit.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RateLimitKeyGenerator {

    public String generate(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}