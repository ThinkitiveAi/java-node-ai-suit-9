package com.healthfirst.server.service;

import com.healthfirst.server.dto.PatientLoginRequest;
import com.healthfirst.server.dto.PatientLoginResponse;
import com.healthfirst.server.dto.PatientRegistrationRequest;
import com.healthfirst.server.dto.PatientResponse;
import com.healthfirst.server.entity.Patient;
import com.healthfirst.server.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public PatientLoginResponse loginPatient(PatientLoginRequest request) {
        // Find patient by email
        Optional<Patient> patientOpt = patientRepository.findByEmail(request.getEmail());
        if (!patientOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        Patient patient = patientOpt.get();

        // Check if account is active
        if (!patient.getIsActive()) {
            throw new IllegalArgumentException("Account is deactivated. Please contact support.");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), patient.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtService.generateToken(patient);

        // Create patient data for response
        PatientLoginResponse.PatientData patientData = new PatientLoginResponse.PatientData(
            patient.getUuid(),
            patient.getFirstName(),
            patient.getLastName(),
            patient.getEmail(),
            patient.getPhoneNumber(),
            patient.getEmailVerified(),
            patient.getPhoneVerified(),
            patient.getIsActive(),
            patient.getCreatedAt()
        );

        // Return login response
        return new PatientLoginResponse(token, 1800L, "Bearer", patientData);
    }

    public PatientResponse registerPatient(PatientRegistrationRequest request) {
        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and confirmation password do not match");
        }

        // Validate age (must be at least 13 years old for COPPA compliance)
        if (request.getDateOfBirth() != null) {
            int age = Period.between(request.getDateOfBirth(), LocalDate.now()).getYears();
            if (age < 13) {
                throw new IllegalArgumentException("Patient must be at least 13 years old");
            }
        }

        // Check for existing email
        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Check for existing phone number
        if (patientRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // Create new patient
        Patient patient = new Patient();
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setEmail(request.getEmail());
        patient.setPhoneNumber(request.getPhoneNumber());
        patient.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setAddress(request.getAddress());
        patient.setEmergencyContact(request.getEmergencyContact());
        patient.setMedicalHistory(request.getMedicalHistory());
        patient.setInsuranceInfo(request.getInsuranceInfo());
        patient.setEmailVerified(false);
        patient.setPhoneVerified(false);
        patient.setIsActive(true);

        Patient savedPatient = patientRepository.save(patient);
        return new PatientResponse(savedPatient);
    }

    public PatientResponse getPatientByUuid(UUID uuid) {
        Optional<Patient> patient = patientRepository.findByUuid(uuid);
        if (patient.isPresent()) {
            return new PatientResponse(patient.get());
        }
        throw new IllegalArgumentException("Patient not found");
    }

    public PatientResponse getPatientByEmail(String email) {
        Optional<Patient> patient = patientRepository.findByEmail(email);
        if (patient.isPresent()) {
            return new PatientResponse(patient.get());
        }
        throw new IllegalArgumentException("Patient not found");
    }

    public Page<PatientResponse> getAllPatients(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Patient> patients = patientRepository.findAllActive(pageable);
        
        return patients.map(PatientResponse::new);
    }

    public Page<PatientResponse> searchPatients(
            String firstName, 
            String lastName, 
            String email, 
            Patient.Gender gender, 
            Boolean isActive, 
            int page, 
            int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Patient> patients = patientRepository.findByFilters(
            firstName, lastName, email, gender, isActive, pageable);
        
        return patients.map(PatientResponse::new);
    }

    public PatientResponse updatePatient(UUID uuid, PatientRegistrationRequest request) {
        Optional<Patient> existingPatient = patientRepository.findByUuid(uuid);
        if (!existingPatient.isPresent()) {
            throw new IllegalArgumentException("Patient not found");
        }

        Patient patient = existingPatient.get();

        // Check if email is being changed and if it already exists
        if (!patient.getEmail().equals(request.getEmail()) && 
            patientRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Check if phone number is being changed and if it already exists
        if (!patient.getPhoneNumber().equals(request.getPhoneNumber()) && 
            patientRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // Update patient information
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setEmail(request.getEmail());
        patient.setPhoneNumber(request.getPhoneNumber());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setAddress(request.getAddress());
        patient.setEmergencyContact(request.getEmergencyContact());
        patient.setMedicalHistory(request.getMedicalHistory());
        patient.setInsuranceInfo(request.getInsuranceInfo());

        // Only update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Password and confirmation password do not match");
            }
            patient.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        Patient savedPatient = patientRepository.save(patient);
        return new PatientResponse(savedPatient);
    }

    public void deactivatePatient(UUID uuid) {
        Optional<Patient> patient = patientRepository.findByUuid(uuid);
        if (patient.isPresent()) {
            Patient p = patient.get();
            p.setIsActive(false);
            patientRepository.save(p);
        } else {
            throw new IllegalArgumentException("Patient not found");
        }
    }

    public void activatePatient(UUID uuid) {
        Optional<Patient> patient = patientRepository.findByUuid(uuid);
        if (patient.isPresent()) {
            Patient p = patient.get();
            p.setIsActive(true);
            patientRepository.save(p);
        } else {
            throw new IllegalArgumentException("Patient not found");
        }
    }

    public void verifyEmail(UUID uuid) {
        Optional<Patient> patient = patientRepository.findByUuid(uuid);
        if (patient.isPresent()) {
            Patient p = patient.get();
            p.setEmailVerified(true);
            patientRepository.save(p);
        } else {
            throw new IllegalArgumentException("Patient not found");
        }
    }

    public void verifyPhone(UUID uuid) {
        Optional<Patient> patient = patientRepository.findByUuid(uuid);
        if (patient.isPresent()) {
            Patient p = patient.get();
            p.setPhoneVerified(true);
            patientRepository.save(p);
        } else {
            throw new IllegalArgumentException("Patient not found");
        }
    }
} 