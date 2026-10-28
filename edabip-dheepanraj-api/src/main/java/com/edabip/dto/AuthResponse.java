package com.edabip.dto;

public class AuthResponse {

    private final String token;
    private final String tokenType = "Bearer";
    private final long expiresInMs;
    private final String email;
    private final String name;
    private final String role;
    private final String authority;

    public AuthResponse(String token, long expiresInMs, String email, String name, String role, String authority) {
        this.token = token;
        this.expiresInMs = expiresInMs;
        this.email = email;
        this.name = name;
        this.role = role;
        this.authority = authority;
    }

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
    public long getExpiresInMs() { return expiresInMs; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getAuthority() { return authority; }
}
