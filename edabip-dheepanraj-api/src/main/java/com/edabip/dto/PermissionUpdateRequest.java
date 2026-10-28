package com.edabip.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class PermissionUpdateRequest {

    @NotNull(message = "permissionIds is required (use an empty list to remove all)")
    @Schema(example = "[1, 3, 5]")
    private List<Long> permissionIds;

    public List<Long> getPermissionIds() { return permissionIds; }
    public void setPermissionIds(List<Long> permissionIds) { this.permissionIds = permissionIds; }
}
