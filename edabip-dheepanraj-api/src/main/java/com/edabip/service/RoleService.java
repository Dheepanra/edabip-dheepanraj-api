package com.edabip.service;

import com.edabip.dto.PageResponse;
import com.edabip.dto.PermissionUpdateRequest;
import com.edabip.dto.RoleRequest;
import com.edabip.dto.RoleResponse;
import com.edabip.entity.Permission;
import com.edabip.entity.Role;
import com.edabip.entity.Status;
import com.edabip.exception.ConflictException;
import com.edabip.exception.DuplicateResourceException;
import com.edabip.exception.ResourceNotFoundException;
import com.edabip.repository.PermissionRepository;
import com.edabip.repository.RoleRepository;
import com.edabip.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, UserRepository userRepository,
                       PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<RoleResponse> list(String search, Pageable pageable) {
        String term = search == null ? "" : search.trim();
        return new PageResponse<>(roleRepository.findAllWithUserCount(term, pageable));
    }

    @Transactional(readOnly = true)
    public RoleResponse get(Long id) {
        Role role = find(id);
        return new RoleResponse(role.getId(), role.getName(), role.getDescription(), role.getStatus(),
                userRepository.countByRoleId(id));
    }

    @Transactional
    public RoleResponse create(RoleRequest req) {
        if (roleRepository.existsByNameIgnoreCase(req.getName().trim())) {
            throw new DuplicateResourceException("Role name already exists: " + req.getName());
        }
        Role role = new Role();
        role.setName(req.getName().trim());
        role.setDescription(req.getDescription());
        role.setStatus(req.getStatus() == null ? Status.ACTIVE : req.getStatus());
        Role saved = roleRepository.save(role);
        log.info("Created role id={} name={}", saved.getId(), saved.getName());
        return new RoleResponse(saved.getId(), saved.getName(), saved.getDescription(), saved.getStatus(), 0L);
    }

    @Transactional
    public RoleResponse update(Long id, RoleRequest req) {
        Role role = find(id);
        if (roleRepository.existsByNameIgnoreCaseAndIdNot(req.getName().trim(), id)) {
            throw new DuplicateResourceException("Role name already exists: " + req.getName());
        }
        role.setName(req.getName().trim());
        role.setDescription(req.getDescription());
        if (req.getStatus() != null) {
            role.setStatus(req.getStatus());
        }
        Role saved = roleRepository.save(role);
        log.info("Updated role id={}", id);
        return new RoleResponse(saved.getId(), saved.getName(), saved.getDescription(), saved.getStatus(),
                userRepository.countByRoleId(id));
    }

    @Transactional
    public RoleResponse setStatus(Long id, String status) {
        Role role = find(id);
        role.setStatus(status);
        Role saved = roleRepository.save(role);
        log.info("Role id={} status set to {}", id, status);
        return new RoleResponse(saved.getId(), saved.getName(), saved.getDescription(), saved.getStatus(),
                userRepository.countByRoleId(id));
    }

    @Transactional
    public void delete(Long id) {
        Role role = find(id);
        long users = userRepository.countByRoleId(id);
        if (users > 0) {
            throw new ConflictException("Role '" + role.getName() + "' cannot be deleted because "
                    + users + " user(s) are still assigned to it. Reassign them first.");
        }
        roleRepository.delete(role);
        log.info("Deleted role id={}", id);
    }

    /**
     * Replaces the role's permission set. Hibernate deletes and re-inserts rows of role_permissions,
     * so the whole update must be one transaction: either every mapping changes or none does.
     */
    @Transactional
    public void updatePermissions(Long roleId, PermissionUpdateRequest req) {
        Role role = roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + roleId));
        Set<Long> ids = new HashSet<>(req.getPermissionIds());
        List<Permission> found = ids.isEmpty() ? List.of() : permissionRepository.findAllById(ids);
        if (found.size() != ids.size()) {
            throw new ResourceNotFoundException("One or more permission ids do not exist: " + ids);
        }
        role.setPermissions(new HashSet<>(found));
        roleRepository.save(role);
        log.info("Updated permissions for role id={} (count={})", roleId, found.size());
    }

    private Role find(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + id));
    }
}
