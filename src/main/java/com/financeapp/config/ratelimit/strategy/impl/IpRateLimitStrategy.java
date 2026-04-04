package com.financeapp.config.ratelimit.strategy.impl;

import com.financeapp.config.ratelimit.config.RateLimitProperties;
import com.financeapp.config.ratelimit.strategy.RateLimitStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class IpRateLimitStrategy implements RateLimitStrategy {

    private final RateLimitProperties properties;

    private final Map<String, Integer> counts = new ConcurrentHashMap<>();
    private final Map<String, Long> timestamps = new ConcurrentHashMap<>();

    @Override
    public boolean allowRequest(String ip) {

        int maxRequests = properties.getIp().getMaxRequests();
        long window = properties.getIp().getWindowMs();

        long now = System.currentTimeMillis();

        timestamps.putIfAbsent(ip, now);
        counts.putIfAbsent(ip, 0);

        if (now - timestamps.get(ip) > window) {
            timestamps.put(ip, now);
            counts.put(ip, 0);
        }

        int count = counts.get(ip);

        if (count >= maxRequests) return false;

        counts.put(ip, count + 1);
        return true;
    }
}