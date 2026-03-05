package com.oceanview.service;

import com.oceanview.entity.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.Map;

@ApplicationScoped
public class AuthService {

  @PersistenceContext(unitName = "oceanPU")
  private EntityManager em;

  public User findUser(String username) {
    if (username == null) return null;

    // ✅ Force read latest value from DB (avoid EclipseLink cache issue)
    Map<String, Object> hints = Collections.singletonMap(
            "javax.persistence.cache.storeMode",
            CacheStoreMode.REFRESH
    );

    return em.find(User.class, username.trim(), hints);
  }

  public boolean verifyPassword(String plain, String hash) {
    if (plain == null || hash == null) return false;

    // ✅ Make jBCrypt accept bcryptjs hashes ($2b$ / $2y$)
    if (hash.startsWith("$2b$") || hash.startsWith("$2y$")) {
      hash = "$2a$" + hash.substring(4);
    }

    try {
      return BCrypt.checkpw(plain, hash);
    } catch (IllegalArgumentException ex) {
      // ✅ prevents "Invalid salt revision" from becoming 500
      return false;
    }
  }
}