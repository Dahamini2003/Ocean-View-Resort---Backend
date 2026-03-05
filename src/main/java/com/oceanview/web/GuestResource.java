package com.oceanview.web;

import com.oceanview.dto.GuestResponse;
import com.oceanview.dto.GuestUpdateRequest;
import com.oceanview.entity.Guest;
import com.oceanview.security.RoleGuard;
import com.oceanview.service.GuestService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/guests")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GuestResource {

    @Inject
    GuestService service;

    // ✅ NEW: Get guest by contact number
    // GET /api/guests/by-contact/0771234567
    @GET
    @Path("/by-contact/{contactNo}")
    public GuestResponse getByContact(@HeaderParam("Authorization") String auth,
                                      @PathParam("contactNo") String contactNo) {
        RoleGuard.requireAdmin(auth);
        return toRes(service.getByContact(contactNo));
    }

    @GET
    @Path("/{guestId}")
    public GuestResponse get(@HeaderParam("Authorization") String auth,
                             @PathParam("guestId") int guestId) {
        RoleGuard.requireAdmin(auth);
        return toRes(service.get(guestId));
    }

    @PUT
    @Path("/{guestId}")
    public GuestResponse update(@HeaderParam("Authorization") String auth,
                                @PathParam("guestId") int guestId,
                                GuestUpdateRequest req) {
        RoleGuard.requireAdmin(auth);
        return toRes(service.update(guestId, req));
    }

    @DELETE
    @Path("/{guestId}")
    public Response delete(@HeaderParam("Authorization") String auth,
                           @PathParam("guestId") int guestId,
                           @QueryParam("force") @DefaultValue("false") boolean force) {
        RoleGuard.requireAdmin(auth);
        service.delete(guestId, force);
        return Response.noContent().build();
    }

    private GuestResponse toRes(Guest g) {
        GuestResponse out = new GuestResponse();
        out.guestId = g.getGuestId();
        out.guestName = g.getGuestName();
        out.address = g.getAddress();
        out.contactNumber = g.getContactNumber();
        return out;
    }
}