package com.oceanview.web;

import com.oceanview.dto.ApiError;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<Throwable> {

  @Inject
  Jsonb jsonb;

  @Override
  public Response toResponse(Throwable ex) {
    int status = 500;
    String msg = "Server error";

    if (ex instanceof WebApplicationException) {
      status = ((WebApplicationException) ex).getResponse().getStatus();
      msg = ex.getMessage();
    } else if (ex.getMessage() != null && !ex.getMessage().trim().isEmpty()) {
      msg = ex.getMessage();
    }

    String body = jsonb.toJson(new ApiError(status, msg));
    return Response.status(status)
        .type(MediaType.APPLICATION_JSON)
        .entity(body)
        .build();
  }
}
