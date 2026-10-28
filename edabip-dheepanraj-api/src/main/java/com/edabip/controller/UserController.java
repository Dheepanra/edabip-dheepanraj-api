package com.edabip.controller;

import com.edabip.dto.*;
import com.edabip.service.UserService;
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
@RequestMapping("/users")
@Tag(name = "Users", description = "VIEWER can read, ADMIN can create, update and delete")
public class UserController {

    private static final Set<String> SORT_FIELDS = Set.of("id", "name", "email", "status", "lastLogin", "createdAt");

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "List users", description = "Paginated, searchable by name or email, optionally filtered by role")
    @GetMapping
    public ApiResponse<PageResponse<UserResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "field,asc|desc. Allowed: id, name, email, status, lastLogin, createdAt", example = "name,asc")
            @RequestParam(defaultValue = "id,asc") String sort,
            @Parameter(description = "Matches name or email", example = "alice") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by role id", example = "3") @RequestParam(required = false) Long roleId) {
        return ApiResponse.ok("Users fetched",
                userService.list(search, roleId, PageUtil.of(page, size, sort, SORT_FIELDS)));
    }

    @Operation(summary = "User statistics", description = "Total, Active, Inactive and New This Month")
    @GetMapping("/stats")
    public ApiResponse<UserStatsResponse> stats() {
        return ApiResponse.ok("User stats fetched", userService.stats());
    }

    @Operation(summary = "Get a user by id")
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> get(@PathVariable Long id) {
        return ApiResponse.ok("User fetched", userService.get(id));
    }

    @Operation(summary = "Add a user (ADMIN)")
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("User created", userService.create(request)));
    }

    @Operation(summary = "Edit a user (ADMIN)", description = "Password is optional; send it only to change it")
    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return ApiResponse.ok("User updated", userService.update(id, request));
    }

    @Operation(summary = "Delete a user (ADMIN)")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.ok("User deleted", null);
    }
}
