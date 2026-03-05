package com.oceanview.web;

import com.oceanview.dto.RoomTypeRequest;
import com.oceanview.entity.RoomType;
import com.oceanview.security.RoleGuard;
import com.oceanview.service.RoomTypeService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/room-types")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomTypeResource {

  @Inject
  RoomTypeService service;

  // ✅ Public
  @GET
  public List<RoomType> all() {
    return service.getAll();
  }

  // ✅ Admin only: create/update
  @POST
  public RoomType save(@HeaderParam("Authorization") String auth, RoomTypeRequest req) {
    RoleGuard.requireAdmin(auth);
    return service.upsert(req);
  }

  // ✅ Admin only: delete
  @DELETE
  @Path("/{typeName}")
  public Response delete(@HeaderParam("Authorization") String auth,
                         @PathParam("typeName") String typeName) {
    RoleGuard.requireAdmin(auth);
    service.delete(typeName);
    return Response.noContent().build();
  }
}