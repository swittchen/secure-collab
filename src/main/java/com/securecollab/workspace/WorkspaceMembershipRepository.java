package com.securecollab.workspace;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceMembershipRepository extends JpaRepository<WorkspaceMembership, Long> {

    boolean existsByWorkspaceIdAndUserId(Long workspaceId, Long userId);

    boolean existsByWorkspaceIdAndUserIdAndRole(Long workspaceId, Long userId, WorkspaceRole role);
}
