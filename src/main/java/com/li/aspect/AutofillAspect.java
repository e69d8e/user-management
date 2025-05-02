package com.li.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

// 切面类
@Aspect
@Component
@Slf4j
public class AutofillAspect {
    @Pointcut("execution(* com.li.server..*(..)) && @annotation(com.li.common.annotation.AutoFill)")
    public void pc() {
    }

    @Before("pc()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段自动填充...");
        Object[] args = joinPoint.getArgs();
        Object o = args[0];
        if (args == null || args.length == 0) {
            return;
        }
        try {
            Method method = o.getClass().getDeclaredMethod("setCreatedTime", LocalDateTime.class);
            method.invoke(o, LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException("自动填充失败");
        }
    }
}
