package com.oceanview.entity;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
  @Id
  private String username;

  @Column(name="password_hash", nullable=false)
  private String passwordHash;

  @Column(nullable=false)
  private String role;

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }

  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
}
