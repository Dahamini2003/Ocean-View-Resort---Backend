package com.oceanview.web;

import com.oceanview.dto.LoginRequest;
import com.oceanview.dto.LoginResponse;
import com.oceanview.entity.User;
import com.oceanview.security.TokenStore;
import com.oceanview.service.AuthService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

  @Inject
  AuthService auth;

  @POST
  @Path("/login")
  public LoginResponse login(LoginRequest req) {
    if (req == null || req.username == null || req.password == null) {
      throw new BadRequestException("username and password are required");
    }

    User u = auth.findUser(req.username);
    if (u == null || !auth.verifyPassword(req.password, u.getPasswordHash())) {
      throw new NotAuthorizedException("Invalid username or password");
    }

    String token = TokenStore.issueToken(u.getUsername(), u.getRole());
    return new LoginResponse(token, u.getRole());
  }
}
