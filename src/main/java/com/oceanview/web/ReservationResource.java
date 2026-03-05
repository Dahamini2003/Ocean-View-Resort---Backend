package com.oceanview.web;

import com.oceanview.dto.*;
import com.oceanview.entity.Reservation;
import com.oceanview.entity.ReservationPayment;
import com.oceanview.security.RoleGuard;
import com.oceanview.service.BillingService;
import com.oceanview.service.PaymentService;
import com.oceanview.service.ReservationService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/reservations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

  @Inject
  ReservationService reservations;

  @Inject
  BillingService billing;

  @Inject
  PaymentService payments;

  // ✅ Both roles: Get next reservation number
  @GET
  @Path("/next-no")
  public NextReservationNoResponse nextNo() {
    return new NextReservationNoResponse(reservations.nextReservationNo());
  }

  // ✅ Both roles: List reservations + filters
  // /api/reservations?roomType=Deluxe&status=completed|active|upcoming|all
  @GET
  public List<ReservationResponse> list(@QueryParam("roomType") String roomType,
                                        @QueryParam("status") String status) {
    return reservations.listReservations(roomType, status).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
  }

  // ✅ Reception + Admin: create
  @POST
  public Response create(ReservationCreateRequest req) {
    ReservationService.CreateResult result = reservations.createReservation(req);

    ReservationResponse out = toResponse(result.getReservation());
    out.guestAlreadyRegistered = result.isGuestAlreadyRegistered();
    out.message = out.guestAlreadyRegistered
            ? "Guest already registered. Existing guest details used for reservation."
            : "New guest registered and reservation created.";

    return Response.status(Response.Status.CREATED).entity(out).build();
  }

  // ✅ Reception + Admin: get single
  @GET
  @Path("/{reservationNo}")
  public ReservationResponse get(@PathParam("reservationNo") String reservationNo) {
    ReservationResponse out = toResponse(reservations.getReservation(reservationNo));
    out.guestAlreadyRegistered = true;
    out.message = "Reservation details loaded.";
    return out;
  }

  // ✅ Reception + Admin: bill
  @GET
  @Path("/{reservationNo}/bill")
  public BillResponse bill(@PathParam("reservationNo") String reservationNo) {
    Reservation r = reservations.getReservation(reservationNo);
    return billing.calculateBill(r);
  }

  // ✅ Reception + Admin: add payment
  @POST
  @Path("/{reservationNo}/payments")
  public Response addPayment(@PathParam("reservationNo") String reservationNo, PaymentRequest req) {
    ReservationPayment p = payments.addPayment(reservationNo, req);
    return Response.status(Response.Status.CREATED).entity(toPaymentResponse(p)).build();
  }

  // ✅ Reception + Admin: list payments
  @GET
  @Path("/{reservationNo}/payments")
  public List<PaymentResponse> listPayments(@PathParam("reservationNo") String reservationNo) {
    return payments.listPayments(reservationNo).stream()
            .map(this::toPaymentResponse)
            .collect(Collectors.toList());
  }

  // ✅ ADMIN ONLY: update reservation
  @PUT
  @Path("/{reservationNo}")
  public ReservationResponse update(@HeaderParam("Authorization") String auth,
                                    @PathParam("reservationNo") String reservationNo,
                                    ReservationCreateRequest req) {
    RoleGuard.requireAdmin(auth);
    Reservation r = reservations.updateReservation(reservationNo, req);

    ReservationResponse out = toResponse(r);
    out.guestAlreadyRegistered = true;
    out.message = "Reservation updated.";
    return out;
  }

  // ✅ ADMIN ONLY: delete reservation
  @DELETE
  @Path("/{reservationNo}")
  public Response delete(@HeaderParam("Authorization") String auth,
                         @PathParam("reservationNo") String reservationNo) {
    RoleGuard.requireAdmin(auth);
    reservations.deleteReservation(reservationNo);
    return Response.noContent().build();
  }

  private ReservationResponse toResponse(Reservation r) {
    ReservationResponse out = new ReservationResponse();
    out.reservationNo = r.getReservationNo();
    out.guestId = r.getGuest().getGuestId();
    out.guestName = r.getGuest().getGuestName();
    out.address = r.getGuest().getAddress();
    out.contactNumber = r.getGuest().getContactNumber();
    out.roomType = r.getRoomType().getTypeName();
    out.checkIn = r.getCheckIn().toString();
    out.checkOut = r.getCheckOut().toString();
    return out;
  }

  private PaymentResponse toPaymentResponse(ReservationPayment p) {
    PaymentResponse out = new PaymentResponse();
    out.paymentId = p.getPaymentId();
    out.reservationNo = p.getReservation().getReservationNo();
    out.amount = p.getAmount();
    out.paymentMethod = p.getPaymentMethod();
    out.paymentStatus = p.getPaymentStatus();
    out.paidAt = (p.getPaidAt() == null) ? null : p.getPaidAt().toString();
    out.referenceNo = p.getReferenceNo();
    out.notes = p.getNotes();
    return out;
  }
}