package com.edabip.controller;

import com.edabip.dto.*;
import com.edabip.entity.Status;
import com.edabip.service.RoleService;
import com.edabip.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/roles")
@Tag(name = "Roles", description = "Role management and role permissions")
public class RoleController {

    private static final Set<String> SORT_FIELDS = Set.of("id", "name", "status");

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "List roles with user count", description = "Paginated and searchable by role name")
    @GetMapping
    public ApiResponse<PageResponse<RoleResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "field,asc|desc. Allowed: id, name, status", example = "name,asc")
            @RequestParam(defaultValue = "id,asc") String sort,
            @RequestParam(required = false) String search) {
        return ApiResponse.ok("Roles fetched", roleService.list(search, PageUtil.of(page, size, sort, SORT_FIELDS)));
    }

    @Operation(summary = "Get a role by id")
    @GetMapping("/{id}")
    public ApiResponse<RoleResponse> get(@PathVariable Long id) {
        return ApiResponse.ok("Role fetched", roleService.get(id));
    }

    @Operation(summary = "Add a role (ADMIN)", description = "Role names are unique (409 if the name exists)")
    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> create(@Valid @RequestBody RoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Role created", roleService.create(request)));
    }

    @Operation(summary = "Edit a role (ADMIN)")
    @PutMapping("/{id}")
    public ApiResponse<RoleResponse> update(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        return ApiResponse.ok("Role updated", roleService.update(id, request));
    }

    @Operation(summary = "Activate a role (ADMIN)")
    @PatchMapping("/{id}/activate")
    public ApiResponse<RoleResponse> activate(@PathVariable Long id) {
        return ApiResponse.ok("Role activated", roleService.setStatus(id, Status.ACTIVE));
    }

    @Operation(summary = "Deactivate a role (ADMIN)")
    @PatchMapping("/{id}/deactivate")
    public ApiResponse<RoleResponse> deactivate(@PathVariable Long id) {
        return ApiResponse.ok("Role deactivated", roleService.setStatus(id, Status.INACTIVE));
    }

    @Operation(summary = "Delete a role (ADMIN)", description = "Returns 409 if users are still assigned to the role")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ApiResponse.ok("Role deleted", null);
    }

    @Operation(summary = "Replace the permissions of a role (ADMIN)", description = "Runs in a single transaction")
    @PutMapping("/{id}/permissions")
    public ApiResponse<Void> updatePermissions(@PathVariable Long id, @Valid @RequestBody PermissionUpdateRequest request) {
        roleService.updatePermissions(id, request);
        return ApiResponse.ok("Role permissions updated", null);
    }
}
