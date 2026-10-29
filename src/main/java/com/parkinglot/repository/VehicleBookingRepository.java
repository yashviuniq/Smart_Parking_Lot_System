package com.parkinglot.repository;

import com.parkinglot.entity.BookingStatus;
import com.parkinglot.entity.VehicleBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleBookingRepository extends JpaRepository<VehicleBooking, Long> {

    List<VehicleBooking> findByBookingStatus(BookingStatus bookingStatus);

    Optional<VehicleBooking> findFirstByVehicleNumberIgnoreCaseAndBookingStatus(String vehicleNumber, BookingStatus bookingStatus);

    Optional<VehicleBooking> findFirstByParkingSlotIdAndBookingStatus(Long parkingSlotId, BookingStatus bookingStatus);

    boolean existsByVehicleNumberIgnoreCaseAndBookingStatus(String vehicleNumber, BookingStatus bookingStatus);

    List<VehicleBooking> findByExitTimeBetweenAndBookingStatus(LocalDateTime start, LocalDateTime end, BookingStatus bookingStatus);

    @Query("SELECT COALESCE(SUM(b.feeAmount), 0) FROM VehicleBooking b WHERE b.bookingStatus = :status AND b.exitTime >= :start AND b.exitTime < :end")
    BigDecimal calculateTotalRevenueBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("status") BookingStatus status
    );

    @Query("SELECT COUNT(b) FROM VehicleBooking b WHERE b.bookingStatus = :status AND b.exitTime >= :start AND b.exitTime < :end")
    long countCompletedBookingsBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("status") BookingStatus status
    );
}
