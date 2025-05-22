package com.securecollab.workspace;

import com.securecollab.user.User;
import com.securecollab.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("workspaceSecurity")
@RequiredArgsConstructor
public class WorkspaceSecurityService {

    private final WorkspaceMembershipRepository membershipRepository;
    private final UserRepository userRepository;

    public boolean isMember(Long workspaceId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return false;

        return membershipRepository.existsByWorkspaceIdAndUserId(workspaceId, user.getId());
    }

    public boolean hasRole(Long workspaceId, String roleName) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return false;

        return membershipRepository.existsByWorkspaceIdAndUserIdAndRole(
                workspaceId, user.getId(), WorkspaceRole.valueOf(roleName)
        );
    }
}
