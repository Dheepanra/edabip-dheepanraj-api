package com.edabip.service;

import com.edabip.dto.PageResponse;
import com.edabip.dto.UserRequest;
import com.edabip.dto.UserResponse;
import com.edabip.dto.UserStatsResponse;
import com.edabip.entity.Role;
import com.edabip.entity.Status;
import com.edabip.entity.User;
import com.edabip.exception.BadRequestException;
import com.edabip.exception.DuplicateResourceException;
import com.edabip.exception.ResourceNotFoundException;
import com.edabip.repository.RoleRepository;
import com.edabip.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> list(String search, Long roleId, Pageable pageable) {
        String term = search == null ? "" : search.trim();
        Page<UserResponse> page = userRepository.search(term, roleId, pageable).map(UserService::toResponse);
        return new PageResponse<>(page);
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) {
        return toResponse(find(id));
    }

    @Transactional
    public UserResponse create(UserRequest req) {
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new BadRequestException("Password is required when creating a user");
        }
        if (userRepository.existsByEmailIgnoreCase(req.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + req.getEmail());
        }
        Role role = findActiveRole(req.getRoleId());

        User user = new User();
        user.setName(req.getName().trim());
        user.setEmail(req.getEmail().trim());
        user.setRole(role);
        user.setStatus(req.getStatus() == null ? Status.ACTIVE : req.getStatus());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        User saved = userRepository.save(user);
        log.info("Created user id={} email={}", saved.getId(), saved.getEmail());
        return toResponse(saved);
    }

    @Transactional
    public UserResponse update(Long id, UserRequest req) {
        User user = find(id);
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(req.getEmail(), id)) {
            throw new DuplicateResourceException("Email already registered: " + req.getEmail());
        }
        if (!user.getRole().getId().equals(req.getRoleId())) {
            user.setRole(findActiveRole(req.getRoleId()));
        }
        user.setName(req.getName().trim());
        user.setEmail(req.getEmail().trim());
        if (req.getStatus() != null) {
            user.setStatus(req.getStatus());
        }
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }
        User saved = userRepository.save(user);
        log.info("Updated user id={}", id);
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        User user = find(id);
        userRepository.delete(user);
        log.info("Deleted user id={}", id);
    }

    @Transactional(readOnly = true)
    public UserStatsResponse stats() {
        var startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        return new UserStatsResponse(
                userRepository.count(),
                userRepository.countByStatus(Status.ACTIVE),
                userRepository.countByStatus(Status.INACTIVE),
                userRepository.countByCreatedAtGreaterThanEqual(startOfMonth));
    }

    private User find(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    private Role findActiveRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + roleId));
        if (!Status.ACTIVE.equals(role.getStatus())) {
            throw new BadRequestException("Role '" + role.getName() + "' is inactive and cannot be assigned");
        }
        return role;
    }

    public static UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole().getId(), u.getRole().getName(),
                u.getStatus(), u.getLastLogin(), u.getCreatedAt());
    }
}
