package com.oceanview.service;

import com.oceanview.dto.BillResponse;
import com.oceanview.entity.Reservation;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@ApplicationScoped
public class BillingService {

  @PersistenceContext(unitName = "oceanPU")
  private EntityManager em;

  public BillResponse calculateBill(Reservation r) {
    long nights = ChronoUnit.DAYS.between(r.getCheckIn(), r.getCheckOut());
    BigDecimal rate = r.getRoomType().getRatePerNight();
    BigDecimal total = rate.multiply(BigDecimal.valueOf(nights));

    BigDecimal totalPaid = em.createQuery(
        "SELECT COALESCE(SUM(p.amount), 0) FROM ReservationPayment p " +
        "WHERE p.reservation.reservationNo = :resNo AND UPPER(p.paymentStatus) = 'PAID'",
        BigDecimal.class)
      .setParameter("resNo", r.getReservationNo())
      .getSingleResult();

    BigDecimal balance = total.subtract(totalPaid);

    return new BillResponse(r.getReservationNo(), nights, rate, total, totalPaid, balance);
  }
}
