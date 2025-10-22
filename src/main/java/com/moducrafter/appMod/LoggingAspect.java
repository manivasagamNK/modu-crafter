package com.moducrafter.appMod;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

  @Around("execution(* com.moducrafter.appMod.controller..*(..))")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();

    log.info("Started execution of: {}" , joinPoint.getSignature());

    Object result = joinPoint.proceed();

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    log.info("Completed execution of: {} " , joinPoint.getSignature());
    log.info("Time taken: {} ms", duration);

    return result;
  }

}
