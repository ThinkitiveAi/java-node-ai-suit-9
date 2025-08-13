package com.healthfirst.server.service;

import com.healthfirst.server.dto.ProviderAvailabilityRequest;
import com.healthfirst.server.dto.ProviderAvailabilityResponse;
import com.healthfirst.server.entity.AppointmentSlot;
import com.healthfirst.server.entity.Location;
import com.healthfirst.server.entity.Pricing;
import com.healthfirst.server.entity.Provider;
import com.healthfirst.server.entity.ProviderAvailability;
import com.healthfirst.server.repository.AppointmentSlotRepository;
import com.healthfirst.server.repository.ProviderAvailabilityRepository;
import com.healthfirst.server.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProviderAvailabilityService {

    @Autowired
    private ProviderAvailabilityRepository availabilityRepository;

    @Autowired
    private AppointmentSlotRepository appointmentSlotRepository;

    @Autowired
    private ProviderRepository providerRepository;

    public ProviderAvailabilityResponse createAvailability(UUID providerId, ProviderAvailabilityRequest request) {
        // Validate provider exists
        Provider provider = providerRepository.findByUuid(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));

        // Validate time range
        LocalTime startTime = request.getStartTimeAsLocalTime();
        LocalTime endTime = request.getEndTimeAsLocalTime();
        
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Check for overlapping slots
        List<ProviderAvailability> overlappingSlots = availabilityRepository.findOverlappingSlots(
                providerId, request.getDate(), startTime, endTime);
        
        if (!overlappingSlots.isEmpty()) {
            throw new IllegalArgumentException("Time slot overlaps with existing availability");
        }

        // Create availability
        ProviderAvailability availability = new ProviderAvailability();
        availability.setProvider(provider);
        availability.setDate(request.getDate());
        availability.setStartTime(startTime);
        availability.setEndTime(endTime);
        availability.setTimezone(request.getTimezone());
        availability.setSlotDuration(request.getSlotDuration());
        availability.setBreakDuration(request.getBreakDuration());
        availability.setIsRecurring(request.getIsRecurring());
        availability.setRecurrencePattern(request.getRecurrencePattern());
        availability.setRecurrenceEndDate(request.getRecurrenceEndDate());
        availability.setAppointmentType(request.getAppointmentType());

        // Set location
        if (request.getLocation() != null) {
            Location location = new Location();
            location.setType(request.getLocation().getType());
            location.setAddress(request.getLocation().getAddress());
            location.setRoomNumber(request.getLocation().getRoomNumber());
            availability.setLocation(location);
        }

        // Set pricing
        if (request.getPricing() != null) {
            Pricing pricing = new Pricing();
            pricing.setBaseFee(request.getPricing().getBaseFee());
            pricing.setInsuranceAccepted(request.getPricing().getInsuranceAccepted());
            pricing.setCurrency(request.getPricing().getCurrency());
            availability.setPricing(pricing);
        }

        availability.setSpecialRequirements(request.getSpecialRequirements());
        availability.setNotes(request.getNotes());

        ProviderAvailability savedAvailability = availabilityRepository.save(availability);

        // Generate appointment slots
        List<AppointmentSlot> slots = generateAppointmentSlots(savedAvailability);
        appointmentSlotRepository.saveAll(slots);

        return new ProviderAvailabilityResponse(savedAvailability);
    }

    public Page<ProviderAvailabilityResponse> getProviderAvailability(
            UUID providerId, 
            LocalDate startDate, 
            LocalDate endDate,
            ProviderAvailability.AvailabilityStatus status,
            ProviderAvailability.AppointmentType appointmentType,
            int page, 
            int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProviderAvailability> availabilities = availabilityRepository.findByProviderAndDateRange(
                providerId, startDate, endDate, status, appointmentType, pageable);
        
        return availabilities.map(ProviderAvailabilityResponse::new);
    }

    public ProviderAvailabilityResponse updateAvailability(UUID availabilityId, ProviderAvailabilityRequest request) {
        ProviderAvailability availability = availabilityRepository.findByUuid(availabilityId)
                .orElseThrow(() -> new IllegalArgumentException("Availability not found"));

        // Validate time range
        LocalTime startTime = request.getStartTimeAsLocalTime();
        LocalTime endTime = request.getEndTimeAsLocalTime();
        
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Check for overlapping slots (excluding current availability)
        List<ProviderAvailability> overlappingSlots = availabilityRepository.findOverlappingSlots(
                availability.getProvider().getUuid(), request.getDate(), startTime, endTime);
        
        overlappingSlots = overlappingSlots.stream()
                .filter(slot -> !slot.getUuid().equals(availabilityId))
                .collect(Collectors.toList());
        
        if (!overlappingSlots.isEmpty()) {
            throw new IllegalArgumentException("Time slot overlaps with existing availability");
        }

        // Update availability
        availability.setDate(request.getDate());
        availability.setStartTime(startTime);
        availability.setEndTime(endTime);
        availability.setTimezone(request.getTimezone());
        availability.setSlotDuration(request.getSlotDuration());
        availability.setBreakDuration(request.getBreakDuration());
        availability.setAppointmentType(request.getAppointmentType());

        // Update location
        if (request.getLocation() != null) {
            Location location = availability.getLocation();
            if (location == null) {
                location = new Location();
            }
            location.setType(request.getLocation().getType());
            location.setAddress(request.getLocation().getAddress());
            location.setRoomNumber(request.getLocation().getRoomNumber());
            availability.setLocation(location);
        }

        // Update pricing
        if (request.getPricing() != null) {
            Pricing pricing = availability.getPricing();
            if (pricing == null) {
                pricing = new Pricing();
            }
            pricing.setBaseFee(request.getPricing().getBaseFee());
            pricing.setInsuranceAccepted(request.getPricing().getInsuranceAccepted());
            pricing.setCurrency(request.getPricing().getCurrency());
            availability.setPricing(pricing);
        }

        availability.setSpecialRequirements(request.getSpecialRequirements());
        availability.setNotes(request.getNotes());

        ProviderAvailability savedAvailability = availabilityRepository.save(availability);

        // Regenerate appointment slots
        List<AppointmentSlot> existingSlots = appointmentSlotRepository.findByAvailabilityUuid(availabilityId);
        appointmentSlotRepository.deleteAll(existingSlots);
        
        List<AppointmentSlot> newSlots = generateAppointmentSlots(savedAvailability);
        appointmentSlotRepository.saveAll(newSlots);

        return new ProviderAvailabilityResponse(savedAvailability);
    }

    public void deleteAvailability(UUID availabilityId, Boolean deleteRecurring, String reason) {
        ProviderAvailability availability = availabilityRepository.findByUuid(availabilityId)
                .orElseThrow(() -> new IllegalArgumentException("Availability not found"));

        if (Boolean.TRUE.equals(deleteRecurring) && Boolean.TRUE.equals(availability.getIsRecurring())) {
            // Delete all recurring instances
            List<ProviderAvailability> recurringSlots = availabilityRepository.findActiveRecurringSlots(
                    availability.getProvider().getUuid(), LocalDate.now());
            
            List<UUID> availabilityIds = recurringSlots.stream()
                    .map(ProviderAvailability::getUuid)
                    .collect(Collectors.toList());
            
            // Delete appointment slots first
            for (UUID id : availabilityIds) {
                List<AppointmentSlot> slots = appointmentSlotRepository.findByAvailabilityUuid(id);
                appointmentSlotRepository.deleteAll(slots);
            }
            
            // Delete availability slots
            availabilityRepository.deleteAll(recurringSlots);
        } else {
            // Delete only this instance
            List<AppointmentSlot> slots = appointmentSlotRepository.findByAvailabilityUuid(availabilityId);
            appointmentSlotRepository.deleteAll(slots);
            availabilityRepository.delete(availability);
        }
    }

    public Page<ProviderAvailabilityResponse> searchAvailableSlots(
            LocalDate startDate,
            LocalDate endDate,
            String specialization,
            String location,
            ProviderAvailability.AppointmentType appointmentType,
            Boolean insuranceAccepted,
            BigDecimal maxPrice,
            int page,
            int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProviderAvailability> availabilities = availabilityRepository.searchAvailableSlots(
                startDate, endDate, specialization, location, appointmentType, 
                insuranceAccepted, maxPrice, pageable);
        
        return availabilities.map(ProviderAvailabilityResponse::new);
    }

    public List<String> getAvailableSpecializations() {
        return availabilityRepository.findAvailableSpecializations(LocalDate.now());
    }

    public List<ProviderAvailabilityResponse> getUpcomingSlots(UUID providerId) {
        List<ProviderAvailability> availabilities = availabilityRepository.findUpcomingSlots(
                providerId, LocalDate.now());
        
        return availabilities.stream()
                .map(ProviderAvailabilityResponse::new)
                .collect(Collectors.toList());
    }

    private List<AppointmentSlot> generateAppointmentSlots(ProviderAvailability availability) {
        List<AppointmentSlot> slots = new ArrayList<>();
        
        LocalTime currentTime = availability.getStartTime();
        ZoneId zoneId = ZoneId.of(availability.getTimezone());
        
        while (currentTime.isBefore(availability.getEndTime())) {
            // Calculate slot end time
            LocalTime slotEndTime = currentTime.plusMinutes(availability.getSlotDuration());
            
            // Skip if slot would extend beyond availability end time
            if (slotEndTime.isAfter(availability.getEndTime())) {
                break;
            }
            
            // Create appointment slot
            AppointmentSlot slot = new AppointmentSlot();
            slot.setAvailability(availability);
            slot.setProvider(availability.getProvider());
            slot.setAppointmentType(availability.getAppointmentType().name());
            
            // Convert to ZonedDateTime
            ZonedDateTime slotStart = availability.getDate().atTime(currentTime).atZone(zoneId);
            ZonedDateTime slotEnd = availability.getDate().atTime(slotEndTime).atZone(zoneId);
            
            slot.setSlotStartTime(slotStart);
            slot.setSlotEndTime(slotEnd);
            slot.setStatus(AppointmentSlot.SlotStatus.AVAILABLE);
            
            slots.add(slot);
            
            // Move to next slot (including break duration)
            currentTime = slotEndTime.plusMinutes(availability.getBreakDuration());
        }
        
        return slots;
    }

    public long getAvailableSlotsCount(UUID providerId, LocalDate startDate, LocalDate endDate) {
        return availabilityRepository.countAvailableSlotsByProviderAndDateRange(providerId, startDate, endDate);
    }
} 