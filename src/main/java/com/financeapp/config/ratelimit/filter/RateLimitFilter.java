package com.financeapp.config.ratelimit.filter;

import com.financeapp.config.ratelimit.resolver.RateLimitStrategyResolver;
import com.financeapp.config.ratelimit.strategy.RateLimitStrategy;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitStrategyResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        RateLimitStrategy strategy = resolver.resolve(request);

        String key = getKey(request);

        if (!strategy.allowRequest(key)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");

            response.getWriter().write("""
                {
                  "message": "Too many requests",
                  "status": 429
                }
            """);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getKey(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}