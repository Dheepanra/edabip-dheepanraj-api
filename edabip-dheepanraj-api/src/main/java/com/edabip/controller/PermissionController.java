package com.edabip.controller;

import com.edabip.dto.ApiResponse;
import com.edabip.dto.PermissionDto;
import com.edabip.dto.PermissionMatrixResponse;
import com.edabip.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@Tag(name = "Permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Operation(summary = "List all permissions", description = "Module and action, for example Users-Manage")
    @GetMapping
    public ApiResponse<List<PermissionDto>> list() {
        return ApiResponse.ok("Permissions fetched", permissionService.list());
    }

    @Operation(summary = "Permission matrix", description = "All roles against all permissions")
    @GetMapping("/matrix")
    public ApiResponse<PermissionMatrixResponse> matrix() {
        return ApiResponse.ok("Permission matrix fetched", permissionService.matrix());
    }
}
