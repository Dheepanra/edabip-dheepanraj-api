package com.edabip.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public class UserRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    @Schema(example = "Priya Raman")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 150)
    @Schema(example = "priya@example.com")
    private String email;

    @NotNull(message = "roleId is required")
    @Schema(example = "3")
    private Long roleId;

    @Pattern(regexp = "Active|Inactive", message = "Status must be Active or Inactive")
    @Schema(example = "Active", allowableValues = {"Active", "Inactive"})
    private String status;

    @Size(min = 8, max = 72, message = "Password must be 8 to 72 characters")
    @Schema(example = "Password@123", description = "Required when creating a user, optional on update")
    private String password;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
