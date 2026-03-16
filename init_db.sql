CREATE DATABASE IF NOT EXISTS busbooking;
USE busbooking;

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS buses (
    bus_id INT AUTO_INCREMENT PRIMARY KEY,
    bus_number VARCHAR(50) NOT NULL,
    bus_name VARCHAR(100) NOT NULL,
    total_seats INT NOT NULL
);

CREATE TABLE IF NOT EXISTS routes (
    route_id INT AUTO_INCREMENT PRIMARY KEY,
    source VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS schedules (
    schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    bus_id INT,
    route_id INT,
    travel_date DATE NOT NULL,
    departure_time TIME NOT NULL,
    fare DOUBLE NOT NULL,
    FOREIGN KEY (bus_id) REFERENCES buses(bus_id),
    FOREIGN KEY (route_id) REFERENCES routes(route_id)
);

CREATE TABLE IF NOT EXISTS seats (
    schedule_id INT,
    seat_number INT,
    is_booked BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (schedule_id, seat_number),
    FOREIGN KEY (schedule_id) REFERENCES schedules(schedule_id)
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    schedule_id INT,
    seat_number INT,
    passenger_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (schedule_id) REFERENCES schedules(schedule_id),
    FOREIGN KEY (schedule_id, seat_number) REFERENCES seats(schedule_id, seat_number)
);

-- Insert admin user
INSERT IGNORE INTO users (username, email, password, role) VALUES ('admin', 'admin@bus.com', 'admin123', 'admin');
