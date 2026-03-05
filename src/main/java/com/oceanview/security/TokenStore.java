package com.oceanview.security;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TokenStore {

  public static class Session {
    public final String username;
    public final String role;

    public Session(String username, String role) {
      this.username = username;
      this.role = role;
    }
  }

  private static final Map<String, Session> TOKENS = new ConcurrentHashMap<>();

  public static String issueToken(String username, String role) {
    String token = UUID.randomUUID().toString();
    TOKENS.put(token, new Session(username, role));
    return token;
  }

  public static Session getSession(String token) {
    return TOKENS.get(token);
  }
}
