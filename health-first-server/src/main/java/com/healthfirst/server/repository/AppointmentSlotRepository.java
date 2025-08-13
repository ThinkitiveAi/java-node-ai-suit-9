package com.healthfirst.server.repository;

import com.healthfirst.server.entity.AppointmentSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {

    Optional<AppointmentSlot> findByUuid(UUID uuid);

    Optional<AppointmentSlot> findByBookingReference(String bookingReference);

    @Query("SELECT as FROM AppointmentSlot as WHERE as.provider.uuid = :providerId")
    List<AppointmentSlot> findByProviderUuid(@Param("providerId") UUID providerId);

    @Query("SELECT as FROM AppointmentSlot as WHERE as.patient.uuid = :patientId")
    List<AppointmentSlot> findByPatientUuid(@Param("patientId") UUID patientId);

    @Query("SELECT as FROM AppointmentSlot as WHERE as.provider.uuid = :providerId " +
           "AND as.slotStartTime >= :startTime " +
           "AND as.slotEndTime <= :endTime")
    List<AppointmentSlot> findByProviderAndTimeRange(
            @Param("providerId") UUID providerId,
            @Param("startTime") ZonedDateTime startTime,
            @Param("endTime") ZonedDateTime endTime
    );

    @Query("SELECT as FROM AppointmentSlot as WHERE " +
           "as.slotStartTime >= :startTime " +
           "AND as.slotEndTime <= :endTime " +
           "AND as.status = 'AVAILABLE' " +
           "AND (:providerId IS NULL OR as.provider.uuid = :providerId) " +
           "AND (:appointmentType IS NULL OR as.appointmentType = :appointmentType)")
    Page<AppointmentSlot> findAvailableSlots(
            @Param("startTime") ZonedDateTime startTime,
            @Param("endTime") ZonedDateTime endTime,
            @Param("providerId") UUID providerId,
            @Param("appointmentType") String appointmentType,
            Pageable pageable
    );

    @Query("SELECT as FROM AppointmentSlot as WHERE " +
           "as.provider.uuid = :providerId " +
           "AND as.slotStartTime >= :startTime " +
           "AND as.slotStartTime < :endTime " +
           "AND as.status = 'AVAILABLE'")
    List<AppointmentSlot> findAvailableSlotsForProvider(
            @Param("providerId") UUID providerId,
            @Param("startTime") ZonedDateTime startTime,
            @Param("endTime") ZonedDateTime endTime
    );

    @Query("SELECT COUNT(as) FROM AppointmentSlot as WHERE " +
           "as.provider.uuid = :providerId " +
           "AND as.slotStartTime >= :startTime " +
           "AND as.slotStartTime < :endTime " +
           "AND as.status = 'BOOKED'")
    long countBookedSlotsForProvider(
            @Param("providerId") UUID providerId,
            @Param("startTime") ZonedDateTime startTime,
            @Param("endTime") ZonedDateTime endTime
    );

    @Query("SELECT as FROM AppointmentSlot as WHERE " +
           "as.patient.uuid = :patientId " +
           "AND as.slotStartTime >= :currentTime " +
           "ORDER BY as.slotStartTime ASC")
    List<AppointmentSlot> findUpcomingAppointmentsForPatient(
            @Param("patientId") UUID patientId,
            @Param("currentTime") ZonedDateTime currentTime
    );

    @Query("SELECT as FROM AppointmentSlot as WHERE " +
           "as.provider.uuid = :providerId " +
           "AND as.slotStartTime >= :currentTime " +
           "ORDER BY as.slotStartTime ASC")
    List<AppointmentSlot> findUpcomingAppointmentsForProvider(
            @Param("providerId") UUID providerId,
            @Param("currentTime") ZonedDateTime currentTime
    );

    @Query("SELECT as FROM AppointmentSlot as WHERE " +
           "as.availability.uuid = :availabilityId")
    List<AppointmentSlot> findByAvailabilityUuid(@Param("availabilityId") UUID availabilityId);

    @Query("SELECT as FROM AppointmentSlot as WHERE " +
           "as.status = 'BOOKED' " +
           "AND as.slotStartTime >= :startTime " +
           "AND as.slotStartTime < :endTime")
    List<AppointmentSlot> findBookedSlotsInTimeRange(
            @Param("startTime") ZonedDateTime startTime,
            @Param("endTime") ZonedDateTime endTime
    );
} 