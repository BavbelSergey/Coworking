package com.example.coworking.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeAspect {
  private static final Logger logger = LoggerFactory.getLogger(ExecutionTimeAspect.class);

  @Around("execution(* com.example.coworking.service..*(..))")
  public Object logExecutionTime(ProceedingJoinPoint jointPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    Object result = jointPoint.proceed();
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    String methodName = jointPoint.getSignature().getName();
    logger.info("Method {} executed in {} ms", methodName, duration);
    return result;
  }
}
