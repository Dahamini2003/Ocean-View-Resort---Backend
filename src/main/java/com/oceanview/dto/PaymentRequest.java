package com.oceanview.dto;

import java.math.BigDecimal;

public class PaymentRequest {
  public BigDecimal amount;
  public String paymentMethod; // CASH/CARD/ONLINE
  public String paymentStatus; // PAID/PENDING/REFUNDED
  public String referenceNo;
  public String notes;
}
