package com.securecollab.user;

public enum UserRole {
    ADMIN,
    EDITOR,
    VIEWER;

    public String asAuthority() {
        return "ROLE_" + name();
    }
}
