package com.edabip.dto;

/**
 * Also used as the JPQL constructor-expression target for "roles with user count".
 */
public class RoleResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final String status;
    private final long userCount;

    public RoleResponse(Long id, String name, String description, String status, Long userCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.userCount = userCount == null ? 0 : userCount;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public long getUserCount() { return userCount; }
}
