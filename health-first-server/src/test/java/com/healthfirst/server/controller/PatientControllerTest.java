package com.healthfirst.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthfirst.server.dto.PatientLoginRequest;
import com.healthfirst.server.dto.PatientLoginResponse;
import com.healthfirst.server.dto.PatientRegistrationRequest;
import com.healthfirst.server.dto.PatientResponse;
import com.healthfirst.server.entity.Patient;
import com.healthfirst.server.entity.Address;
import com.healthfirst.server.service.PatientService;
import com.healthfirst.server.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PatientControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private PatientService patientService;

    @MockBean
    private JwtService jwtService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PatientRegistrationRequest validRequest;
    private PatientResponse mockResponse;
    private PatientLoginRequest loginRequest;
    private PatientLoginResponse loginResponse;
    private UUID testUuid;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        
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

        // Create mock registration response
        mockResponse = new PatientResponse();
        mockResponse.setUuid(testUuid);
        mockResponse.setFirstName("Jane");
        mockResponse.setLastName("Smith");
        mockResponse.setEmail("jane.smith@email.com");
        mockResponse.setPhoneNumber("+1234567890");
        mockResponse.setDateOfBirth(LocalDate.of(1990, 5, 15));
        mockResponse.setGender(Patient.Gender.FEMALE);
        mockResponse.setAddress(address);
        mockResponse.setEmailVerified(false);
        mockResponse.setPhoneVerified(false);
        mockResponse.setIsActive(true);

        // Create login request
        loginRequest = new PatientLoginRequest();
        loginRequest.setEmail("jane.smith@email.com");
        loginRequest.setPassword("SecurePass123!");

        // Create login response
        PatientLoginResponse.PatientData patientData = new PatientLoginResponse.PatientData(
            testUuid, "Jane", "Smith", "jane.smith@email.com", 
            "+1234567890", false, false, true, null
        );
        loginResponse = new PatientLoginResponse("jwt-token-here", 1800L, "Bearer", patientData);
    }

    @Test
    void loginPatient_Success() throws Exception {
        // Arrange
        when(patientService.loginPatient(any(PatientLoginRequest.class)))
                .thenReturn(loginResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/patient/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-token-here"))
                .andExpect(jsonPath("$.data.expiresIn").value(1800))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.patient.uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$.data.patient.email").value("jane.smith@email.com"));

        verify(patientService).loginPatient(any(PatientLoginRequest.class));
    }

    @Test
    void loginPatient_InvalidCredentials() throws Exception {
        // Arrange
        when(patientService.loginPatient(any(PatientLoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid email or password"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/patient/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));

        verify(patientService).loginPatient(any(PatientLoginRequest.class));
    }

    @Test
    void registerPatient_Success() throws Exception {
        // Arrange
        when(patientService.registerPatient(any(PatientRegistrationRequest.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/patient/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Patient registered successfully. Verification email sent."))
                .andExpect(jsonPath("$.data.patient_id").value(testUuid.toString()))
                .andExpect(jsonPath("$.data.email").value("jane.smith@email.com"))
                .andExpect(jsonPath("$.data.phone_number").value("+1234567890"))
                .andExpect(jsonPath("$.data.email_verified").value(false))
                .andExpect(jsonPath("$.data.phone_verified").value(false));

        verify(patientService).registerPatient(any(PatientRegistrationRequest.class));
    }

    @Test
    void registerPatient_ValidationError() throws Exception {
        // Arrange
        validRequest.setEmail("invalid-email");

        // Act & Assert
        mockMvc.perform(post("/api/v1/patient/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        verify(patientService, never()).registerPatient(any(PatientRegistrationRequest.class));
    }

    @Test
    void registerPatient_ServiceException() throws Exception {
        // Arrange
        when(patientService.registerPatient(any(PatientRegistrationRequest.class)))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/patient/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email already exists"));

        verify(patientService).registerPatient(any(PatientRegistrationRequest.class));
    }

    @Test
    void getPatientByUuid_Success() throws Exception {
        // Arrange
        when(patientService.getPatientByUuid(testUuid))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/patient/{uuid}", testUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Patient retrieved successfully"))
                .andExpect(jsonPath("$.data.uuid").value(testUuid.toString()))
                .andExpect(jsonPath("$.data.email").value("jane.smith@email.com"));

        verify(patientService).getPatientByUuid(testUuid);
    }

    @Test
    void getPatientByUuid_NotFound() throws Exception {
        // Arrange
        when(patientService.getPatientByUuid(testUuid))
                .thenThrow(new IllegalArgumentException("Patient not found"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/patient/{uuid}", testUuid))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Patient not found"));

        verify(patientService).getPatientByUuid(testUuid);
    }

    @Test
    void deactivatePatient_Success() throws Exception {
        // Arrange
        doNothing().when(patientService).deactivatePatient(testUuid);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/patient/{uuid}/deactivate", testUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Patient deactivated successfully"));

        verify(patientService).deactivatePatient(testUuid);
    }

    @Test
    void verifyEmail_Success() throws Exception {
        // Arrange
        doNothing().when(patientService).verifyEmail(testUuid);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/patient/{uuid}/verify-email", testUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Email verified successfully"));

        verify(patientService).verifyEmail(testUuid);
    }
} 