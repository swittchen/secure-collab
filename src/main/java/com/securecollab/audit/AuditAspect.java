package com.securecollab.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Instant;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;

    @Around("@annotation(com.securecollab.audit.Audit)")
    public Object logAction(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Audit auditAnnotation = method.getAnnotation(Audit.class);

        String action = auditAnnotation.action();
        String email = extractCurrentUserEmail();

        String details = buildDetails(joinPoint);

        //save log in to DB
        auditLogRepository.save(AuditLog.builder()
                .userEmail(email)
                .action(action)
                .timeStamp(Instant.now())
                .details(details)
                .build());

        log.info("AUDIT: {} by {}", action, email);
        return joinPoint.proceed();
    }


    private String extractCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "anonymous";
    }

    private String buildDetails(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            if (arg != null) {
                sb.append(arg.getClass().getSimpleName())
                        .append(": ")
                        .append(arg.toString())
                        .append("; ");
            }
        }

        return sb.toString();
    }
}
