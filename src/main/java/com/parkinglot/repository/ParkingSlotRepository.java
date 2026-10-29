package com.parkinglot.repository;

import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.entity.SlotStatus;
import com.parkinglot.entity.SlotType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    Optional<ParkingSlot> findBySlotNumberIgnoreCase(String slotNumber);

    List<ParkingSlot> findByStatus(SlotStatus status);

    List<ParkingSlot> findByStatusAndSlotType(SlotStatus status, SlotType slotType);

    Optional<ParkingSlot> findFirstByStatusOrderByIdAsc(SlotStatus status);

    Optional<ParkingSlot> findFirstByStatusAndSlotTypeOrderByIdAsc(SlotStatus status, SlotType slotType);

    long countByStatus(SlotStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ParkingSlot s WHERE s.id = :id")
    Optional<ParkingSlot> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ParkingSlot s WHERE s.slotNumber = :slotNumber")
    Optional<ParkingSlot> findBySlotNumberForUpdate(@Param("slotNumber") String slotNumber);
}
