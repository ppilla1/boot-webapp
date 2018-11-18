package net.f0error.bootwebapp.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

//@Aspect
@Component
@Slf4j
public class MethodInvokeAspect {

    @Pointcut("target(net.f0error.bootwebapp.service.ContentMsgService)")
    public void modelMethods() {};

    @Before("modelMethods())")
    public void logMethodInvokationAdvice(JoinPoint joinPoint) {
        log.info("Invoked Method [{}] on ContentMsgService ", joinPoint.getSignature().getName());
    }
}
