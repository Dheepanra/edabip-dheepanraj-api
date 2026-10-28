package com.edabip.dto;

public class PermissionDto {

    private final Long id;
    private final String module;
    private final String action;
    private final String name;

    public PermissionDto(Long id, String module, String action) {
        this.id = id;
        this.module = module;
        this.action = action;
        this.name = module + "-" + action;
    }

    public Long getId() { return id; }
    public String getModule() { return module; }
    public String getAction() { return action; }
    public String getName() { return name; }
}
