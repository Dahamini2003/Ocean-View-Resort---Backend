package com.oceanview.service;

import com.oceanview.dto.GuestUpdateRequest;
import com.oceanview.entity.Guest;
import com.oceanview.entity.Reservation;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.List;

@ApplicationScoped
public class GuestService {

    @PersistenceContext(unitName = "oceanPU")
    private EntityManager em;

    public Guest get(int guestId) {
        Guest g = em.find(Guest.class, guestId);
        if (g == null) throw new NotFoundException("Guest not found: " + guestId);
        return g;
    }

    // ✅ NEW: get guest by contact number
    public Guest getByContact(String contactNo) {
        if (contactNo == null || contactNo.trim().isEmpty()) {
            throw new BadRequestException("contactNo is required");
        }

        List<Guest> list = em.createQuery(
                        "SELECT g FROM Guest g WHERE g.contactNumber = :cn",
                        Guest.class)
                .setParameter("cn", contactNo.trim())
                .setMaxResults(1)
                .getResultList();

        if (list.isEmpty()) {
            throw new NotFoundException("Guest not found for contact: " + contactNo);
        }
        return list.get(0);
    }

    @Transactional
    public Guest update(int guestId, GuestUpdateRequest req) {
        Guest g = get(guestId);
        if (req == null) throw new BadRequestException("Request body is required");

        if (req.contactNumber != null && !req.contactNumber.trim().isEmpty()) {
            String cn = req.contactNumber.trim();

            Long dup = em.createQuery(
                            "SELECT COUNT(x) FROM Guest x WHERE x.contactNumber = :cn AND x.guestId <> :id",
                            Long.class)
                    .setParameter("cn", cn)
                    .setParameter("id", guestId)
                    .getSingleResult();

            if (dup != null && dup > 0) {
                throw new BadRequestException("Contact number already exists for another guest");
            }

            g.setContactNumber(cn);
        }

        if (req.guestName != null && !req.guestName.trim().isEmpty()) {
            g.setGuestName(req.guestName.trim());
        }

        if (req.address != null) {
            g.setAddress(req.address.trim());
        }

        return g;
    }

    @Transactional
    public void delete(int guestId, boolean force) {
        Guest g = get(guestId);

        Long count = em.createQuery(
                        "SELECT COUNT(r) FROM Reservation r WHERE r.guest.guestId = :gid",
                        Long.class)
                .setParameter("gid", guestId)
                .getSingleResult();

        if (count != null && count > 0 && !force) {
            throw new BadRequestException("Guest has reservations. Use ?force=true to delete with reservations.");
        }

        if (count != null && count > 0) {
            em.createQuery("DELETE FROM Reservation r WHERE r.guest.guestId = :gid")
                    .setParameter("gid", guestId)
                    .executeUpdate();
        }

        em.remove(g);
    }
}