package com.oceanview.dto;

import java.math.BigDecimal;

public class PaymentResponse {
  public Integer paymentId;
  public String reservationNo;
  public BigDecimal amount;
  public String paymentMethod;
  public String paymentStatus;
  public String paidAt;
  public String referenceNo;
  public String notes;
}
