package cn.doveshelly.teslamate.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private SignatureAuthInterceptor signatureAuthInterceptor;

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(signatureAuthInterceptor).addPathPatterns("/**"); // 所有接口都进拦截器，由注解决定是否执行校验
        registry.addInterceptor(rateLimitInterceptor).addPathPatterns("/**");
    }
}
