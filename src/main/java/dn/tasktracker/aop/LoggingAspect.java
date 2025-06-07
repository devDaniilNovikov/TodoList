package dn.tasktracker.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect  {

    private final KafkaTemplate<String,Object> kafkaTemplate;


    @Before("@annotation(Loggable)")
    public void beforeTaskServiceMethods(JoinPoint joinPoint) {
        log.info("Метод: {} начинает свое выполнение!",joinPoint.getSignature().getName());
    }

//    @After("@annotation(Loggable)")
//    public void afterTaskServiceMethods(JoinPoint joinPoint) {
//        log.info("Метод: {} завершил свое выполнение!", joinPoint.getSignature().getName());
//    }

    @Around("@annotation(Loggable)")
    public Object aroundTaskServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;
        Object[] args = joinPoint.getArgs();
        String joinPointInfo = joinPoint.getSignature().getName();
        var kafkaMessage = List.of(joinPointInfo,args,executionTime);
        kafkaTemplate.send("TaskTracker",kafkaMessage);
        log.info("Метод: {} c аргументами {} выполнился за {} мс",
                joinPoint.getSignature().getName(), args, executionTime);
        return result;
    }





}
