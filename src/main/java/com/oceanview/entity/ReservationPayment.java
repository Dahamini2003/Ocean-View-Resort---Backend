package com.oceanview.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="reservation_payments")
public class ReservationPayment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="payment_id")
  private Integer paymentId;

  @ManyToOne(optional=false, fetch = FetchType.LAZY)
  @JoinColumn(name="reservation_no", referencedColumnName="reservation_no")
  private Reservation reservation;

  @Column(nullable=false)
  private BigDecimal amount;

  @Column(name="payment_method", nullable=false)
  private String paymentMethod;

  @Column(name="payment_status", nullable=false)
  private String paymentStatus;

  @Column(name="paid_at")
  private LocalDateTime paidAt;

  @Column(name="reference_no")
  private String referenceNo;

  @Column(name="notes")
  private String notes;

  public Integer getPaymentId() { return paymentId; }
  public void setPaymentId(Integer paymentId) { this.paymentId = paymentId; }

  public Reservation getReservation() { return reservation; }
  public void setReservation(Reservation reservation) { this.reservation = reservation; }

  public BigDecimal getAmount() { return amount; }
  public void setAmount(BigDecimal amount) { this.amount = amount; }

  public String getPaymentMethod() { return paymentMethod; }
  public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

  public String getPaymentStatus() { return paymentStatus; }
  public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

  public LocalDateTime getPaidAt() { return paidAt; }
  public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

  public String getReferenceNo() { return referenceNo; }
  public void setReferenceNo(String referenceNo) { this.referenceNo = referenceNo; }

  public String getNotes() { return notes; }
  public void setNotes(String notes) { this.notes = notes; }
}
