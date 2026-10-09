package com.edabip.users.model;

import java.time.LocalDateTime;
import java.util.List;

/** Maps to the "users" table. */
public class User {

    public static final List<String> ROLES =
            List.of("Administrator", "Editor", "Viewer", "Business Analyst", "User");
    public static final List<String> STATUSES = List.of("Active", "Inactive");
    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_INACTIVE = "Inactive";

    private Long id;
    private String userId;
    private String name;
    private String email;
    private String role;
    private String status;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    public User() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
