-- ============================================================
-- Initial Data Seeding for Parking Lot Management System
-- ============================================================

INSERT INTO parking_slots (slot_number, slot_type, status, version, created_at, updated_at)
SELECT 'A1', 'COMPACT', 'AVAILABLE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM parking_slots WHERE slot_number = 'A1');

INSERT INTO parking_slots (slot_number, slot_type, status, version, created_at, updated_at)
SELECT 'A2', 'COMPACT', 'AVAILABLE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM parking_slots WHERE slot_number = 'A2');

INSERT INTO parking_slots (slot_number, slot_type, status, version, created_at, updated_at)
SELECT 'A3', 'STANDARD', 'AVAILABLE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM parking_slots WHERE slot_number = 'A3');

INSERT INTO parking_slots (slot_number, slot_type, status, version, created_at, updated_at)
SELECT 'A4', 'STANDARD', 'AVAILABLE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM parking_slots WHERE slot_number = 'A4');

INSERT INTO parking_slots (slot_number, slot_type, status, version, created_at, updated_at)
SELECT 'A5', 'STANDARD', 'AVAILABLE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM parking_slots WHERE slot_number = 'A5');

INSERT INTO parking_slots (slot_number, slot_type, status, version, created_at, updated_at)
SELECT 'B1', 'STANDARD', 'AVAILABLE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM parking_slots WHERE slot_number = 'B1');

INSERT INTO parking_slots (slot_number, slot_type, status, version, created_at, updated_at)
SELECT 'B2', 'STANDARD', 'AVAILABLE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM parking_slots WHERE slot_number = 'B2');

INSERT INTO parking_slots (slot_number, slot_type, status, version, created_at, updated_at)
SELECT 'B3', 'LARGE', 'AVAILABLE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM parking_slots WHERE slot_number = 'B3');

INSERT INTO parking_slots (slot_number, slot_type, status, version, created_at, updated_at)
SELECT 'B4', 'LARGE', 'AVAILABLE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM parking_slots WHERE slot_number = 'B4');

INSERT INTO parking_slots (slot_number, slot_type, status, version, created_at, updated_at)
SELECT 'EV1', 'EV', 'AVAILABLE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM parking_slots WHERE slot_number = 'EV1');

INSERT INTO parking_slots (slot_number, slot_type, status, version, created_at, updated_at)
SELECT 'EV2', 'EV', 'AVAILABLE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM parking_slots WHERE slot_number = 'EV2');
