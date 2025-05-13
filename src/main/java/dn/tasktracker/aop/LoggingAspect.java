package dn.tasktracker.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@EnableAspectJAutoProxy
public class LoggingAspect {

    @Before("@annotation(Loggable)")
    public void beforeTaskServiceMethods(JoinPoint joinPoint) {
        log.info("Метод: {} начинает свое выполнение!",joinPoint.getSignature().getName());
    }

    @After("@annotation(Loggable)")
    public void afterTaskServiceMethods(JoinPoint joinPoint) {
        log.info("Метод: {} завершил свое выполнение!", joinPoint.getSignature().getName());
    }

    @Around("@annotation(Loggable)")
    public Object aroundTaskServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        log.info("Метод: {} выполнился за {} мс",
                joinPoint.getSignature().getName(),
                executionTime);
        return result;
    }





}
