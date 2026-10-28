package com.edabip.security;

/**
 * Maps the business role (roles table) to one of the two API authorities.
 * The "Administrator" business role is ADMIN (create/update/delete); everyone else is VIEWER (read only).
 */
public final class SecurityRoles {

    public static final String ADMIN = "ADMIN";
    public static final String VIEWER = "VIEWER";

    private SecurityRoles() {
    }

    public static String fromRoleName(String roleName) {
        return "Administrator".equalsIgnoreCase(roleName) ? ADMIN : VIEWER;
    }
}
