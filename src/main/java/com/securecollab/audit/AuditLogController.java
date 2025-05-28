package com.securecollab.audit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Audit Log", description = "View security-relevant user actions in the system")
@SecurityRequirement(name = "bearerAuth")
public class AuditLogController  {

    private final AuditLogRepository auditLogRepository;

    @Operation(
            summary = "Get all audit log entries",
            description = "Returns a list of all recorded audit events. ADMIN only.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of audit entries"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – only ADMINs allowed")
            }
    )
    @GetMapping
    public List<AuditLog> getAll(){
        return auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }
}
