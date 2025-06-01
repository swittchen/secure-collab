package com.securecollab.workspace;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkspaceMembershipRepository extends JpaRepository<WorkspaceMembership, UUID> {

    boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

    boolean existsByWorkspaceIdAndUserIdAndRole(UUID workspaceId, UUID userId, WorkspaceRole role);

    WorkspaceMembership findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);
}
