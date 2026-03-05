package com.oceanview.security;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;

public class RoleGuard {

    private RoleGuard() {}

    public static void requireAdmin(String authHeader) {
        TokenStore.Session s = getSession(authHeader);
        String role = (s.role == null) ? "" : s.role.trim().toUpperCase();
        if (!"ADMIN".equals(role)) {
            throw new ForbiddenException("Admin access required");
        }
    }

    public static TokenStore.Session getSession(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Missing Authorization: Bearer <token>");
        }
        String token = authHeader.substring("Bearer ".length()).trim();
        TokenStore.Session s = TokenStore.getSession(token);
        if (s == null) throw new NotAuthorizedException("Invalid or expired token");
        return s;
    }
}