package com.edabip.service;

import com.edabip.dto.AuthResponse;
import com.edabip.dto.LoginRequest;
import com.edabip.dto.RegisterRequest;
import com.edabip.entity.Role;
import com.edabip.entity.Status;
import com.edabip.entity.User;
import com.edabip.exception.DuplicateResourceException;
import com.edabip.exception.ResourceNotFoundException;
import com.edabip.repository.RoleRepository;
import com.edabip.repository.UserRepository;
import com.edabip.security.JwtService;
import com.edabip.security.SecurityRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String DEFAULT_ROLE = "Viewer";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** Public sign-up always creates a Viewer. Admins are created through POST /users by an existing ADMIN. */
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + req.getEmail());
        }
        Role viewer = roleRepository.findByNameIgnoreCase(DEFAULT_ROLE)
                .orElseThrow(() -> new ResourceNotFoundException("Default role '" + DEFAULT_ROLE + "' is not configured"));
        User user = new User();
        user.setName(req.getName().trim());
        user.setEmail(req.getEmail().trim());
        user.setRole(viewer);
        user.setStatus(Status.ACTIVE);
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setLastLogin(LocalDateTime.now());
        User saved = userRepository.save(user);
        log.info("Registered user id={}", saved.getId());
        return toAuthResponse(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmailIgnoreCase(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            log.warn("Failed login for {}", req.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }
        if (!Status.ACTIVE.equals(user.getStatus())) {
            throw new DisabledException("User account is inactive");
        }
        user.setLastLogin(LocalDateTime.now());
        log.info("User id={} logged in", user.getId());
        return toAuthResponse(user);
    }

    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(jwtService.generateToken(user), jwtService.getExpirationMs(),
                user.getEmail(), user.getName(), user.getRole().getName(),
                SecurityRoles.fromRoleName(user.getRole().getName()));
    }
}
