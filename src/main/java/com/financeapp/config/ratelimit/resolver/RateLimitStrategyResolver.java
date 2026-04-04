package com.financeapp.config.ratelimit.resolver;

import com.financeapp.config.ratelimit.strategy.RateLimitStrategy;
import com.financeapp.config.ratelimit.strategy.impl.IpRateLimitStrategy;
import com.financeapp.config.ratelimit.strategy.impl.LoginRateLimitStrategy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RateLimitStrategyResolver {

    private final IpRateLimitStrategy ipStrategy;
    private final LoginRateLimitStrategy loginStrategy;

    public RateLimitStrategy resolve(HttpServletRequest request) {

        String path = request.getRequestURI();

        if (path.contains("/auth/login")) {
            return loginStrategy;
        }

        return ipStrategy;
    }
}