package org.example.userstories.audit;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class PaymentAuditAspect {

    @Pointcut("@annotation(org.example.userstories.audit.Auditable)")
    public void paymentModificationMethods() {
    }

    @Before("paymentModificationMethods()")
    public void logAudit(JoinPoint joinPoint) {
        String methodName =
                joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        String paymentId = "N/A";
        if (args.length > 0 && args[0] instanceof UUID id) {
            paymentId = id.toString();
        }

        log.info("[AUDIT] method={} paymentId={} timestamp={}",
                methodName,
                paymentId,
                LocalDateTime.now()
        );
    }
}
