package com.oceanview.service;

import com.oceanview.dto.PaymentRequest;
import com.oceanview.entity.Reservation;
import com.oceanview.entity.ReservationPayment;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class PaymentService {

  @PersistenceContext(unitName = "oceanPU")
  private EntityManager em;

  @Transactional
  public ReservationPayment addPayment(String reservationNo, PaymentRequest req) {
    if (req == null) throw new BadRequestException("Request body is required");
    if (req.amount == null) throw new BadRequestException("amount is required");
    if (isBlank(req.paymentMethod)) throw new BadRequestException("paymentMethod is required");
    if (isBlank(req.paymentStatus)) req.paymentStatus = "PAID";

    Reservation r = em.find(Reservation.class, reservationNo);
    if (r == null) throw new NotFoundException("Reservation not found: " + reservationNo);

    ReservationPayment p = new ReservationPayment();
    p.setReservation(r);
    p.setAmount(req.amount);
    p.setPaymentMethod(req.paymentMethod);
    p.setPaymentStatus(req.paymentStatus);
    p.setReferenceNo(req.referenceNo);
    p.setNotes(req.notes);

    if ("PAID".equalsIgnoreCase(req.paymentStatus)) {
      p.setPaidAt(LocalDateTime.now());
    }

    em.persist(p);
    return p;
  }

  public List<ReservationPayment> listPayments(String reservationNo) {
    return em.createQuery(
        "SELECT p FROM ReservationPayment p WHERE p.reservation.reservationNo = :resNo ORDER BY p.paymentId DESC",
        ReservationPayment.class)
      .setParameter("resNo", reservationNo)
      .getResultList();
  }

  private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}
