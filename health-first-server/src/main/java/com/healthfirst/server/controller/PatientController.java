package com.healthfirst.server.controller;

import com.healthfirst.server.dto.ApiResponse;
import com.healthfirst.server.dto.PatientLoginRequest;
import com.healthfirst.server.dto.PatientLoginResponse;
import com.healthfirst.server.dto.PatientRegistrationRequest;
import com.healthfirst.server.dto.PatientResponse;
import com.healthfirst.server.entity.Patient;
import com.healthfirst.server.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patient")
@CrossOrigin(origins = "*")
@Tag(name = "Patient Management", description = "APIs for managing patient registrations and data")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // POST /api/v1/patient/login
    @PostMapping("/login")
    @Operation(
        summary = "Patient login",
        description = "Authenticate patient with email and password to receive JWT token",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PatientLoginRequest.class),
                examples = @ExampleObject(
                    name = "Valid Patient Login",
                    value = """
                    {
                      "email": "jane.smith@email.com",
                      "password": "SecurePassword123!"
                    }
                    """
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = com.healthfirst.server.dto.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid credentials"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication failed"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<PatientLoginResponse>> loginPatient(
            @Valid @RequestBody PatientLoginRequest request) {
        try {
            PatientLoginResponse response = patientService.loginPatient(request);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An error occurred during login"));
        }
    }

    // POST /api/v1/patient/register
    @PostMapping("/register")
    @Operation(
        summary = "Register a new patient",
        description = "Register a new patient with comprehensive validation and HIPAA compliance",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PatientRegistrationRequest.class),
                examples = @ExampleObject(
                    name = "Valid Patient Registration",
                    value = """
                    {
                      "first_name": "Jane",
                      "last_name": "Smith",
                      "email": "jane.smith@email.com",
                      "phone_number": "+1234567890",
                      "password": "SecurePassword123!",
                      "confirm_password": "SecurePassword123!",
                      "date_of_birth": "1990-05-15",
                      "gender": "FEMALE",
                      "address": {
                        "street": "456 Main Street",
                        "city": "Boston",
                        "state": "MA",
                        "zip": "02101"
                      },
                      "emergency_contact": {
                        "name": "John Smith",
                        "phone": "+1234567891",
                        "relationship": "spouse"
                      },
                      "insurance_info": {
                        "provider": "Blue Cross",
                        "policy_number": "BC123456789"
                      }
                    }
                    """
                )
            )
        )
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Patient registered successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = com.healthfirst.server.dto.ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email or phone number already exists"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> registerPatient(
            @Valid @RequestBody PatientRegistrationRequest request) {
        try {
            PatientResponse response = patientService.registerPatient(request);
            
            Map<String, Object> data = new HashMap<>();
            data.put("patient_id", response.getUuid());
            data.put("email", response.getEmail());
            data.put("phone_number", response.getPhoneNumber());
            data.put("email_verified", response.getEmailVerified());
            data.put("phone_verified", response.getPhoneVerified());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Patient registered successfully. Verification email sent.", data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An error occurred while registering the patient"));
        }
    }

    // GET /api/v1/patient/{uuid}
    @GetMapping("/{uuid}")
    @Operation(
        summary = "Get patient by UUID",
        description = "Retrieve patient information by their unique identifier"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientByUuid(
            @Parameter(description = "Patient UUID") @PathVariable UUID uuid) {
        try {
            PatientResponse patient = patientService.getPatientByUuid(uuid);
            return ResponseEntity.ok(ApiResponse.success("Patient retrieved successfully", patient));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/v1/patient/email/{email}
    @GetMapping("/email/{email}")
    @Operation(
        summary = "Get patient by email",
        description = "Retrieve patient information by their email address"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientByEmail(
            @Parameter(description = "Patient email") @PathVariable String email) {
        try {
            PatientResponse patient = patientService.getPatientByEmail(email);
            return ResponseEntity.ok(ApiResponse.success("Patient retrieved successfully", patient));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/v1/patient/list
    @GetMapping("/list")
    @Operation(
        summary = "Get all patients",
        description = "Retrieve a paginated list of all active patients"
    )
    public ResponseEntity<ApiResponse<Page<PatientResponse>>> getAllPatients(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Page<PatientResponse> patients = patientService.getAllPatients(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Patients retrieved successfully", patients));
    }

    // GET /api/v1/patient/search
    @GetMapping("/search")
    @Operation(
        summary = "Search patients",
        description = "Search patients by various criteria with pagination"
    )
    public ResponseEntity<ApiResponse<Page<PatientResponse>>> searchPatients(
            @Parameter(description = "First name filter") @RequestParam(required = false) String firstName,
            @Parameter(description = "Last name filter") @RequestParam(required = false) String lastName,
            @Parameter(description = "Email filter") @RequestParam(required = false) String email,
            @Parameter(description = "Gender filter") @RequestParam(required = false) Patient.Gender gender,
            @Parameter(description = "Active status filter") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        
        Page<PatientResponse> patients = patientService.searchPatients(
            firstName, lastName, email, gender, isActive, page, size);
        return ResponseEntity.ok(ApiResponse.success("Patients search completed", patients));
    }

    // PUT /api/v1/patient/{uuid}
    @PutMapping("/{uuid}")
    @Operation(
        summary = "Update patient",
        description = "Update patient information by UUID"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email or phone number already exists")
    })
    public ResponseEntity<ApiResponse<PatientResponse>> updatePatient(
            @Parameter(description = "Patient UUID") @PathVariable UUID uuid,
            @Valid @RequestBody PatientRegistrationRequest request) {
        try {
            PatientResponse patient = patientService.updatePatient(uuid, request);
            return ResponseEntity.ok(ApiResponse.success("Patient updated successfully", patient));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // PATCH /api/v1/patient/{uuid}/deactivate
    @PatchMapping("/{uuid}/deactivate")
    @Operation(
        summary = "Deactivate patient",
        description = "Deactivate a patient account"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient deactivated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<ApiResponse<String>> deactivatePatient(
            @Parameter(description = "Patient UUID") @PathVariable UUID uuid) {
        try {
            patientService.deactivatePatient(uuid);
            return ResponseEntity.ok(ApiResponse.success("Patient deactivated successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // PATCH /api/v1/patient/{uuid}/activate
    @PatchMapping("/{uuid}/activate")
    @Operation(
        summary = "Activate patient",
        description = "Activate a patient account"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient activated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<ApiResponse<String>> activatePatient(
            @Parameter(description = "Patient UUID") @PathVariable UUID uuid) {
        try {
            patientService.activatePatient(uuid);
            return ResponseEntity.ok(ApiResponse.success("Patient activated successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // PATCH /api/v1/patient/{uuid}/verify-email
    @PatchMapping("/{uuid}/verify-email")
    @Operation(
        summary = "Verify patient email",
        description = "Mark patient email as verified"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Email verified successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<ApiResponse<String>> verifyEmail(
            @Parameter(description = "Patient UUID") @PathVariable UUID uuid) {
        try {
            patientService.verifyEmail(uuid);
            return ResponseEntity.ok(ApiResponse.success("Email verified successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // PATCH /api/v1/patient/{uuid}/verify-phone
    @PatchMapping("/{uuid}/verify-phone")
    @Operation(
        summary = "Verify patient phone",
        description = "Mark patient phone as verified"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Phone verified successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<ApiResponse<String>> verifyPhone(
            @Parameter(description = "Patient UUID") @PathVariable UUID uuid) {
        try {
            patientService.verifyPhone(uuid);
            return ResponseEntity.ok(ApiResponse.success("Phone verified successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
} 