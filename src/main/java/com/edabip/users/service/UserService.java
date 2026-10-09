package com.edabip.users.service;

import com.edabip.users.dto.PageResponse;
import com.edabip.users.dto.UserRequest;
import com.edabip.users.dto.UserResponse;
import com.edabip.users.dto.UserStatsResponse;
import com.edabip.users.exception.BadRequestException;
import com.edabip.users.exception.DuplicateResourceException;
import com.edabip.users.exception.ResourceNotFoundException;
import com.edabip.users.model.User;
import com.edabip.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    /** API sort field -> DB column (whitelist; prevents SQL injection through sortBy). */
    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "userId", "user_id",
            "name", "name",
            "email", "email",
            "role", "role",
            "status", "status",
            "lastLogin", "last_login",
            "createdAt", "created_at");

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getUsers(String search, String role, String status,
                                               int page, int size, String sortBy, String sortDir) {
        String sortColumn = SORT_COLUMNS.get(sortBy);
        if (sortColumn == null) {
            throw new BadRequestException("Invalid sortBy '" + sortBy + "'. Allowed: " + SORT_COLUMNS.keySet());
        }
        String direction;
        if ("asc".equalsIgnoreCase(sortDir)) {
            direction = "ASC";
        } else if ("desc".equalsIgnoreCase(sortDir)) {
            direction = "DESC";
        } else {
            throw new BadRequestException("Invalid sortDir '" + sortDir + "'. Allowed: asc, desc");
        }
        String roleFilter = role == null || role.isBlank() ? null : canonical(role, User.ROLES, "role");
        String statusFilter = status == null || status.isBlank() ? null : canonical(status, User.STATUSES, "status");

        long total = repository.count(search, roleFilter, statusFilter);
        int offset = (page - 1) * size;
        List<UserResponse> content = repository
                .findAll(search, roleFilter, statusFilter, sortColumn, direction, size, offset)
                .stream().map(UserResponse::from).collect(Collectors.toList());
        return new PageResponse<>(content, page, size, total);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(String userId) {
        return UserResponse.from(findOrThrow(userId));
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (repository.emailExists(email, null)) {
            throw new DuplicateResourceException("Email already registered: " + email);
        }
        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setRole(canonical(request.getRole(), User.ROLES, "role"));
        user.setStatus(request.getStatus() == null
                ? User.STATUS_ACTIVE : canonical(request.getStatus(), User.STATUSES, "status"));

        long id = repository.insert(user);
        String userId = String.format("USR%03d", id);
        repository.assignUserId(id, userId);
        return UserResponse.from(findOrThrow(userId));
    }

    @Transactional
    public UserResponse updateUser(String userId, UserRequest request) {
        User existing = findOrThrow(userId);
        String email = request.getEmail().trim().toLowerCase();
        if (repository.emailExists(email, userId)) {
            throw new DuplicateResourceException("Email already registered: " + email);
        }
        existing.setName(request.getName().trim());
        existing.setEmail(email);
        existing.setRole(canonical(request.getRole(), User.ROLES, "role"));
        if (request.getStatus() != null) {
            existing.setStatus(canonical(request.getStatus(), User.STATUSES, "status"));
        }
        repository.update(userId, existing);
        return UserResponse.from(findOrThrow(userId));
    }

    @Transactional
    public void deleteUser(String userId) {
        if (repository.deleteByUserId(userId) == 0) {
            throw notFound(userId);
        }
    }

    /** Bulk delete (UI: tick checkboxes + Delete). Returns how many rows were removed. */
    @Transactional
    public int deleteUsers(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new BadRequestException("At least one user id is required");
        }
        int deleted = 0;
        for (String id : userIds) {
            deleted += repository.deleteByUserId(id.trim());
        }
        if (deleted == 0) {
            throw new ResourceNotFoundException("None of the given users were found");
        }
        return deleted;
    }

    @Transactional(readOnly = true)
    public UserStatsResponse getStats() {
        LocalDateTime monthStart = YearMonth.now().atDay(1).atStartOfDay();
        return repository.stats(monthStart, monthStart.plusMonths(1));
    }

    private User findOrThrow(String userId) {
        return repository.findByUserId(userId).orElseThrow(() -> notFound(userId));
    }

    private ResourceNotFoundException notFound(String userId) {
        return new ResourceNotFoundException("User not found with id: " + userId);
    }

    /** Case-insensitive match against allowed values, returning the canonical spelling. */
    private String canonical(String value, List<String> allowed, String field) {
        return allowed.stream()
                .filter(a -> a.equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(
                        "Invalid " + field + " '" + value + "'. Allowed: " + allowed));
    }
}
