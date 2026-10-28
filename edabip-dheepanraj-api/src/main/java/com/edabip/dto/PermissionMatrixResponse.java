package com.edabip.dto;

import java.util.List;

public class PermissionMatrixResponse {

    private final List<PermissionDto> permissions;
    private final List<RoleRow> roles;

    public PermissionMatrixResponse(List<PermissionDto> permissions, List<RoleRow> roles) {
        this.permissions = permissions;
        this.roles = roles;
    }

    public List<PermissionDto> getPermissions() { return permissions; }
    public List<RoleRow> getRoles() { return roles; }

    public static class RoleRow {
        private final Long roleId;
        private final String roleName;
        private final String status;
        private final List<Long> permissionIds;

        public RoleRow(Long roleId, String roleName, String status, List<Long> permissionIds) {
            this.roleId = roleId;
            this.roleName = roleName;
            this.status = status;
            this.permissionIds = permissionIds;
        }

        public Long getRoleId() { return roleId; }
        public String getRoleName() { return roleName; }
        public String getStatus() { return status; }
        public List<Long> getPermissionIds() { return permissionIds; }
    }
}
