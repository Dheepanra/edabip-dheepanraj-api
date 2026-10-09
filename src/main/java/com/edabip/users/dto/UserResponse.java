package com.edabip.users.dto;

import com.edabip.users.model.User;

import java.time.LocalDateTime;

public class UserResponse {

    private final String userId;
    private final String name;
    private final String email;
    private final String role;
    private final String status;
    private final LocalDateTime lastLogin;
    private final LocalDateTime createdAt;

    public UserResponse(String userId, String name, String email, String role, String status,
                        LocalDateTime lastLogin, LocalDateTime createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
    }

    public static UserResponse from(User u) {
        return new UserResponse(u.getUserId(), u.getName(), u.getEmail(), u.getRole(),
                u.getStatus(), u.getLastLogin(), u.getCreatedAt());
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
