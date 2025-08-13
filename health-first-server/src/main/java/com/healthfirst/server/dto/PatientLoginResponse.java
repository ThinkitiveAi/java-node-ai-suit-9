package com.healthfirst.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;
import java.util.UUID;

@Schema(description = "Patient login response")
public class PatientLoginResponse {

    @Schema(description = "JWT access token")
    private String accessToken;

    @Schema(description = "Token expiration time in seconds")
    private Long expiresIn;

    @Schema(description = "Token type")
    private String tokenType;

    @Schema(description = "Patient data")
    private PatientData patient;

    // Constructors
    public PatientLoginResponse() {}

    public PatientLoginResponse(String accessToken, Long expiresIn, String tokenType, PatientData patient) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
        this.patient = patient;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public PatientData getPatient() {
        return patient;
    }

    public void setPatient(PatientData patient) {
        this.patient = patient;
    }

    // Inner class for patient data
    @Schema(description = "Patient data in login response")
    public static class PatientData {
        @Schema(description = "Patient's unique identifier")
        private UUID uuid;

        @Schema(description = "Patient's first name")
        private String firstName;

        @Schema(description = "Patient's last name")
        private String lastName;

        @Schema(description = "Patient's email address")
        private String email;

        @Schema(description = "Patient's phone number")
        private String phoneNumber;

        @Schema(description = "Whether email is verified")
        private Boolean emailVerified;

        @Schema(description = "Whether phone is verified")
        private Boolean phoneVerified;

        @Schema(description = "Whether patient account is active")
        private Boolean isActive;

        @Schema(description = "Account creation timestamp")
        private ZonedDateTime createdAt;

        // Constructors
        public PatientData() {}

        public PatientData(UUID uuid, String firstName, String lastName, String email, 
                          String phoneNumber, Boolean emailVerified, Boolean phoneVerified, 
                          Boolean isActive, ZonedDateTime createdAt) {
            this.uuid = uuid;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.emailVerified = emailVerified;
            this.phoneVerified = phoneVerified;
            this.isActive = isActive;
            this.createdAt = createdAt;
        }

        // Getters and Setters
        public UUID getUuid() {
            return uuid;
        }

        public void setUuid(UUID uuid) {
            this.uuid = uuid;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public Boolean getEmailVerified() {
            return emailVerified;
        }

        public void setEmailVerified(Boolean emailVerified) {
            this.emailVerified = emailVerified;
        }

        public Boolean getPhoneVerified() {
            return phoneVerified;
        }

        public void setPhoneVerified(Boolean phoneVerified) {
            this.phoneVerified = phoneVerified;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }

        public ZonedDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(ZonedDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
} 