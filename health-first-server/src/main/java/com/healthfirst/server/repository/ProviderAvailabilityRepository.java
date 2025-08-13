package com.healthfirst.server.repository;

import com.healthfirst.server.entity.ProviderAvailability;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderAvailabilityRepository extends JpaRepository<ProviderAvailability, Long> {

    Optional<ProviderAvailability> findByUuid(UUID uuid);

    @Query("SELECT pa FROM ProviderAvailability pa WHERE pa.provider.uuid = :providerId")
    List<ProviderAvailability> findByProviderUuid(@Param("providerId") UUID providerId);

    @Query("SELECT pa FROM ProviderAvailability pa WHERE pa.provider.uuid = :providerId " +
           "AND pa.date BETWEEN :startDate AND :endDate " +
           "AND (:status IS NULL OR pa.status = :status) " +
           "AND (:appointmentType IS NULL OR pa.appointmentType = :appointmentType)")
    Page<ProviderAvailability> findByProviderAndDateRange(
            @Param("providerId") UUID providerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") ProviderAvailability.AvailabilityStatus status,
            @Param("appointmentType") ProviderAvailability.AppointmentType appointmentType,
            Pageable pageable
    );

    @Query("SELECT pa FROM ProviderAvailability pa WHERE " +
           "pa.date BETWEEN :startDate AND :endDate " +
           "AND pa.status = 'AVAILABLE' " +
           "AND (:specialization IS NULL OR LOWER(pa.provider.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))) " +
           "AND (:location IS NULL OR LOWER(pa.location.address) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "AND (:appointmentType IS NULL OR pa.appointmentType = :appointmentType) " +
           "AND (:insuranceAccepted IS NULL OR pa.pricing.insuranceAccepted = :insuranceAccepted) " +
           "AND (:maxPrice IS NULL OR pa.pricing.baseFee <= :maxPrice)")
    Page<ProviderAvailability> searchAvailableSlots(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("specialization") String specialization,
            @Param("location") String location,
            @Param("appointmentType") ProviderAvailability.AppointmentType appointmentType,
            @Param("insuranceAccepted") Boolean insuranceAccepted,
            @Param("maxPrice") java.math.BigDecimal maxPrice,
            Pageable pageable
    );

    @Query("SELECT pa FROM ProviderAvailability pa WHERE " +
           "pa.provider.uuid = :providerId " +
           "AND pa.date = :date " +
           "AND pa.startTime < :endTime " +
           "AND pa.endTime > :startTime")
    List<ProviderAvailability> findOverlappingSlots(
            @Param("providerId") UUID providerId,
            @Param("date") LocalDate date,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endTime") java.time.LocalTime endTime
    );

    @Query("SELECT COUNT(pa) FROM ProviderAvailability pa WHERE " +
           "pa.provider.uuid = :providerId " +
           "AND pa.date BETWEEN :startDate AND :endDate " +
           "AND pa.status = 'AVAILABLE'")
    long countAvailableSlotsByProviderAndDateRange(
            @Param("providerId") UUID providerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT pa FROM ProviderAvailability pa WHERE " +
           "pa.isRecurring = true " +
           "AND pa.recurrenceEndDate >= :currentDate " +
           "AND pa.provider.uuid = :providerId")
    List<ProviderAvailability> findActiveRecurringSlots(
            @Param("providerId") UUID providerId,
            @Param("currentDate") LocalDate currentDate
    );

    @Query("SELECT DISTINCT pa.provider.specialization FROM ProviderAvailability pa " +
           "WHERE pa.status = 'AVAILABLE' AND pa.date >= :currentDate")
    List<String> findAvailableSpecializations(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT pa FROM ProviderAvailability pa WHERE " +
           "pa.provider.uuid = :providerId " +
           "AND pa.date >= :currentDate " +
           "ORDER BY pa.date ASC, pa.startTime ASC")
    List<ProviderAvailability> findUpcomingSlots(
            @Param("providerId") UUID providerId,
            @Param("currentDate") LocalDate currentDate
    );
} 