package com.edabip.dto;

import java.time.LocalDateTime;

public class UserResponse {

    private final Long id;
    private final String userId;
    private final String name;
    private final String email;
    private final Long roleId;
    private final String role;
    private final String status;
    private final LocalDateTime lastLogin;
    private final LocalDateTime createdAt;

    public UserResponse(Long id, String name, String email, Long roleId, String role,
                        String status, LocalDateTime lastLogin, LocalDateTime createdAt) {
        this.id = id;
        this.userId = id == null ? null : String.format("USR%03d", id);
        this.name = name;
        this.email = email;
        this.roleId = roleId;
        this.role = role;
        this.status = status;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Long getRoleId() { return roleId; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
