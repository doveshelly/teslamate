package cn.doveshelly.teslamate.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    /**
     * 允许的请求次数
     */
    int limit();

    /**
     * 时间窗口（单位：秒）
     */
    int duration(); 
}