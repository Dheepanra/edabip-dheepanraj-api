package com.edabip.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Request body for add (POST) and edit (PUT). */
public class UserRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "(?i)Administrator|Editor|Viewer|Business Analyst|User",
            message = "Role must be one of: Administrator, Editor, Viewer, Business Analyst, User")
    private String role;

    /** Optional. Defaults to Active on add; unchanged on edit when omitted. */
    @Pattern(regexp = "(?i)Active|Inactive", message = "Status must be Active or Inactive")
    private String status;

    public UserRequest() {
    }

    public UserRequest(String name, String email, String role, String status) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
