package org.prgrms.kdt.aop;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(org.prgrms.kdt.aop.TrackTime)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Before method called. {}", joinPoint.getSignature().toString());
        var startTime = System.nanoTime(); // 1 -> 1,000,000,000
        var result = joinPoint.proceed();
        var endTime = System.nanoTime();
        logger.info("After method called with result => {} and time taken by {} nanoseconds", result, endTime - startTime);
        return result;
    }
}
