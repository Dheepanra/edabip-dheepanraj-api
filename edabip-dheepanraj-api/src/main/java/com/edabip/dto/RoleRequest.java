package com.edabip.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RoleRequest {

    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name must be at most 50 characters")
    @Schema(example = "Support Agent")
    private String name;

    @Size(max = 255, message = "Description must be at most 255 characters")
    @Schema(example = "Can view users and reports")
    private String description;

    @Pattern(regexp = "Active|Inactive", message = "Status must be Active or Inactive")
    @Schema(example = "Active", allowableValues = {"Active", "Inactive"})
    private String status;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
