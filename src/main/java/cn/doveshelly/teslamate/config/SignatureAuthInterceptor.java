package cn.doveshelly.teslamate.config;

import cn.dev33.satoken.stp.StpUtil;
import cn.doveshelly.teslamate.utils.CommonConfig;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SignatureAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private CommonConfig commonConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只处理方法级别拦截（controller 方法）
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod method = (HandlerMethod) handler;

        boolean requireSignature = method.hasMethodAnnotation(SignatureRequired.class)
                || method.getBeanType().isAnnotationPresent(SignatureRequired.class);

        boolean requireLogin = method.hasMethodAnnotation(LoginRequired.class)
                || method.getBeanType().isAnnotationPresent(LoginRequired.class);

        // 1. 签名校验
        if (requireSignature) {
            String signature = request.getHeader("X-Signature");
            String nonce = request.getHeader("X-Nonce");
            String timestamp = request.getHeader("X-Timestamp");

            if (!StringUtils.hasText(signature) || !StringUtils.hasText(nonce) || !StringUtils.hasText(timestamp)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing required headers");
                return false;
            }

            String paramsString = getSignParams(request);

            String stringToSign = paramsString + nonce + timestamp + commonConfig.getSignSecret();
            String expectedSignature = DigestUtil.sha256Hex(stringToSign);

            if (!expectedSignature.equals(signature)) {
                log.warn("Invalid Signature: {}", signature);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Signature");
                return false;
            }

            long requestTimestamp = Long.parseLong(timestamp);
            if (System.currentTimeMillis() - requestTimestamp > 5 * 60 * 1000) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request timestamp expired");
                return false;
            }
        }

        // 2. 登录校验
        if (requireLogin) {
            StpUtil.checkLogin();
        }

        return true;
    }

    /**
     * 签名参数获取方法（支持过滤空 key 和非法参数）
     */
    public static String getSignParams(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap == null || parameterMap.isEmpty()) return "";

        List<String> paramList = parameterMap.entrySet().stream()
                .filter(entry -> entry.getKey() != null && !entry.getKey().trim().equals("") && !entry.getKey().equals("{}"))
                .map(entry -> entry.getKey() + "=" + Arrays.stream(entry.getValue()).findFirst().orElse(""))
                .sorted()
                .collect(Collectors.toList());
        return String.join("&", paramList);
    }
}
