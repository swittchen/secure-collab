package com.securecollab.workspace;

import com.securecollab.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMapper workspaceMapper;

    public List<WorkspaceDto> getWorkspacesForUser(User user) {
        return workspaceRepository.findAll().stream()
                .filter(ws -> ws.getMembers().stream()
                        .anyMatch(m -> m.getUser().getId().equals(user.getId())))
                .map(workspaceMapper::toDto)
                .toList();
    }
}
