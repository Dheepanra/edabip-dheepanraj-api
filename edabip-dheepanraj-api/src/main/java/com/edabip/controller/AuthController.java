package com.edabip.controller;

import com.edabip.dto.ApiResponse;
import com.edabip.dto.AuthResponse;
import com.edabip.dto.LoginRequest;
import com.edabip.dto.RegisterRequest;
import com.edabip.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication")
@SecurityRequirements
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user (Viewer role)",
            description = "Creates a user with the Viewer role and returns a JWT. Admins are created by an ADMIN through POST /users.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registered successfully", authService.register(request)));
    }

    @Operation(summary = "Login",
            description = "Returns a JWT. Seeded admin: alice@example.com, seeded viewer: bob@example.com (password from SEED_USER_PASSWORD, default Password@123).")
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("Login successful", authService.login(request));
    }
}
