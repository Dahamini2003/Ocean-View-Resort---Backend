package com.oceanview.service;

import com.oceanview.dto.UserCreateRequest;
import com.oceanview.dto.UserUpdateRequest;
import com.oceanview.entity.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class UserService {

    @PersistenceContext(unitName = "oceanPU")
    private EntityManager em;

    public List<User> list() {
        return em.createQuery("SELECT u FROM User u ORDER BY u.username", User.class).getResultList();
    }

    public User get(String username) {
        if (username == null || username.trim().isEmpty()) throw new BadRequestException("username is required");
        User u = em.find(User.class, username.trim());
        if (u == null) throw new NotFoundException("User not found: " + username);
        return u;
    }

    @Transactional
    public User create(UserCreateRequest req) {
        if (req == null) throw new BadRequestException("Request body is required");
        if (req.username == null || req.username.trim().isEmpty()) throw new BadRequestException("username is required");
        if (req.password == null || req.password.isEmpty()) throw new BadRequestException("password is required");
        if (req.role == null || req.role.trim().isEmpty()) throw new BadRequestException("role is required");

        String username = req.username.trim();
        if (em.find(User.class, username) != null) throw new BadRequestException("Username already exists");

        // ✅ Use jBCrypt ($2a$) to avoid "salt revision" issues
        String hash = BCrypt.hashpw(req.password, BCrypt.gensalt(10));

        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(hash);
        u.setRole(req.role.trim().toUpperCase());

        em.persist(u);
        return u;
    }

    @Transactional
    public User update(String username, UserUpdateRequest req) {
        User u = get(username);
        if (req == null) throw new BadRequestException("Request body is required");

        if (req.role != null && !req.role.trim().isEmpty()) {
            u.setRole(req.role.trim().toUpperCase());
        }

        if (req.password != null && !req.password.isEmpty()) {
            String hash = BCrypt.hashpw(req.password, BCrypt.gensalt(10));
            u.setPasswordHash(hash);
        }

        return u;
    }

    @Transactional
    public void delete(String username) {
        User u = get(username);

        // safety: do not allow deleting yourself (optional)
        // (you can remove this rule if you want)
        if ("admin".equalsIgnoreCase(u.getUsername())) {
            throw new BadRequestException("Default admin cannot be deleted");
        }

        em.remove(u);
    }
}