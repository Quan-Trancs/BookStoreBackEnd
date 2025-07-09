package quantran.api.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@Log4j2
public class RateLimitConfig implements WebMvcConfigurer {

    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private static final ConcurrentHashMap<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();

    @Bean
    public RateLimitInterceptor rateLimitInterceptor() {
        return new RateLimitInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/health/**", "/api/actuator/**");
    }

    public static class RateLimitInterceptor extends HandlerInterceptorAdapter {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String clientIp = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            String key = clientIp + ":" + userAgent;

            RequestCounter counter = requestCounters.computeIfAbsent(key, k -> new RequestCounter());
            
            if (counter.isRateLimited()) {
                log.warn("Rate limit exceeded for IP: {}, User-Agent: {}", clientIp, userAgent);
                response.setStatus(429); // Too Many Requests
                response.getWriter().write("Rate limit exceeded. Please try again later.");
                return false;
            }

            counter.increment();
            return true;
        }

        private String getClientIpAddress(HttpServletRequest request) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
                return xForwardedFor.split(",")[0];
            }
            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
                return xRealIp;
            }
            return request.getRemoteAddr();
        }
    }

    private static class RequestCounter {
        private final AtomicInteger count = new AtomicInteger(0);
        private long resetTime = System.currentTimeMillis() + 60000; // 1 minute

        public boolean isRateLimited() {
            long currentTime = System.currentTimeMillis();
            if (currentTime > resetTime) {
                count.set(0);
                resetTime = currentTime + 60000;
            }
            return count.get() >= MAX_REQUESTS_PER_MINUTE;
        }

        public void increment() {
            count.incrementAndGet();
        }
    }
} 