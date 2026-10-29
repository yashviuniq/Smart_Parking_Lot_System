-- ============================================================
-- Schema Definition for Parking Lot Management System
-- Compatible with MySQL 8.0 & PostgreSQL
-- ============================================================

-- Table: parking_slots
CREATE TABLE IF NOT EXISTS parking_slots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    slot_number VARCHAR(20) NOT NULL UNIQUE,
    slot_type VARCHAR(30) NOT NULL DEFAULT 'STANDARD',
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_slot_status (status),
    INDEX idx_slot_number (slot_number)
);

-- Table: vehicle_bookings
CREATE TABLE IF NOT EXISTS vehicle_bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_number VARCHAR(30) NOT NULL,
    parking_slot_id BIGINT NOT NULL,
    entry_time TIMESTAMP NOT NULL,
    exit_time TIMESTAMP NULL,
    duration_minutes BIGINT NULL,
    fee_amount DECIMAL(10, 2) NULL,
    payment_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    booking_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicle_bookings_slot
        FOREIGN KEY (parking_slot_id)
        REFERENCES parking_slots (id)
        ON DELETE RESTRICT,
    INDEX idx_booking_status (booking_status),
    INDEX idx_vehicle_number (vehicle_number),
    INDEX idx_entry_time (entry_time),
    INDEX idx_exit_time (exit_time)
);
