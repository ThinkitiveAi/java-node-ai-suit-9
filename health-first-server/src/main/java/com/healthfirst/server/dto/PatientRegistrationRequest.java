package com.healthfirst.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import com.healthfirst.server.entity.Address;
import com.healthfirst.server.entity.EmergencyContact;
import com.healthfirst.server.entity.InsuranceInfo;
import com.healthfirst.server.entity.Patient.Gender;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Patient registration request")
public class PatientRegistrationRequest {

    @Schema(description = "Patient's first name", example = "Jane", minLength = 2, maxLength = 50)
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Schema(description = "Patient's last name", example = "Smith", minLength = 2, maxLength = 50)
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Schema(description = "Patient's email address", example = "jane.smith@email.com")
    @Email(message = "Email must be a valid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "Patient's phone number in international format", example = "+1234567890")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in international format")
    private String phoneNumber;

    @Schema(description = "Patient's password (must meet security requirements)", example = "SecurePassword123!")
    @NotBlank(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must contain at least 8 characters, one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    @Schema(description = "Password confirmation (must match password)", example = "SecurePassword123!")
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    @Schema(description = "Patient's date of birth (must be at least 13 years old)", example = "1990-05-15")
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Schema(description = "Patient's gender", example = "FEMALE", allowableValues = {"MALE", "FEMALE", "OTHER", "PREFER_NOT_TO_SAY"})
    @NotNull(message = "Gender is required")
    private Gender gender;

    @Schema(description = "Patient's address")
    @Valid
    @NotNull(message = "Address is required")
    private Address address;

    @Schema(description = "Patient's emergency contact information (optional)")
    @Valid
    private EmergencyContact emergencyContact;

    @Schema(description = "Patient's medical history (optional)")
    private List<String> medicalHistory;

    @Schema(description = "Patient's insurance information (optional)")
    @Valid
    private InsuranceInfo insuranceInfo;

    // Constructors
    public PatientRegistrationRequest() {}

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
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
} 