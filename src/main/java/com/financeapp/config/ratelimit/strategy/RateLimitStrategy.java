package com.financeapp.config.ratelimit.strategy;

public interface RateLimitStrategy {
    boolean allowRequest(String key);
}