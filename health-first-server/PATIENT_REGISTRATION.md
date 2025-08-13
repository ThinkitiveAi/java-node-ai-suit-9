# Patient Registration Module

## Overview

The Patient Registration module provides a comprehensive backend solution for managing patient registrations with secure authentication, comprehensive validation, and HIPAA compliance considerations. The module supports both relational (MySQL/PostgreSQL) and NoSQL (MongoDB) database setups.

## Features

### Core Functionality
- **Patient Registration**: Secure patient account creation with comprehensive validation
- **Data Management**: Full CRUD operations for patient data
- **Search & Filtering**: Advanced search capabilities with pagination
- **Verification System**: Email and phone verification endpoints
- **Account Management**: Activate/deactivate patient accounts

### Security Features
- **Password Hashing**: Bcrypt encryption with salt rounds >= 12
- **Input Validation**: Comprehensive validation for all fields
- **HIPAA Compliance**: Secure handling of medical data
- **Data Privacy**: Sensitive data never logged or returned in responses

### Validation Rules
- **Email**: Unique, valid format, required
- **Phone Number**: Unique, international format, required
- **Password**: 8+ characters, uppercase, lowercase, number, special character
- **Age**: Minimum 13 years old (COPPA compliance)
- **Address**: Valid postal code format
- **Gender**: Enum values only (MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY)

## Database Schema

### Patient Entity
```java
@Entity
@Table(name = "patients")
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;
    
    @NotBlank @Size(min = 2, max = 50)
    private String firstName;
    
    @NotBlank @Size(min = 2, max = 50)
    private String lastName;
    
    @NotBlank @Email @Column(unique = true)
    private String email;
    
    @NotBlank @Pattern(regexp = "^\\+[1-9]\\d{1,14}$")
    @Column(unique = true)
    private String phoneNumber;
    
    @NotBlank
    private String passwordHash;
    
    @NotNull @Past
    private LocalDate dateOfBirth;
    
    @NotNull @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Embedded @NotNull
    private Address address;
    
    @Embedded
    private EmergencyContact emergencyContact;
    
    @ElementCollection
    private List<String> medicalHistory;
    
    @Embedded
    private InsuranceInfo insuranceInfo;
    
    @Column(nullable = false)
    private Boolean emailVerified = false;
    
    @Column(nullable = false)
    private Boolean phoneVerified = false;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    private ZonedDateTime createdAt;
    
    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
```

### Embedded Entities

#### Address
```java
@Embeddable
public class Address {
    @NotBlank @Size(max = 200)
    private String street;
    
    @NotBlank @Size(max = 100)
    private String city;
    
    @NotBlank @Size(max = 50)
    private String state;
    
    @NotBlank @Pattern(regexp = "^[0-9]{5}(-[0-9]{4})?$")
    private String zip;
}
```

#### EmergencyContact
```java
@Embeddable
public class EmergencyContact {
    @Size(max = 100)
    private String name;
    
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$")
    private String phone;
    
    @Size(max = 50)
    private String relationship;
}
```

#### InsuranceInfo
```java
@Embeddable
public class InsuranceInfo {
    private String provider;
    private String policyNumber;
}
```

## API Endpoints

### 1. Patient Registration
**POST** `/api/v1/patient/register`

**Request Body:**
```json
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
  },
  "medical_history": ["Hypertension", "Diabetes"]
}
```

**Success Response (201):**
```json
{
  "success": true,
  "message": "Patient registered successfully. Verification email sent.",
  "data": {
    "patient_id": "uuid-here",
    "email": "jane.smith@email.com",
    "phone_number": "+1234567890",
    "email_verified": false,
    "phone_verified": false
  }
}
```

### 2. Get Patient by UUID
**GET** `/api/v1/patient/{uuid}`

**Success Response (200):**
```json
{
  "success": true,
  "message": "Patient retrieved successfully",
  "data": {
    "uuid": "uuid-here",
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@email.com",
    "phoneNumber": "+1234567890",
    "dateOfBirth": "1990-05-15",
    "gender": "FEMALE",
    "address": { ... },
    "emergencyContact": { ... },
    "insuranceInfo": { ... },
    "medicalHistory": ["Hypertension", "Diabetes"],
    "emailVerified": false,
    "phoneVerified": false,
    "isActive": true,
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  }
}
```

### 3. Get Patient by Email
**GET** `/api/v1/patient/email/{email}`

### 4. Get All Patients (Paginated)
**GET** `/api/v1/patient/list?page=0&size=10&sortBy=createdAt&sortDir=desc`

### 5. Search Patients
**GET** `/api/v1/patient/search?firstName=Jane&lastName=Smith&email=jane.smith@email.com&gender=FEMALE&isActive=true&page=0&size=10`

### 6. Update Patient
**PUT** `/api/v1/patient/{uuid}`

### 7. Deactivate Patient
**PATCH** `/api/v1/patient/{uuid}/deactivate`

### 8. Activate Patient
**PATCH** `/api/v1/patient/{uuid}/activate`

### 9. Verify Email
**PATCH** `/api/v1/patient/{uuid}/verify-email`

### 10. Verify Phone
**PATCH** `/api/v1/patient/{uuid}/verify-phone`

## Error Handling

### Validation Errors (422)
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "email": ["Email is already registered"],
    "password": ["Password must contain at least 8 characters"],
    "date_of_birth": ["Must be at least 13 years old"]
  }
}
```

### Business Logic Errors (400)
```json
{
  "success": false,
  "message": "Email already exists"
}
```

### Not Found Errors (404)
```json
{
  "success": false,
  "message": "Patient not found"
}
```

## Security Considerations

### Password Security
- Passwords are hashed using bcrypt with salt rounds >= 12
- Password requirements: 8+ characters, uppercase, lowercase, number, special character
- Passwords are never logged or returned in responses

### Data Privacy
- Sensitive medical data is handled securely
- HIPAA compliance considerations implemented
- Audit trails for data access and modifications

### Input Validation
- Comprehensive validation for all input fields
- SQL injection prevention through parameterized queries
- XSS prevention through input sanitization

## Testing

### Unit Tests
- `PatientServiceTest`: Tests for service layer logic
- `PatientControllerTest`: Tests for REST endpoints
- Coverage includes:
  - Registration validation
  - Duplicate checking
  - Password hashing
  - Error scenarios
  - Data privacy

### Integration Tests
- End-to-end API testing
- Database integration testing
- Security testing

## Usage Examples

### Java Service Usage
```java
@Autowired
private PatientService patientService;

// Register a new patient
PatientRegistrationRequest request = new PatientRegistrationRequest();
request.setFirstName("Jane");
request.setLastName("Smith");
request.setEmail("jane.smith@email.com");
// ... set other fields

PatientResponse response = patientService.registerPatient(request);

// Get patient by UUID
PatientResponse patient = patientService.getPatientByUuid(uuid);

// Search patients
Page<PatientResponse> patients = patientService.searchPatients(
    "Jane", "Smith", "jane.smith@email.com", 
    Patient.Gender.FEMALE, true, 0, 10);
```

### REST API Usage (cURL)
```bash
# Register a patient
curl -X POST http://localhost:8080/api/v1/patient/register \
  -H "Content-Type: application/json" \
  -d '{
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
    }
  }'

# Get patient by UUID
curl -X GET http://localhost:8080/api/v1/patient/{uuid}

# Search patients
curl -X GET "http://localhost:8080/api/v1/patient/search?firstName=Jane&page=0&size=10"
```

## Configuration

### Database Configuration
The module works with both relational and NoSQL databases:

**MySQL/PostgreSQL:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/healthfirst
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
```

**MongoDB:**
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/healthfirst
```

### Security Configuration
```properties
# Password encoder strength
spring.security.password.encoder.strength=12

# JWT configuration
jwt.secret=your-secret-key
jwt.expiration=86400000
```

## Dependencies

### Required Dependencies
- Spring Boot 3.x
- Spring Data JPA
- Spring Security
- Spring Validation
- Lombok
- JUnit 5
- Mockito

### Optional Dependencies
- Spring Data MongoDB (for NoSQL setup)
- Spring Boot Actuator (for monitoring)
- Spring Boot DevTools (for development)

## Contributing

1. Follow the existing code style and patterns
2. Add comprehensive tests for new features
3. Update documentation for API changes
4. Ensure HIPAA compliance for medical data handling
5. Follow security best practices

## License

This module is part of the Health First Server project and follows the same licensing terms. 