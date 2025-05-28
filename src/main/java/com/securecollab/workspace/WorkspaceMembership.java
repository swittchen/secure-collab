package com.securecollab.workspace;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.securecollab.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class WorkspaceMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(optional = false)
    private Workspace workspace;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne(optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private WorkspaceRole role;

    public WorkspaceMembership orElseThrow(Object notAMember) {
        return null;
    }
}
