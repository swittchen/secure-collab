package com.securecollab.workspace;

import com.securecollab.audit.Audit;
import com.securecollab.user.User;
import com.securecollab.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final WorkspaceService workspaceService;

    @PostMapping
    @Transactional
    @Audit(action = "CREATE_WORKSPACE")
    public ResponseEntity<Workspace> create(@RequestBody Workspace workspace,
                                            @AuthenticationPrincipal User user) {
        Workspace saved = workspaceRepository.save(workspace);
        WorkspaceMembership membership = new WorkspaceMembership();
        membership.setUser(user);
        membership.setWorkspace(saved);
        membership.setRole(WorkspaceRole.OWNER);
        membershipRepository.save(membership);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<WorkspaceDto> getMyWorkspaces(@AuthenticationPrincipal User user) {
        return workspaceService.getWorkspacesForUser(user);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@workspaceSecurity.isMember(#id)")
    public ResponseEntity<Workspace> getById(@PathVariable UUID id) {
        return workspaceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/invite")
    @PreAuthorize("@workspaceSecurity.hasRole(#id, 'OWNER')")
    @Audit(action = "INVITE_USER")
    public ResponseEntity<?> invite(@PathVariable UUID id,
                                    @RequestParam String email,
                                    @RequestParam(defaultValue = "VIEWER") WorkspaceRole role) {
        Workspace workspace = workspaceRepository.findById(id).orElseThrow();
        User user = userRepository.findByEmail(email).orElseThrow();
        if (membershipRepository.existsByWorkspaceIdAndUserId(id, user.getId())) {
            return ResponseEntity.badRequest().body("User already in workspace");
        }
        WorkspaceMembership membership = new WorkspaceMembership();
        membership.setUser(user);
        membership.setWorkspace(workspace);
        membership.setRole(role);
        membershipRepository.save(membership);
        return ResponseEntity.ok("User invited");
    }

    @GetMapping("/{id}/members")
    @PreAuthorize("@workspaceSecurity.isMember(#id)")
    public List<WorkspaceMembership> listMembers(@PathVariable UUID id) {
        return membershipRepository.findAll().stream()
                .filter(m -> m.getWorkspace().getId().equals(id))
                .toList();
    }
}
