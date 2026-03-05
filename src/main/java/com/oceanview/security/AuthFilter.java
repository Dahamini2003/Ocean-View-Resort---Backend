package com.oceanview.security;

import com.oceanview.dto.ApiError;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

  @Inject
  Jsonb jsonb;

  @Override
  public void filter(ContainerRequestContext ctx) {
    String path = ctx.getUriInfo().getPath();
    String method = ctx.getMethod();

    // ✅ Public endpoints
    if (path.startsWith("auth/login") || path.startsWith("help") || path.startsWith("health")) {
      return;
    }

    // ✅ Room types: only GET is public
    if (path.startsWith("room-types") && "GET".equalsIgnoreCase(method)) {
      return;
    }

    String auth = ctx.getHeaderString("Authorization");
    if (auth == null || !auth.startsWith("Bearer ")) {
      abort(ctx, 401, "Missing Authorization: Bearer <token>");
      return;
    }

    String token = auth.substring("Bearer ".length()).trim();
    if (TokenStore.getSession(token) == null) {
      abort(ctx, 401, "Invalid or expired token");
    }
  }

  private void abort(ContainerRequestContext ctx, int status, String msg) {
    String body = jsonb.toJson(new ApiError(status, msg));
    ctx.abortWith(Response.status(status)
            .type(MediaType.APPLICATION_JSON)
            .entity(body)
            .build());
  }
}