package com.healthfirst.server.dto;

import com.healthfirst.server.entity.Location;
import com.healthfirst.server.entity.Pricing;
import com.healthfirst.server.entity.ProviderAvailability;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Provider availability response")
public class ProviderAvailabilityResponse {

    @Schema(description = "Availability UUID")
    private UUID uuid;

    @Schema(description = "Provider UUID")
    private UUID providerId;

    @Schema(description = "Provider name")
    private String providerName;

    @Schema(description = "Provider specialization")
    private String specialization;

    @Schema(description = "Date")
    private LocalDate date;

    @Schema(description = "Start time")
    private LocalTime startTime;

    @Schema(description = "End time")
    private LocalTime endTime;

    @Schema(description = "Timezone")
    private String timezone;

    @Schema(description = "Whether this is recurring")
    private Boolean isRecurring;

    @Schema(description = "Recurrence pattern")
    private ProviderAvailability.RecurrencePattern recurrencePattern;

    @Schema(description = "Recurrence end date")
    private LocalDate recurrenceEndDate;

    @Schema(description = "Slot duration in minutes")
    private Integer slotDuration;

    @Schema(description = "Break duration in minutes")
    private Integer breakDuration;

    @Schema(description = "Availability status")
    private ProviderAvailability.AvailabilityStatus status;

    @Schema(description = "Maximum appointments per slot")
    private Integer maxAppointmentsPerSlot;

    @Schema(description = "Current appointments")
    private Integer currentAppointments;

    @Schema(description = "Appointment type")
    private ProviderAvailability.AppointmentType appointmentType;

    @Schema(description = "Location information")
    private LocationResponse location;

    @Schema(description = "Pricing information")
    private PricingResponse pricing;

    @Schema(description = "Notes")
    private String notes;

    @Schema(description = "Special requirements")
    private List<String> specialRequirements;

    @Schema(description = "Created at")
    private ZonedDateTime createdAt;

    @Schema(description = "Updated at")
    private ZonedDateTime updatedAt;

    // Constructors
    public ProviderAvailabilityResponse() {}

    public ProviderAvailabilityResponse(ProviderAvailability availability) {
        this.uuid = availability.getUuid();
        this.providerId = availability.getProvider().getUuid();
        this.providerName = availability.getProvider().getFirstName() + " " + availability.getProvider().getLastName();
        this.specialization = availability.getProvider().getSpecialization();
        this.date = availability.getDate();
        this.startTime = availability.getStartTime();
        this.endTime = availability.getEndTime();
        this.timezone = availability.getTimezone();
        this.isRecurring = availability.getIsRecurring();
        this.recurrencePattern = availability.getRecurrencePattern();
        this.recurrenceEndDate = availability.getRecurrenceEndDate();
        this.slotDuration = availability.getSlotDuration();
        this.breakDuration = availability.getBreakDuration();
        this.status = availability.getStatus();
        this.maxAppointmentsPerSlot = availability.getMaxAppointmentsPerSlot();
        this.currentAppointments = availability.getCurrentAppointments();
        this.appointmentType = availability.getAppointmentType();
        this.location = availability.getLocation() != null ? new LocationResponse(availability.getLocation()) : null;
        this.pricing = availability.getPricing() != null ? new PricingResponse(availability.getPricing()) : null;
        this.notes = availability.getNotes();
        this.specialRequirements = availability.getSpecialRequirements();
        this.createdAt = availability.getCreatedAt();
        this.updatedAt = availability.getUpdatedAt();
    }

    // Getters and Setters
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public void setProviderId(UUID providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Boolean getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public ProviderAvailability.RecurrencePattern getRecurrencePattern() {
        return recurrencePattern;
    }

    public void setRecurrencePattern(ProviderAvailability.RecurrencePattern recurrencePattern) {
        this.recurrencePattern = recurrencePattern;
    }

    public LocalDate getRecurrenceEndDate() {
        return recurrenceEndDate;
    }

    public void setRecurrenceEndDate(LocalDate recurrenceEndDate) {
        this.recurrenceEndDate = recurrenceEndDate;
    }

    public Integer getSlotDuration() {
        return slotDuration;
    }

    public void setSlotDuration(Integer slotDuration) {
        this.slotDuration = slotDuration;
    }

    public Integer getBreakDuration() {
        return breakDuration;
    }

    public void setBreakDuration(Integer breakDuration) {
        this.breakDuration = breakDuration;
    }

    public ProviderAvailability.AvailabilityStatus getStatus() {
        return status;
    }

    public void setStatus(ProviderAvailability.AvailabilityStatus status) {
        this.status = status;
    }

    public Integer getMaxAppointmentsPerSlot() {
        return maxAppointmentsPerSlot;
    }

    public void setMaxAppointmentsPerSlot(Integer maxAppointmentsPerSlot) {
        this.maxAppointmentsPerSlot = maxAppointmentsPerSlot;
    }

    public Integer getCurrentAppointments() {
        return currentAppointments;
    }

    public void setCurrentAppointments(Integer currentAppointments) {
        this.currentAppointments = currentAppointments;
    }

    public ProviderAvailability.AppointmentType getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(ProviderAvailability.AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    public LocationResponse getLocation() {
        return location;
    }

    public void setLocation(LocationResponse location) {
        this.location = location;
    }

    public PricingResponse getPricing() {
        return pricing;
    }

    public void setPricing(PricingResponse pricing) {
        this.pricing = pricing;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<String> getSpecialRequirements() {
        return specialRequirements;
    }

    public void setSpecialRequirements(List<String> specialRequirements) {
        this.specialRequirements = specialRequirements;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Inner classes for nested objects
    @Schema(description = "Location information")
    public static class LocationResponse {
        @Schema(description = "Location type")
        private Location.LocationType type;

        @Schema(description = "Address")
        private String address;

        @Schema(description = "Room number")
        private String roomNumber;

        // Constructors
        public LocationResponse() {}

        public LocationResponse(Location location) {
            this.type = location.getType();
            this.address = location.getAddress();
            this.roomNumber = location.getRoomNumber();
        }

        // Getters and Setters
        public Location.LocationType getType() {
            return type;
        }

        public void setType(Location.LocationType type) {
            this.type = type;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        public void setRoomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
        }
    }

    @Schema(description = "Pricing information")
    public static class PricingResponse {
        @Schema(description = "Base fee")
        private BigDecimal baseFee;

        @Schema(description = "Whether insurance is accepted")
        private Boolean insuranceAccepted;

        @Schema(description = "Currency")
        private String currency;

        // Constructors
        public PricingResponse() {}

        public PricingResponse(Pricing pricing) {
            this.baseFee = pricing.getBaseFee();
            this.insuranceAccepted = pricing.getInsuranceAccepted();
            this.currency = pricing.getCurrency();
        }

        // Getters and Setters
        public BigDecimal getBaseFee() {
            return baseFee;
        }

        public void setBaseFee(BigDecimal baseFee) {
            this.baseFee = baseFee;
        }

        public Boolean getInsuranceAccepted() {
            return insuranceAccepted;
        }

        public void setInsuranceAccepted(Boolean insuranceAccepted) {
            this.insuranceAccepted = insuranceAccepted;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }
} 