package com.oceanview.dto;

public class ReservationCreateRequest {
  public String reservationNo;

  // Provide guestId OR provide guest details
  public Integer guestId;
  public String guestName;
  public String address;
  public String contactNumber;

  public String roomType;   // Standard/Deluxe/Suite
  public String checkIn;    // yyyy-MM-dd
  public String checkOut;   // yyyy-MM-dd
}
