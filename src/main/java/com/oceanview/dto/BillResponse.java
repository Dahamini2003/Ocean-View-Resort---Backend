package com.oceanview.dto;

import java.math.BigDecimal;

public class BillResponse {
  public String reservationNo;
  public long nights;
  public BigDecimal ratePerNight;
  public BigDecimal totalCost;

  public BigDecimal totalPaid;
  public BigDecimal balance;

  public BillResponse() {}

  public BillResponse(String reservationNo, long nights, BigDecimal ratePerNight,
                      BigDecimal totalCost, BigDecimal totalPaid, BigDecimal balance) {
    this.reservationNo = reservationNo;
    this.nights = nights;
    this.ratePerNight = ratePerNight;
    this.totalCost = totalCost;
    this.totalPaid = totalPaid;
    this.balance = balance;
  }
}
