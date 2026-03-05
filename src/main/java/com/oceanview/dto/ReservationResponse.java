package com.oceanview.dto;

public class ReservationResponse {
  public String reservationNo;

  public Integer guestId;
  public String guestName;
  public String address;
  public String contactNumber;

  public String roomType;
  public String checkIn;
  public String checkOut;

  // ✅ new fields
  public boolean guestAlreadyRegistered;
  public String message;
}