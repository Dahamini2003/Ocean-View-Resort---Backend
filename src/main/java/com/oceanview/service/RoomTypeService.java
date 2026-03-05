package com.oceanview.service;

import com.oceanview.dto.RoomTypeRequest;
import com.oceanview.entity.RoomType;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class RoomTypeService {

  @PersistenceContext(unitName = "oceanPU")
  private EntityManager em;

  public List<RoomType> getAll() {
    return em.createQuery("SELECT r FROM RoomType r ORDER BY r.typeName", RoomType.class).getResultList();
  }

  @Transactional
  public RoomType upsert(RoomTypeRequest req) {
    if (req == null) throw new BadRequestException("Request body is required");
    if (req.typeName == null || req.typeName.trim().isEmpty()) throw new BadRequestException("typeName is required");
    if (req.ratePerNight == null) throw new BadRequestException("ratePerNight is required");

    String name = req.typeName.trim();
    RoomType rt = em.find(RoomType.class, name);
    if (rt == null) {
      rt = new RoomType();
      rt.setTypeName(name);
      rt.setRatePerNight(req.ratePerNight);
      em.persist(rt);
    } else {
      rt.setRatePerNight(req.ratePerNight);
    }
    return rt;
  }

  @Transactional
  public void delete(String typeName) {
    if (typeName == null || typeName.trim().isEmpty()) throw new BadRequestException("typeName is required");
    RoomType rt = em.find(RoomType.class, typeName.trim());
    if (rt == null) throw new NotFoundException("Room type not found: " + typeName);
    em.remove(rt);
  }
}