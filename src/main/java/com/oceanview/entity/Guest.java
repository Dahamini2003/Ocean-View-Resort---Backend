package com.oceanview.entity;

import javax.persistence.*;

@Entity
@Table(name="guests")
public class Guest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="guest_id")
  private Integer guestId;

  @Column(name="guest_name", nullable=false)
  private String guestName;

  @Column(name="address")
  private String address;

  @Column(name="contact_number", nullable=false, unique = true) // ✅ prevent duplicates
  private String contactNumber;

  public Integer getGuestId() { return guestId; }
  public void setGuestId(Integer guestId) { this.guestId = guestId; }

  public String getGuestName() { return guestName; }
  public void setGuestName(String guestName) { this.guestName = guestName; }

  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }

  public String getContactNumber() { return contactNumber; }
  public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
}