package com.healthfirst.server.controller;

import com.healthfirst.server.dto.ApiResponse;
import com.healthfirst.server.dto.ProviderAvailabilityRequest;
import com.healthfirst.server.dto.ProviderAvailabilityResponse;
import com.healthfirst.server.entity.ProviderAvailability;
import com.healthfirst.server.service.ProviderAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/provider")
@CrossOrigin(origins = "*")
@Tag(name = "Provider Availability Management", description = "APIs for managing provider availability")
public class ProviderAvailabilityController {

    @Autowired
    private ProviderAvailabilityService availabilityService;

    @PostMapping("/availability")
    @Operation(summary = "Create provider availability slots")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createAvailability(
            @RequestParam UUID provider_id,
            @Valid @RequestBody ProviderAvailabilityRequest request) {
        try {
            ProviderAvailabilityResponse response = availabilityService.createAvailability(provider_id, request);
            
            Map<String, Object> data = new HashMap<>();
            data.put("availability_id", response.getUuid());
            data.put("provider_id", response.getProviderId());
            data.put("provider_name", response.getProviderName());
            data.put("date", response.getDate());
            data.put("start_time", response.getStartTime());
            data.put("end_time", response.getEndTime());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Availability slots created successfully", data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An error occurred while creating availability slots"));
        }
    }

    @GetMapping("/{provider_id}/availability")
    @Operation(summary = "Get provider availability")
    public ResponseEntity<ApiResponse<Page<ProviderAvailabilityResponse>>> getProviderAvailability(
            @PathVariable("provider_id") UUID providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date,
            @RequestParam(required = false) ProviderAvailability.AvailabilityStatus status,
            @RequestParam(required = false) ProviderAvailability.AppointmentType appointment_type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Page<ProviderAvailabilityResponse> availabilities = availabilityService.getProviderAvailability(
                    providerId, start_date, end_date, status, appointment_type, page, size);
            
            return ResponseEntity.ok(ApiResponse.success("Provider availability retrieved successfully", availabilities));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/availability/{slot_id}")
    @Operation(summary = "Update specific availability slot")
    public ResponseEntity<ApiResponse<ProviderAvailabilityResponse>> updateAvailability(
            @PathVariable("slot_id") UUID slotId,
            @Valid @RequestBody ProviderAvailabilityRequest request) {
        try {
            ProviderAvailabilityResponse response = availabilityService.updateAvailability(slotId, request);
            return ResponseEntity.ok(ApiResponse.success("Availability updated successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/availability/{slot_id}")
    @Operation(summary = "Delete availability slot")
    public ResponseEntity<ApiResponse<String>> deleteAvailability(
            @PathVariable("slot_id") UUID slotId,
            @RequestParam(required = false) Boolean delete_recurring,
            @RequestParam(required = false) String reason) {
        try {
            availabilityService.deleteAvailability(slotId, delete_recurring, reason);
            return ResponseEntity.ok(ApiResponse.success("Availability deleted successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/availability/search")
    @Operation(summary = "Search for available slots")
    public ResponseEntity<ApiResponse<Page<ProviderAvailabilityResponse>>> searchAvailableSlots(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start_date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end_date,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) ProviderAvailability.AppointmentType appointment_type,
            @RequestParam(required = false) Boolean insurance_accepted,
            @RequestParam(required = false) BigDecimal max_price,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            LocalDate searchStartDate = date != null ? date : start_date;
            LocalDate searchEndDate = date != null ? date : end_date;
            
            if (searchStartDate == null || searchEndDate == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Either date or start_date and end_date must be provided"));
            }
            
            Page<ProviderAvailabilityResponse> results = availabilityService.searchAvailableSlots(
                    searchStartDate, searchEndDate, specialization, location, appointment_type,
                    insurance_accepted, max_price, page, size);
            
            return ResponseEntity.ok(ApiResponse.success("Search completed successfully", results));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/availability/specializations")
    @Operation(summary = "Get available specializations")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableSpecializations() {
        List<String> specializations = availabilityService.getAvailableSpecializations();
        return ResponseEntity.ok(ApiResponse.success("Available specializations retrieved successfully", specializations));
    }

    @GetMapping("/{provider_id}/upcoming-slots")
    @Operation(summary = "Get upcoming slots for provider")
    public ResponseEntity<ApiResponse<List<ProviderAvailabilityResponse>>> getUpcomingSlots(
            @PathVariable("provider_id") UUID providerId) {
        try {
            List<ProviderAvailabilityResponse> upcomingSlots = availabilityService.getUpcomingSlots(providerId);
            return ResponseEntity.ok(ApiResponse.success("Upcoming slots retrieved successfully", upcomingSlots));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
} 