package com.oceanview.web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/help")
@Produces(MediaType.TEXT_PLAIN)
public class HelpResource {

  @GET
  public String help() {
    return ""
      + "Ocean View Resort API Help\n"
      + "POST  /api/auth/login\n"
      + "GET   /api/room-types\n"
      + "POST  /api/reservations (Bearer token)\n"
      + "GET   /api/reservations/{no} (Bearer token)\n"
      + "GET   /api/reservations/{no}/bill (Bearer token)\n"
      + "POST  /api/reservations/{no}/payments (Bearer token)\n"
      + "GET   /api/reservations/{no}/payments (Bearer token)\n";
  }
}
