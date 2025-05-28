package com.securecollab.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository  extends JpaRepository<AuditLog, UUID> {
}
