package com.healthfirst.server.dto;

import com.healthfirst.server.entity.Location;
import com.healthfirst.server.entity.Pricing;
import com.healthfirst.server.entity.ProviderAvailability;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Schema(description = "Provider availability creation request")
public class ProviderAvailabilityRequest {

    @Schema(description = "Date for availability", example = "2024-02-15")
    @NotNull(message = "Date is required")
    private LocalDate date;

    @Schema(description = "Start time in HH:mm format", example = "09:00")
    @NotNull(message = "Start time is required")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Start time must be in HH:mm format")
    private String startTime;

    @Schema(description = "End time in HH:mm format", example = "17:00")
    @NotNull(message = "End time is required")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "End time must be in HH:mm format")
    private String endTime;

    @Schema(description = "Timezone", example = "America/New_York")
    @NotBlank(message = "Timezone is required")
    private String timezone;

    @Schema(description = "Slot duration in minutes", example = "30")
    @Min(value = 15, message = "Slot duration must be at least 15 minutes")
    @Max(value = 480, message = "Slot duration cannot exceed 8 hours")
    private Integer slotDuration = 30;

    @Schema(description = "Break duration in minutes", example = "15")
    @Min(value = 0, message = "Break duration cannot be negative")
    @Max(value = 120, message = "Break duration cannot exceed 2 hours")
    private Integer breakDuration = 0;

    @Schema(description = "Whether this is a recurring availability", example = "true")
    private Boolean isRecurring = false;

    @Schema(description = "Recurrence pattern", example = "WEEKLY")
    private ProviderAvailability.RecurrencePattern recurrencePattern;

    @Schema(description = "Recurrence end date", example = "2024-08-15")
    private LocalDate recurrenceEndDate;

    @Schema(description = "Appointment type", example = "CONSULTATION")
    @NotNull(message = "Appointment type is required")
    private ProviderAvailability.AppointmentType appointmentType;

    @Schema(description = "Location information")
    @Valid
    @NotNull(message = "Location is required")
    private LocationRequest location;

    @Schema(description = "Pricing information")
    @Valid
    private PricingRequest pricing;

    @Schema(description = "Special requirements", example = "[\"fasting_required\", \"bring_insurance_card\"]")
    private List<String> specialRequirements;

    @Schema(description = "Notes", example = "Standard consultation slots")
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    // Constructors
    public ProviderAvailabilityRequest() {}

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
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

    public ProviderAvailability.AppointmentType getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(ProviderAvailability.AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    public LocationRequest getLocation() {
        return location;
    }

    public void setLocation(LocationRequest location) {
        this.location = location;
    }

    public PricingRequest getPricing() {
        return pricing;
    }

    public void setPricing(PricingRequest pricing) {
        this.pricing = pricing;
    }

    public List<String> getSpecialRequirements() {
        return specialRequirements;
    }

    public void setSpecialRequirements(List<String> specialRequirements) {
        this.specialRequirements = specialRequirements;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Helper method to convert string time to LocalTime
    public LocalTime getStartTimeAsLocalTime() {
        return LocalTime.parse(startTime);
    }

    public LocalTime getEndTimeAsLocalTime() {
        return LocalTime.parse(endTime);
    }

    // Inner classes for nested objects
    @Schema(description = "Location information")
    public static class LocationRequest {
        @Schema(description = "Location type", example = "CLINIC")
        @NotNull(message = "Location type is required")
        private Location.LocationType type;

        @Schema(description = "Address", example = "123 Medical Center Dr, New York, NY 10001")
        private String address;

        @Schema(description = "Room number", example = "Room 205")
        private String roomNumber;

        // Constructors
        public LocationRequest() {}

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
    public static class PricingRequest {
        @Schema(description = "Base fee", example = "150.00")
        @DecimalMin(value = "0.0", inclusive = false, message = "Base fee must be greater than 0")
        private BigDecimal baseFee;

        @Schema(description = "Whether insurance is accepted", example = "true")
        @NotNull(message = "Insurance accepted status is required")
        private Boolean insuranceAccepted = false;

        @Schema(description = "Currency", example = "USD")
        @NotNull(message = "Currency is required")
        private String currency = "USD";

        // Constructors
        public PricingRequest() {}

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