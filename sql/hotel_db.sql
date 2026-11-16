CREATE DATABASE IF NOT EXISTS hotel_db;
USE hotel_db;

CREATE TABLE rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    room_type VARCHAR(20) NOT NULL,
    base_tariff DOUBLE NOT NULL,
    is_available BOOLEAN DEFAULT TRUE
);

CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) UNIQUE NOT NULL,
    email VARCHAR(100)
);

CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    room_id INT,
    check_in DATE,
    check_out DATE,
    total_bill DOUBLE DEFAULT 0,
    status VARCHAR(20) DEFAULT 'BOOKED',
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

-- Sample rooms
INSERT INTO rooms (room_number, room_type, base_tariff, is_available) VALUES
('101', 'Standard', 1500, TRUE),
('102', 'Standard', 1500, TRUE),
('201', 'Deluxe', 2500, TRUE),
('202', 'Deluxe', 2500, TRUE),
('301', 'Suite', 5000, TRUE);

-- Stored procedure for bill generation (CallableStatement - Unit 4)
DELIMITER $$
CREATE PROCEDURE generate_bill(IN b_id INT, OUT final_bill DOUBLE)
BEGIN
    DECLARE days INT;
    DECLARE tariff DOUBLE;
    DECLARE tax DOUBLE;
    SELECT DATEDIFF(check_out, check_in), r.base_tariff
      INTO days, tariff
      FROM bookings b JOIN rooms r ON b.room_id = r.room_id
      WHERE b.booking_id = b_id;
    SET tax = tariff * days * 0.18;
    SET final_bill = (tariff * days) + tax;
    UPDATE bookings SET total_bill = final_bill WHERE booking_id = b_id;
END$$
DELIMITER ;