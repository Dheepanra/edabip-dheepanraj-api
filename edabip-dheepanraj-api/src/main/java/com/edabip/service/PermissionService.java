package com.edabip.service;

import com.edabip.dto.PermissionDto;
import com.edabip.dto.PermissionMatrixResponse;
import com.edabip.entity.Permission;
import com.edabip.repository.PermissionRepository;
import com.edabip.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public PermissionService(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<PermissionDto> list() {
        return permissionRepository.findAllByOrderByModuleAscActionAsc().stream().map(PermissionService::toDto).toList();
    }

    /** All roles against all permissions (two queries in total, no N+1). */
    @Transactional(readOnly = true)
    public PermissionMatrixResponse matrix() {
        List<PermissionDto> permissions = list();
        List<PermissionMatrixResponse.RoleRow> rows = roleRepository.findAllWithPermissions().stream()
                .map(r -> new PermissionMatrixResponse.RoleRow(r.getId(), r.getName(), r.getStatus(),
                        r.getPermissions().stream().map(Permission::getId).sorted().toList()))
                .toList();
        return new PermissionMatrixResponse(permissions, rows);
    }

    private static PermissionDto toDto(Permission p) {
        return new PermissionDto(p.getId(), p.getModule(), p.getAction());
    }
}
