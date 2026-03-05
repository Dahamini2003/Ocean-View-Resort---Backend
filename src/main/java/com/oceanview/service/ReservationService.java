package com.oceanview.service;

import com.oceanview.dto.ReservationCreateRequest;
import com.oceanview.entity.Guest;
import com.oceanview.entity.Reservation;
import com.oceanview.entity.RoomType;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ReservationService {

    @PersistenceContext(unitName = "oceanPU")
    private EntityManager em;

    // ✅ return both: reservation + flag
    public static class CreateResult {
        private final Reservation reservation;
        private final boolean guestAlreadyRegistered;

        public CreateResult(Reservation reservation, boolean guestAlreadyRegistered) {
            this.reservation = reservation;
            this.guestAlreadyRegistered = guestAlreadyRegistered;
        }

        public Reservation getReservation() { return reservation; }
        public boolean isGuestAlreadyRegistered() { return guestAlreadyRegistered; }
    }

    // ✅ generate next reservation no (RyyyyMMdd-0001)
    public String nextReservationNo() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd
        String prefix = "R" + date + "-";

        String max = em.createQuery(
                "SELECT MAX(r.reservationNo) FROM Reservation r WHERE r.reservationNo LIKE :p",
                String.class
        ).setParameter("p", prefix + "%").getSingleResult();

        int next = 1;
        if (max != null && max.startsWith(prefix)) {
            String tail = max.substring(prefix.length());
            try { next = Integer.parseInt(tail) + 1; } catch (Exception ignored) {}
        }
        return prefix + String.format("%04d", next);
    }

    // ✅ list reservations with filters (roomType + status)
    public List<Reservation> listReservations(String roomType, String status) {

        StringBuilder jpql = new StringBuilder(
                "SELECT r FROM Reservation r " +
                        "JOIN FETCH r.guest g " +
                        "JOIN FETCH r.roomType rt " +
                        "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (!isBlank(roomType)) {
            jpql.append(" AND rt.typeName = ?").append(params.size() + 1).append(" ");
            params.add(roomType.trim());
        }

        LocalDate today = LocalDate.now();
        String s = (status == null) ? "all" : status.trim().toLowerCase();

        if ("completed".equals(s)) {
            jpql.append(" AND r.checkOut < ?").append(params.size() + 1).append(" ");
            params.add(today);
        } else if ("upcoming".equals(s)) {
            jpql.append(" AND r.checkIn > ?").append(params.size() + 1).append(" ");
            params.add(today);
        } else if ("active".equals(s)) {
            jpql.append(" AND r.checkIn <= ?").append(params.size() + 1).append(" ");
            params.add(today);
            jpql.append(" AND r.checkOut > ?").append(params.size() + 1).append(" ");
            params.add(today);
        }

        jpql.append(" ORDER BY r.checkIn DESC ");

        var q = em.createQuery(jpql.toString(), Reservation.class);
        for (int i = 0; i < params.size(); i++) {
            q.setParameter(i + 1, params.get(i));
        }
        return q.getResultList();
    }

    @Transactional
    public CreateResult createReservation(ReservationCreateRequest req) {

        if (req == null) throw new BadRequestException("Request body is required");

        // ✅ auto reservationNo if empty
        if (isBlank(req.reservationNo)) {
            req.reservationNo = nextReservationNo();
        }

        if (isBlank(req.reservationNo)) throw new BadRequestException("reservationNo is required");
        if (isBlank(req.roomType)) throw new BadRequestException("roomType is required");
        if (isBlank(req.checkIn) || isBlank(req.checkOut)) throw new BadRequestException("checkIn/checkOut are required");

        if (em.find(Reservation.class, req.reservationNo.trim()) != null) {
            throw new BadRequestException("Reservation number already exists");
        }

        LocalDate in = LocalDate.parse(req.checkIn.trim());
        LocalDate out = LocalDate.parse(req.checkOut.trim());
        if (!out.isAfter(in)) throw new BadRequestException("checkOut must be after checkIn");

        RoomType roomType = em.find(RoomType.class, req.roomType.trim());
        if (roomType == null) throw new NotFoundException("Room type not found: " + req.roomType);

        boolean alreadyRegistered = false;
        Guest guest;

        if (req.guestId != null) {
            guest = em.find(Guest.class, req.guestId);
            if (guest == null) throw new NotFoundException("Guest not found: " + req.guestId);

        } else {
            if (isBlank(req.contactNumber)) throw new BadRequestException("contactNumber is required");
            String contact = req.contactNumber.trim();

            List<Guest> found = em.createQuery(
                            "SELECT g FROM Guest g WHERE g.contactNumber = :cn", Guest.class)
                    .setParameter("cn", contact)
                    .setMaxResults(1)
                    .getResultList();

            if (!found.isEmpty()) {
                guest = found.get(0);
                alreadyRegistered = true;
            } else {
                if (isBlank(req.guestName)) throw new BadRequestException("guestName is required");

                guest = new Guest();
                guest.setGuestName(req.guestName.trim());
                guest.setAddress(req.address);
                guest.setContactNumber(contact);
                em.persist(guest);
            }
        }

        Reservation r = new Reservation();
        r.setReservationNo(req.reservationNo.trim());
        r.setGuest(guest);
        r.setRoomType(roomType);
        r.setCheckIn(in);
        r.setCheckOut(out);

        em.persist(r);

        return new CreateResult(r, alreadyRegistered);
    }

    public Reservation getReservation(String reservationNo) {
        Reservation r = em.find(Reservation.class, reservationNo.trim());
        if (r == null) throw new NotFoundException("Reservation not found: " + reservationNo);
        return r;
    }

    // ✅ ADMIN: update reservation
    @Transactional
    public Reservation updateReservation(String reservationNo, ReservationCreateRequest req) {
        if (isBlank(reservationNo)) throw new BadRequestException("reservationNo is required");
        if (req == null) throw new BadRequestException("Request body is required");

        Reservation r = em.find(Reservation.class, reservationNo.trim());
        if (r == null) throw new NotFoundException("Reservation not found: " + reservationNo);

        if (!isBlank(req.roomType)) {
            RoomType rt = em.find(RoomType.class, req.roomType.trim());
            if (rt == null) throw new NotFoundException("Room type not found: " + req.roomType);
            r.setRoomType(rt);
        }

        LocalDate in = r.getCheckIn();
        LocalDate out = r.getCheckOut();

        if (!isBlank(req.checkIn)) in = LocalDate.parse(req.checkIn.trim());
        if (!isBlank(req.checkOut)) out = LocalDate.parse(req.checkOut.trim());

        if (in == null || out == null || !out.isAfter(in)) {
            throw new BadRequestException("checkOut must be after checkIn");
        }
        r.setCheckIn(in);
        r.setCheckOut(out);

        if (req.guestId != null) {
            Guest g = em.find(Guest.class, req.guestId);
            if (g == null) throw new NotFoundException("Guest not found: " + req.guestId);
            r.setGuest(g);
            return r;
        }

        Guest g = r.getGuest();

        if (!isBlank(req.contactNumber)) {
            String cn = req.contactNumber.trim();

            Long dup = em.createQuery(
                            "SELECT COUNT(x) FROM Guest x WHERE x.contactNumber = :cn AND x.guestId <> :id",
                            Long.class)
                    .setParameter("cn", cn)
                    .setParameter("id", g.getGuestId())
                    .getSingleResult();

            if (dup != null && dup > 0) {
                throw new BadRequestException("Contact number already exists for another guest");
            }
            g.setContactNumber(cn);
        }

        if (!isBlank(req.guestName)) {
            g.setGuestName(req.guestName.trim());
        }

        if (req.address != null) {
            g.setAddress(req.address.trim());
        }

        return r;
    }

    // ✅ ADMIN: delete reservation
    @Transactional
    public void deleteReservation(String reservationNo) {
        if (isBlank(reservationNo)) throw new BadRequestException("reservationNo is required");
        Reservation r = em.find(Reservation.class, reservationNo.trim());
        if (r == null) throw new NotFoundException("Reservation not found: " + reservationNo);
        em.remove(r);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}