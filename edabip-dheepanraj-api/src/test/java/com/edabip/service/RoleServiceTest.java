package com.edabip.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock RoleRepository roleRepository;
    @Mock UserRepository userRepository;
    @Mock PermissionRepository permissionRepository;
    @InjectMocks RoleService roleService;

    private Role role(long id, String name) {
        Role r = new Role();
        r.setId(id);
        r.setName(name);
        r.setStatus(Status.ACTIVE);
        return r;
    }

    private RoleRequest request(String name) {
        RoleRequest req = new RoleRequest();
        req.setName(name);
        req.setDescription("desc");
        return req;
    }

    @Test
    void create_duplicateName_throwsDuplicate() {
        when(roleRepository.existsByNameIgnoreCase("Editor")).thenReturn(true);

        assertThatThrownBy(() -> roleService.create(request("Editor")))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Editor");
        verify(roleRepository, never()).save(any());
    }

    @Test
    void create_success_defaultsToActiveWithZeroUsers() {
        when(roleRepository.existsByNameIgnoreCase("Support")).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenAnswer(inv -> {
            Role r = inv.getArgument(0);
            r.setId(9L);
            return r;
        });

        RoleResponse res = roleService.create(request("Support"));

        assertThat(res.getId()).isEqualTo(9L);
        assertThat(res.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(res.getUserCount()).isZero();
    }

    @Test
    void delete_roleWithUsers_throwsConflict() {
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role(2, "Editor")));
        when(userRepository.countByRoleId(2L)).thenReturn(3L);

        assertThatThrownBy(() -> roleService.delete(2L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("3 user(s)");
        verify(roleRepository, never()).delete(any());
    }

    @Test
    void delete_roleWithoutUsers_deletes() {
        Role r = role(5, "Auditor");
        when(roleRepository.findById(5L)).thenReturn(Optional.of(r));
        when(userRepository.countByRoleId(5L)).thenReturn(0L);

        roleService.delete(5L);

        verify(roleRepository).delete(r);
    }

    @Test
    void delete_notFound_throwsNotFound() {
        when(roleRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.delete(404L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void setStatus_deactivatesRole() {
        Role r = role(2, "Editor");
        when(roleRepository.findById(2L)).thenReturn(Optional.of(r));
        when(roleRepository.save(r)).thenReturn(r);
        when(userRepository.countByRoleId(2L)).thenReturn(1L);

        RoleResponse res = roleService.setStatus(2L, Status.INACTIVE);

        assertThat(res.getStatus()).isEqualTo(Status.INACTIVE);
        assertThat(res.getUserCount()).isEqualTo(1);
    }

    @Test
    void updatePermissions_unknownPermission_throwsNotFoundAndSavesNothing() {
        when(roleRepository.findByIdWithPermissions(2L)).thenReturn(Optional.of(role(2, "Editor")));
        when(permissionRepository.findAllById(any())).thenReturn(List.of(new Permission(1L, "Dashboard", "View")));

        PermissionUpdateRequest req = new PermissionUpdateRequest();
        req.setPermissionIds(List.of(1L, 999L));

        assertThatThrownBy(() -> roleService.updatePermissions(2L, req))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(roleRepository, never()).save(any());
    }

    @Test
    void updatePermissions_success_replacesPermissionSet() {
        Role r = role(2, "Editor");
        r.getPermissions().add(new Permission(10L, "Settings", "Edit"));
        when(roleRepository.findByIdWithPermissions(2L)).thenReturn(Optional.of(r));
        when(permissionRepository.findAllById(any())).thenReturn(
                List.of(new Permission(1L, "Dashboard", "View"), new Permission(3L, "Reports", "View")));

        PermissionUpdateRequest req = new PermissionUpdateRequest();
        req.setPermissionIds(List.of(1L, 3L));
        roleService.updatePermissions(2L, req);

        assertThat(r.getPermissions()).extracting(Permission::getId).containsExactlyInAnyOrder(1L, 3L);
        verify(roleRepository).save(r);
    }
}
