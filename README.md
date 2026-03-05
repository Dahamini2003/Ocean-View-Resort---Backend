# Ocean View Resort - Java EE Backend (Payara 5 + MySQL)

## What is this?
A starter backend for a Room Reservation System using:
- Java EE 8 (JAX-RS + JPA + CDI)
- MySQL
- Payara Server 5 (Community)

## 1) Create MySQL tables
Run this in MySQL:

```sql
CREATE DATABASE IF NOT EXISTS oceanview_db;
USE oceanview_db;

CREATE TABLE users (
  username VARCHAR(50) PRIMARY KEY,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL
);

CREATE TABLE room_types (
  type_name VARCHAR(30) PRIMARY KEY,
  rate_per_night DECIMAL(10,2) NOT NULL
);

CREATE TABLE guests (
  guest_id INT AUTO_INCREMENT PRIMARY KEY,
  guest_name VARCHAR(100) NOT NULL,
  address VARCHAR(200),
  contact_number VARCHAR(20) NOT NULL
);

CREATE TABLE reservations (
  reservation_no VARCHAR(30) PRIMARY KEY,
  guest_id INT NOT NULL,
  check_in DATE NOT NULL,
  check_out DATE NOT NULL,
  room_type_name VARCHAR(30) NOT NULL,
  CONSTRAINT fk_res_guest FOREIGN KEY (guest_id) REFERENCES guests(guest_id),
  CONSTRAINT fk_res_roomtype FOREIGN KEY (room_type_name) REFERENCES room_types(type_name)
);

CREATE TABLE reservation_payments (
  payment_id INT AUTO_INCREMENT PRIMARY KEY,
  reservation_no VARCHAR(30) NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  payment_method VARCHAR(20) NOT NULL,
  payment_status VARCHAR(20) NOT NULL,
  paid_at DATETIME NULL,
  reference_no VARCHAR(50) NULL,
  notes VARCHAR(200) NULL,
  CONSTRAINT fk_payment_reservation
    FOREIGN KEY (reservation_no) REFERENCES reservations(reservation_no)
    ON DELETE CASCADE
);

INSERT INTO room_types(type_name, rate_per_night) VALUES
('Standard', 12000.00),
('Deluxe', 18000.00),
('Suite', 30000.00);
```

## 2) Payara JDBC Resource
1) Put MySQL connector jar file into:
   `payara5/glassfish/domains/domain1/lib/`
2) Restart Payara.
3) Create JDBC connection pool (MysqlDataSource)
4) Create JDBC resource (JNDI):
   `jdbc/oceanview`

## 3) Build and deploy
```bash
mvn clean package
```
Deploy: `target/oceanview.war`

## 4) Test endpoints
- GET  /oceanview/api/help
- POST /oceanview/api/auth/login
- GET  /oceanview/api/room-types

After login, use header:
`Authorization: Bearer <token>`

- POST /oceanview/api/reservations
- GET  /oceanview/api/reservations/{reservationNo}
- GET  /oceanview/api/reservations/{reservationNo}/bill
- POST /oceanview/api/reservations/{reservationNo}/payments
- GET  /oceanview/api/reservations/{reservationNo}/payments
