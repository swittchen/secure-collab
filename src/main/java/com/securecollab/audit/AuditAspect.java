package com.securecollab.audit;

import com.securecollab.user.User;
import com.securecollab.workspace.Workspace;
import com.securecollab.workspace.WorkspaceRepository;
import jakarta.transaction.Transactional;
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
import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;
    private final WorkspaceRepository workspaceRepository;

    @Around("@annotation(com.securecollab.audit.Audit)")
    @Transactional
    public Object logAction(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Audit auditAnnotation = method.getAnnotation(Audit.class);

        String action = auditAnnotation.action();
        User user = extractCurrentUser();
        Workspace workspace = extractWorkspaceArg(joinPoint);

        String description = buildDescription(joinPoint);

        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .workspace(workspace)
                .action(action)
                .description(description)
                .timestamp(Instant.now())
                .build());

        log.info("AUDIT: {} by {}", action, user.getEmail());
        return joinPoint.proceed();
    }


    private User extractCurrentUser() throws AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User user) {
            return user;
        }
        throw new AccessDeniedException("Unauthenticated access");
    }

    private Workspace extractWorkspaceArg(ProceedingJoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof Workspace w) {
                return w;
            } else if (arg instanceof UUID workspaceId) {
                return workspaceRepository.findById(workspaceId).orElse(null);
            }
        }
        return null;
    }

    private String buildDescription(ProceedingJoinPoint joinPoint) {
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
