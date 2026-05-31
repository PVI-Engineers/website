package com.pviengineers.careersapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestRateLimitFilter extends OncePerRequestFilter {

    private static final long WINDOW_MS = 60_000L;

    private final Map<String, CounterWindow> counters = new ConcurrentHashMap<>();
    private final AtomicInteger cleanupTicker = new AtomicInteger(0);

    @Value("${app.security.rate-limit.enabled:true}")
    private boolean enabled;

    @Value("${app.security.rate-limit.login-per-minute:12}")
    private int loginPerMinute;

    @Value("${app.security.rate-limit.presign-per-minute:40}")
    private int presignPerMinute;

    @Value("${app.security.rate-limit.application-submit-per-minute:20}")
    private int applicationSubmitPerMinute;

    @Value("${app.security.rate-limit.health-per-minute:120}")
    private int healthPerMinute;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        int limit = resolveLimit(request);
        if (limit <= 0) {
            filterChain.doFilter(request, response);
            return;
        }

        String bucketKey = resolveBucketKey(request);
        if (isRateLimited(bucketKey, limit)) {
            response.setStatus(429);
            response.setHeader("Retry-After", "60");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                    {"timestamp":"%s","status":429,"error":"Too Many Requests","details":["Request limit exceeded. Please retry after 1 minute."]}
                    """.formatted(Instant.now()));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private int resolveLimit(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("POST".equalsIgnoreCase(method) && "/api/auth/login".equals(path)) {
            return loginPerMinute;
        }

        if ("POST".equalsIgnoreCase(method) && "/api/careers/applications/presign".equals(path)) {
            return presignPerMinute;
        }

        if ("POST".equalsIgnoreCase(method) && "/api/careers/applications".equals(path)) {
            return applicationSubmitPerMinute;
        }

        if ("/api/health".equals(path)) {
            return healthPerMinute;
        }

        return 0;
    }

    private String resolveBucketKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        String clientIp = request.getRemoteAddr();

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            String[] values = forwardedFor.split(",");
            for (int index = values.length - 1; index >= 0; index--) {
                String candidate = values[index].trim();
                if (!candidate.isBlank()) {
                    clientIp = candidate;
                    break;
                }
            }
        }

        return request.getRequestURI() + "|" + clientIp;
    }

    private boolean isRateLimited(String key, int limit) {
        long now = System.currentTimeMillis();
        CounterWindow window = counters.computeIfAbsent(key, ignored -> new CounterWindow(now));

        synchronized (window) {
            if (now - window.windowStartMs >= WINDOW_MS) {
                window.windowStartMs = now;
                window.requestCount = 0;
            }

            if (window.requestCount >= limit) {
                return true;
            }

            window.requestCount++;
        }

        if (cleanupTicker.incrementAndGet() % 500 == 0) {
            cleanupOldCounters(now);
        }

        return false;
    }

    private void cleanupOldCounters(long now) {
        counters.entrySet().removeIf(entry -> now - entry.getValue().windowStartMs > WINDOW_MS * 3);
    }

    private static final class CounterWindow {
        private long windowStartMs;
        private int requestCount;

        private CounterWindow(long windowStartMs) {
            this.windowStartMs = windowStartMs;
            this.requestCount = 0;
        }
    }
}
