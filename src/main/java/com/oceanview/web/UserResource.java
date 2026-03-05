package com.oceanview.web;

import com.oceanview.dto.UserCreateRequest;
import com.oceanview.dto.UserResponse;
import com.oceanview.dto.UserUpdateRequest;
import com.oceanview.entity.User;
import com.oceanview.security.RoleGuard;
import com.oceanview.service.UserService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService service;

    @GET
    public List<UserResponse> list(@HeaderParam("Authorization") String auth) {
        RoleGuard.requireAdmin(auth);
        return service.list().stream().map(this::toRes).collect(Collectors.toList());
    }

    @GET
    @Path("/{username}")
    public UserResponse get(@HeaderParam("Authorization") String auth,
                            @PathParam("username") String username) {
        RoleGuard.requireAdmin(auth);
        return toRes(service.get(username));
    }

    @POST
    public Response create(@HeaderParam("Authorization") String auth, UserCreateRequest req) {
        RoleGuard.requireAdmin(auth);
        User u = service.create(req);
        return Response.status(Response.Status.CREATED).entity(toRes(u)).build();
    }

    @PUT
    @Path("/{username}")
    public UserResponse update(@HeaderParam("Authorization") String auth,
                               @PathParam("username") String username,
                               UserUpdateRequest req) {
        RoleGuard.requireAdmin(auth);
        return toRes(service.update(username, req));
    }

    @DELETE
    @Path("/{username}")
    public Response delete(@HeaderParam("Authorization") String auth,
                           @PathParam("username") String username) {
        RoleGuard.requireAdmin(auth);
        service.delete(username);
        return Response.noContent().build();
    }

    private UserResponse toRes(User u) {
        UserResponse out = new UserResponse();
        out.username = u.getUsername();
        out.role = u.getRole();
        return out;
    }
}