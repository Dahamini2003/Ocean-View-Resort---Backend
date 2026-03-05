package com.oceanview.dto;

public class UserCreateRequest {
    public String username;
    public String password; // plain text from admin UI
    public String role;     // ADMIN / RECEPTIONIST
}