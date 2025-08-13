package com.healthfirst.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Patient login request")
public class PatientLoginRequest {

    @Schema(description = "Patient's email address", example = "jane.smith@email.com")
    @Email(message = "Email must be a valid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "Patient's password", example = "SecurePassword123!")
    @NotBlank(message = "Password is required")
    private String password;

    // Constructors
    public PatientLoginRequest() {}

    public PatientLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
} 