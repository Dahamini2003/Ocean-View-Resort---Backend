package com.oceanview.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="room_types")
public class RoomType {
  @Id
  @Column(name="type_name", length=30)
  private String typeName;

  @Column(name="rate_per_night", nullable=false)
  private BigDecimal ratePerNight;

  public String getTypeName() { return typeName; }
  public void setTypeName(String typeName) { this.typeName = typeName; }

  public BigDecimal getRatePerNight() { return ratePerNight; }
  public void setRatePerNight(BigDecimal ratePerNight) { this.ratePerNight = ratePerNight; }
}
