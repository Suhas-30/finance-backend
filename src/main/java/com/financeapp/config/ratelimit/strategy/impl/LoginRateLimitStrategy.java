package com.financeapp.config.ratelimit.strategy.impl;

import com.financeapp.config.ratelimit.config.RateLimitProperties;
import com.financeapp.config.ratelimit.strategy.RateLimitStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class LoginRateLimitStrategy implements RateLimitStrategy {

    private final RateLimitProperties properties;

    private final Map<String, Integer> counts = new ConcurrentHashMap<>();
    private final Map<String, Long> timestamps = new ConcurrentHashMap<>();

    @Override
    public boolean allowRequest(String key) {

        int maxRequests = properties.getLogin().getMaxRequests();
        long window = properties.getLogin().getWindowMs();

        long now = System.currentTimeMillis();

        timestamps.putIfAbsent(key, now);
        counts.putIfAbsent(key, 0);

        if (now - timestamps.get(key) > window) {
            timestamps.put(key, now);
            counts.put(key, 0);
        }

        int count = counts.get(key);

        if (count >= maxRequests) return false;

        counts.put(key, count + 1);
        return true;
    }
}