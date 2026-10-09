package com.edabip.users.controller;

import com.edabip.users.dto.ApiResponse;
import com.edabip.users.dto.PageResponse;
import com.edabip.users.dto.UserRequest;
import com.edabip.users.dto.UserResponse;
import com.edabip.users.dto.UserStatsResponse;
import com.edabip.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Validated
@Tag(name = "Users", description = "User management: list, search, add, edit, delete and stats")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "List users", description = "Paginated list (page is 1-based) with optional search, "
            + "role/status filters and sorting. Search matches user id, name, email and role.")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> listUsers(
            @Parameter(description = "Search text (user id, name, email, role)")
            @RequestParam(required = false) String search,
            @Parameter(description = "Filter: Administrator | Editor | Viewer | Business Analyst | User")
            @RequestParam(required = false) String role,
            @Parameter(description = "Filter: Active | Inactive")
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "page must be at least 1") int page,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "size must be at least 1")
            @Max(value = 100, message = "size must not exceed 100") int size,
            @Parameter(description = "userId | name | email | role | status | lastLogin | createdAt")
            @RequestParam(defaultValue = "userId") String sortBy,
            @Parameter(description = "asc | desc")
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResponse<UserResponse> result = userService.getUsers(search, role, status, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(200, "Users fetched successfully", result));
    }

    @Operation(summary = "User stats", description = "Total, Active, Inactive and New This Month counts")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<UserStatsResponse>> stats() {
        return ResponseEntity.ok(ApiResponse.success(200, "User stats fetched successfully", userService.getStats()));
    }

    @Operation(summary = "Get a user by user id (e.g. USR001)")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success(200, "User fetched successfully", userService.getUser(userId)));
    }

    @Operation(summary = "Add a user", description = "User id (USRnnn) is generated. Status defaults to Active.")
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> addUser(@Valid @RequestBody UserRequest request) {
        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(201).body(ApiResponse.success(201, "User created successfully", created));
    }

    @Operation(summary = "Edit a user", description = "Updates name, email, role and (optionally) status")
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> editUser(@PathVariable String userId,
                                                              @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(200, "User updated successfully",
                userService.updateUser(userId, request)));
    }

    @Operation(summary = "Delete a user")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success(200, "User deleted successfully", null));
    }

    @Operation(summary = "Bulk delete users", description = "e.g. DELETE /api/v1/users?ids=USR014,USR015")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Map<String, Integer>>> deleteUsers(@RequestParam("ids") List<String> ids) {
        int deleted = userService.deleteUsers(ids);
        return ResponseEntity.ok(ApiResponse.success(200, deleted + " user(s) deleted successfully",
                Map.of("deleted", deleted)));
    }
}
