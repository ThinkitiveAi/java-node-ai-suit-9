package com.healthfirst.server.dto;

import com.healthfirst.server.entity.Patient;
import com.healthfirst.server.entity.Address;
import com.healthfirst.server.entity.EmergencyContact;
import com.healthfirst.server.entity.InsuranceInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Patient response data")
public class PatientResponse {

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

    @Schema(description = "Patient's date of birth")
    private LocalDate dateOfBirth;

    @Schema(description = "Patient's gender")
    private Patient.Gender gender;

    @Schema(description = "Patient's address")
    private Address address;

    @Schema(description = "Patient's emergency contact")
    private EmergencyContact emergencyContact;

    @Schema(description = "Patient's medical history")
    private List<String> medicalHistory;

    @Schema(description = "Patient's insurance information")
    private InsuranceInfo insuranceInfo;

    @Schema(description = "Whether email is verified")
    private Boolean emailVerified;

    @Schema(description = "Whether phone is verified")
    private Boolean phoneVerified;

    @Schema(description = "Whether patient account is active")
    private Boolean isActive;

    @Schema(description = "Account creation timestamp")
    private ZonedDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private ZonedDateTime updatedAt;

    // Constructors
    public PatientResponse() {}

    public PatientResponse(Patient patient) {
        this.uuid = patient.getUuid();
        this.firstName = patient.getFirstName();
        this.lastName = patient.getLastName();
        this.email = patient.getEmail();
        this.phoneNumber = patient.getPhoneNumber();
        this.dateOfBirth = patient.getDateOfBirth();
        this.gender = patient.getGender();
        this.address = patient.getAddress();
        this.emergencyContact = patient.getEmergencyContact();
        this.medicalHistory = patient.getMedicalHistory();
        this.insuranceInfo = patient.getInsuranceInfo();
        this.emailVerified = patient.getEmailVerified();
        this.phoneVerified = patient.getPhoneVerified();
        this.isActive = patient.getIsActive();
        this.createdAt = patient.getCreatedAt();
        this.updatedAt = patient.getUpdatedAt();
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Patient.Gender getGender() {
        return gender;
    }

    public void setGender(Patient.Gender gender) {
        this.gender = gender;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public EmergencyContact getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(EmergencyContact emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public List<String> getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(List<String> medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public InsuranceInfo getInsuranceInfo() {
        return insuranceInfo;
    }

    public void setInsuranceInfo(InsuranceInfo insuranceInfo) {
        this.insuranceInfo = insuranceInfo;
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

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 