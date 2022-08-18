package com.cyh.caprice.capriceservice.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class LimitAccessTimesAspect {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Before("@annotation(com.wechat.wechatservice.util.LimitAccessTimes)")
    public void repeatSumbitIntercept(JoinPoint joinPoint)  throws Exception {
        //获取当前切面所设置的方法的类名、方法名
        String className = joinPoint.getTarget().getClass().getName();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();

        // 获取配置的过期时间
        LimitAccessTimes annotation = method.getAnnotation(LimitAccessTimes.class);
        long timeout = annotation.timeout();

        String key =  className + ":" + methodName + ":" + timeout + "s";
        log.info(" --- >> 防重提交：key -- {}", key);
        // 判断是否已经超过重复提交的限制时间
        String value = redisTemplate.opsForValue().get(key);
        //如果不为空，说明redis设置的禁止访问时间未过期，抛出异常，禁止访问
        if (StringUtils.isNotBlank(value)) {
            String messge = MessageFormat.format("请勿在{0}s内重复提交", timeout);
            throw new Exception(messge);
        }
        redisTemplate.opsForValue().set(key, key, timeout,TimeUnit.SECONDS);
    }
}
