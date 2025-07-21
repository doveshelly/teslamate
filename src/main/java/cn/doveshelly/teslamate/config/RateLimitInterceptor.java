package cn.doveshelly.teslamate.config;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    // key: uri + method => value: timestamp list
    private final Map<String, ConcurrentLinkedQueue<Long>> requestRecords = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod methodHandler = (HandlerMethod) handler;
        Method method = methodHandler.getMethod();

        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        if (rateLimit == null) {
            return true; // 没有注解，不限流
        }

        int limit = rateLimit.limit();
        int duration = rateLimit.duration();

        String key = request.getRequestURI() + "#" + method.getName(); // 可扩展为用户ID+接口组合

        long now = System.currentTimeMillis();
        long windowStart = now - duration * 1000L;

        ConcurrentLinkedQueue<Long> timestamps = requestRecords.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>());

        // 清理过期时间戳
        while (!timestamps.isEmpty() && timestamps.peek() < windowStart) {
            timestamps.poll();
        }

        if (timestamps.size() >= limit) {
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("Rate limit exceeded. Please try again later.");
            return false;
        }

        timestamps.add(now);
        return true;
    }
}