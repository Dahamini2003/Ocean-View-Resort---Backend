package com.oceanview.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="reservations")
public class Reservation {

  @Id
  @Column(name="reservation_no", length=30)
  private String reservationNo;

  @ManyToOne(optional=false, fetch = FetchType.EAGER)
  @JoinColumn(name="guest_id", referencedColumnName="guest_id")
  private Guest guest;

  @Column(name="check_in", nullable=false)
  private LocalDate checkIn;

  @Column(name="check_out", nullable=false)
  private LocalDate checkOut;

  @ManyToOne(optional=false, fetch = FetchType.EAGER)
  @JoinColumn(name="room_type_name", referencedColumnName="type_name")
  private RoomType roomType;

  public String getReservationNo() { return reservationNo; }
  public void setReservationNo(String reservationNo) { this.reservationNo = reservationNo; }

  public Guest getGuest() { return guest; }
  public void setGuest(Guest guest) { this.guest = guest; }

  public LocalDate getCheckIn() { return checkIn; }
  public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

  public LocalDate getCheckOut() { return checkOut; }
  public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

  public RoomType getRoomType() { return roomType; }
  public void setRoomType(RoomType roomType) { this.roomType = roomType; }
}
