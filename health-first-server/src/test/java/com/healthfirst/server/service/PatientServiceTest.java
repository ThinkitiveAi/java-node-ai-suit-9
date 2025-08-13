package com.healthfirst.server.service;

import com.healthfirst.server.dto.PatientRegistrationRequest;
import com.healthfirst.server.dto.PatientResponse;
import com.healthfirst.server.entity.Patient;
import com.healthfirst.server.entity.Address;
import com.healthfirst.server.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private PatientService patientService;

    private PatientRegistrationRequest validRequest;
    private Patient mockPatient;
    private UUID testUuid;

    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();
        
        // Create valid registration request
        validRequest = new PatientRegistrationRequest();
        validRequest.setFirstName("Jane");
        validRequest.setLastName("Smith");
        validRequest.setEmail("jane.smith@email.com");
        validRequest.setPhoneNumber("+1234567890");
        validRequest.setPassword("SecurePass123!");
        validRequest.setConfirmPassword("SecurePass123!");
        validRequest.setDateOfBirth(LocalDate.of(1990, 5, 15));
        validRequest.setGender(Patient.Gender.FEMALE);
        
        Address address = new Address();
        address.setStreet("456 Main Street");
        address.setCity("Boston");
        address.setState("MA");
        address.setZip("02101");
        validRequest.setAddress(address);

        // Create mock patient
        mockPatient = new Patient();
        mockPatient.setId(1L);
        mockPatient.setUuid(testUuid);
        mockPatient.setFirstName("Jane");
        mockPatient.setLastName("Smith");
        mockPatient.setEmail("jane.smith@email.com");
        mockPatient.setPhoneNumber("+1234567890");
        mockPatient.setPasswordHash("hashedPassword");
        mockPatient.setDateOfBirth(LocalDate.of(1990, 5, 15));
        mockPatient.setGender(Patient.Gender.FEMALE);
        mockPatient.setAddress(address);
        mockPatient.setEmailVerified(false);
        mockPatient.setPhoneVerified(false);
        mockPatient.setIsActive(true);
    }

    @Test
    void registerPatient_Success() {
        // Arrange
        when(patientRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(patientRepository.existsByPhoneNumber(validRequest.getPhoneNumber())).thenReturn(false);
        when(passwordEncoder.encode(validRequest.getPassword())).thenReturn("hashedPassword");
        when(patientRepository.save(any(Patient.class))).thenReturn(mockPatient);

        // Act
        PatientResponse response = patientService.registerPatient(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testUuid, response.getUuid());
        assertEquals("jane.smith@email.com", response.getEmail());
        assertEquals("Jane", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals(Patient.Gender.FEMALE, response.getGender());
        assertFalse(response.getEmailVerified());
        assertFalse(response.getPhoneVerified());
        assertTrue(response.getIsActive());

        verify(patientRepository).existsByEmail(validRequest.getEmail());
        verify(patientRepository).existsByPhoneNumber(validRequest.getPhoneNumber());
        verify(passwordEncoder).encode(validRequest.getPassword());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void registerPatient_PasswordMismatch() {
        // Arrange
        validRequest.setConfirmPassword("DifferentPassword123!");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patientService.registerPatient(validRequest));
        assertEquals("Password and confirmation password do not match", exception.getMessage());
        
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void registerPatient_EmailAlreadyExists() {
        // Arrange
        when(patientRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patientService.registerPatient(validRequest));
        assertEquals("Email already exists", exception.getMessage());
        
        verify(patientRepository).existsByEmail(validRequest.getEmail());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void getPatientByUuid_Success() {
        // Arrange
        when(patientRepository.findByUuid(testUuid)).thenReturn(Optional.of(mockPatient));

        // Act
        PatientResponse response = patientService.getPatientByUuid(testUuid);

        // Assert
        assertNotNull(response);
        assertEquals(testUuid, response.getUuid());
        assertEquals("jane.smith@email.com", response.getEmail());
        
        verify(patientRepository).findByUuid(testUuid);
    }

    @Test
    void getPatientByUuid_NotFound() {
        // Arrange
        when(patientRepository.findByUuid(testUuid)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patientService.getPatientByUuid(testUuid));
        assertEquals("Patient not found", exception.getMessage());
        
        verify(patientRepository).findByUuid(testUuid);
    }
} 