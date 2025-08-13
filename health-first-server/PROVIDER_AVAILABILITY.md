# Provider Availability Module

## Overview

The Provider Availability Module is a comprehensive system for managing healthcare provider schedules, appointment slots, and availability. It supports both one-time and recurring availability patterns, with advanced features for timezone handling, conflict prevention, and patient search functionality.

## Features

### Core Features
- **Availability Management**: Create, update, and delete provider availability slots
- **Recurring Patterns**: Support for daily, weekly, and monthly recurring schedules
- **Appointment Slot Generation**: Automatic generation of bookable appointment slots
- **Timezone Support**: Full timezone handling with daylight saving time considerations
- **Conflict Prevention**: Prevents overlapping availability slots for the same provider

### Advanced Features
- **Patient Search**: Search for available slots based on multiple criteria
- **Specialization Filtering**: Filter by medical specialization
- **Location-based Search**: Search by city, state, or zip code
- **Pricing Integration**: Support for different pricing models and insurance acceptance
- **Special Requirements**: Track special requirements for appointments

## Database Schema

### ProviderAvailability Entity
```java
@Entity
@Table(name = "provider_availability")
public class ProviderAvailability {
    private UUID uuid;
    private Provider provider;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String timezone;
    private Boolean isRecurring;
    private RecurrencePattern recurrencePattern;
    private LocalDate recurrenceEndDate;
    private Integer slotDuration;
    private Integer breakDuration;
    private AvailabilityStatus status;
    private Integer maxAppointmentsPerSlot;
    private Integer currentAppointments;
    private AppointmentType appointmentType;
    private Location location;
    private Pricing pricing;
    private String notes;
    private List<String> specialRequirements;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
```

### AppointmentSlot Entity
```java
@Entity
@Table(name = "appointment_slots")
public class AppointmentSlot {
    private UUID uuid;
    private ProviderAvailability availability;
    private Provider provider;
    private ZonedDateTime slotStartTime;
    private ZonedDateTime slotEndTime;
    private SlotStatus status;
    private Patient patient;
    private String appointmentType;
    private String bookingReference;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
```

### Embedded Entities

#### Location
```java
@Embeddable
public class Location {
    private LocationType type; // CLINIC, HOSPITAL, TELEMEDICINE, HOME_VISIT
    private String address;
    private String roomNumber;
}
```

#### Pricing
```java
@Embeddable
public class Pricing {
    private BigDecimal baseFee;
    private Boolean insuranceAccepted;
    private String currency;
}
```

## API Endpoints

### 1. Create Availability Slots
**POST** `/api/v1/provider/availability`

**Request Body:**
```json
{
  "date": "2024-02-15",
  "start_time": "09:00",
  "end_time": "17:00",
  "timezone": "America/New_York",
  "slot_duration": 30,
  "break_duration": 15,
  "is_recurring": true,
  "recurrence_pattern": "WEEKLY",
  "recurrence_end_date": "2024-08-15",
  "appointment_type": "CONSULTATION",
  "location": {
    "type": "CLINIC",
    "address": "123 Medical Center Dr, New York, NY 10001",
    "room_number": "Room 205"
  },
  "pricing": {
    "base_fee": 150.00,
    "insurance_accepted": true,
    "currency": "USD"
  },
  "special_requirements": ["fasting_required", "bring_insurance_card"],
  "notes": "Standard consultation slots"
}
```

**Success Response (201):**
```json
{
  "success": true,
  "message": "Availability slots created successfully",
  "data": {
    "availability_id": "uuid-here",
    "provider_id": "provider-uuid",
    "provider_name": "Dr. John Doe",
    "date": "2024-02-15",
    "start_time": "09:00",
    "end_time": "17:00"
  }
}
```

### 2. Get Provider Availability
**GET** `/api/v1/provider/{provider_id}/availability`

**Query Parameters:**
- `start_date` (required): Start date in YYYY-MM-DD format
- `end_date` (required): End date in YYYY-MM-DD format
- `status` (optional): Filter by availability status
- `appointment_type` (optional): Filter by appointment type
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

**Success Response (200):**
```json
{
  "success": true,
  "message": "Provider availability retrieved successfully",
  "data": {
    "content": [
      {
        "uuid": "availability-uuid",
        "providerId": "provider-uuid",
        "providerName": "Dr. John Doe",
        "specialization": "Cardiology",
        "date": "2024-02-15",
        "startTime": "09:00",
        "endTime": "17:00",
        "timezone": "America/New_York",
        "isRecurring": true,
        "recurrencePattern": "WEEKLY",
        "slotDuration": 30,
        "breakDuration": 15,
        "status": "AVAILABLE",
        "appointmentType": "CONSULTATION",
        "location": {
          "type": "CLINIC",
          "address": "123 Medical Center Dr",
          "roomNumber": "Room 205"
        },
        "pricing": {
          "baseFee": 150.00,
          "insuranceAccepted": true,
          "currency": "USD"
        }
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0
  }
}
```

### 3. Update Availability Slot
**PUT** `/api/v1/provider/availability/{slot_id}`

**Request Body:** Same as create availability

**Success Response (200):**
```json
{
  "success": true,
  "message": "Availability updated successfully",
  "data": {
    // Updated availability data
  }
}
```

### 4. Delete Availability Slot
**DELETE** `/api/v1/provider/availability/{slot_id}`

**Query Parameters:**
- `delete_recurring` (optional): Delete all recurring instances
- `reason` (optional): Reason for deletion

**Success Response (200):**
```json
{
  "success": true,
  "message": "Availability deleted successfully",
  "data": null
}
```

### 5. Search Available Slots
**GET** `/api/v1/availability/search`

**Query Parameters:**
- `date` (optional): Specific date in YYYY-MM-DD format
- `start_date` & `end_date` (optional): Date range
- `specialization` (optional): Medical specialization
- `location` (optional): City, state, or zip code
- `appointment_type` (optional): Type of appointment
- `insurance_accepted` (optional): Filter by insurance acceptance
- `max_price` (optional): Maximum price filter
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

**Success Response (200):**
```json
{
  "success": true,
  "message": "Search completed successfully",
  "data": {
    "content": [
      {
        "uuid": "availability-uuid",
        "providerId": "provider-uuid",
        "providerName": "Dr. John Doe",
        "specialization": "Cardiology",
        "date": "2024-02-15",
        "startTime": "10:00",
        "endTime": "10:30",
        "appointmentType": "CONSULTATION",
        "location": {
          "type": "CLINIC",
          "address": "123 Medical Center Dr",
          "roomNumber": "Room 205"
        },
        "pricing": {
          "baseFee": 150.00,
          "insuranceAccepted": true,
          "currency": "USD"
        },
        "specialRequirements": ["bring_insurance_card"]
      }
    ],
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 6. Get Available Specializations
**GET** `/api/v1/availability/specializations`

**Success Response (200):**
```json
{
  "success": true,
  "message": "Available specializations retrieved successfully",
  "data": ["Cardiology", "Dermatology", "Neurology", "Orthopedics"]
}
```

### 7. Get Upcoming Slots for Provider
**GET** `/api/v1/provider/{provider_id}/upcoming-slots`

**Success Response (200):**
```json
{
  "success": true,
  "message": "Upcoming slots retrieved successfully",
  "data": [
    // List of upcoming availability slots
  ]
}
```

## Business Logic

### Time Zone Handling
- All times are stored in UTC in the database
- Times are converted to provider's local timezone for display
- Handles daylight saving time transitions automatically
- Uses Java's `ZoneId` for timezone conversions

### Conflict Prevention
- Prevents overlapping slots for the same provider on the same date
- Validates time ranges (end time must be after start time)
- Ensures minimum and maximum slot durations
- Checks for existing appointments before slot deletion

### Slot Generation
- Automatically generates appointment slots based on availability
- Considers slot duration and break duration
- Creates slots that don't extend beyond availability end time
- Handles timezone conversions for slot start/end times

### Validation Rules
- **Slot Duration**: 15-480 minutes (15 minutes to 8 hours)
- **Break Duration**: 0-120 minutes (0 to 2 hours)
- **Max Appointments per Slot**: 1-10 appointments
- **Time Range**: End time must be after start time
- **Date**: Must be in the future for new availability
- **Timezone**: Must be a valid timezone identifier

## Error Handling

### Validation Errors (400)
```json
{
  "success": false,
  "message": "End time must be after start time"
}
```

### Conflict Errors (400)
```json
{
  "success": false,
  "message": "Time slot overlaps with existing availability"
}
```

### Not Found Errors (404)
```json
{
  "success": false,
  "message": "Provider not found"
}
```

## Usage Examples

### Java Service Usage
```java
@Service
public class AppointmentService {
    
    @Autowired
    private ProviderAvailabilityService availabilityService;
    
    public void createWeeklySchedule(UUID providerId) {
        ProviderAvailabilityRequest request = new ProviderAvailabilityRequest();
        request.setDate(LocalDate.now().plusDays(1));
        request.setStartTime("09:00");
        request.setEndTime("17:00");
        request.setTimezone("America/New_York");
        request.setSlotDuration(30);
        request.setBreakDuration(15);
        request.setIsRecurring(true);
        request.setRecurrencePattern(RecurrencePattern.WEEKLY);
        request.setRecurrenceEndDate(LocalDate.now().plusMonths(6));
        request.setAppointmentType(AppointmentType.CONSULTATION);
        
        // Set location
        ProviderAvailabilityRequest.LocationRequest location = new ProviderAvailabilityRequest.LocationRequest();
        location.setType(Location.LocationType.CLINIC);
        location.setAddress("123 Medical Center Dr, New York, NY 10001");
        location.setRoomNumber("Room 205");
        request.setLocation(location);
        
        // Set pricing
        ProviderAvailabilityRequest.PricingRequest pricing = new ProviderAvailabilityRequest.PricingRequest();
        pricing.setBaseFee(new BigDecimal("150.00"));
        pricing.setInsuranceAccepted(true);
        pricing.setCurrency("USD");
        request.setPricing(pricing);
        
        ProviderAvailabilityResponse response = availabilityService.createAvailability(providerId, request);
    }
}
```

### cURL Examples

#### Create Availability
```bash
curl -X POST "http://localhost:8080/api/v1/provider/availability?provider_id=provider-uuid" \
  -H "Content-Type: application/json" \
  -d '{
    "date": "2024-02-15",
    "start_time": "09:00",
    "end_time": "17:00",
    "timezone": "America/New_York",
    "slot_duration": 30,
    "break_duration": 15,
    "is_recurring": true,
    "recurrence_pattern": "WEEKLY",
    "recurrence_end_date": "2024-08-15",
    "appointment_type": "CONSULTATION",
    "location": {
      "type": "CLINIC",
      "address": "123 Medical Center Dr, New York, NY 10001",
      "room_number": "Room 205"
    },
    "pricing": {
      "base_fee": 150.00,
      "insurance_accepted": true,
      "currency": "USD"
    },
    "special_requirements": ["fasting_required", "bring_insurance_card"],
    "notes": "Standard consultation slots"
  }'
```

#### Search Available Slots
```bash
curl -X GET "http://localhost:8080/api/v1/availability/search?date=2024-02-15&specialization=Cardiology&location=New%20York&insurance_accepted=true&max_price=200.00"
```

#### Get Provider Availability
```bash
curl -X GET "http://localhost:8080/api/v1/provider/provider-uuid/availability?start_date=2024-02-15&end_date=2024-02-22"
```

## Configuration

### Application Properties
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/health_first
spring.datasource.username=root
spring.datasource.password=password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Timezone Configuration
spring.jackson.time-zone=UTC
```

### Dependencies
```xml
<dependencies>
    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Boot Starter Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- Spring Boot Starter Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- MySQL Connector -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Swagger/OpenAPI -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.1.0</version>
    </dependency>
</dependencies>
```

## Security Considerations

### Data Privacy
- All provider and patient data is encrypted at rest
- API endpoints require proper authentication
- Timezone information is validated to prevent injection attacks
- Input validation prevents SQL injection and XSS attacks

### HIPAA Compliance
- All medical data is handled according to HIPAA guidelines
- Audit trails are maintained for all availability changes
- Access controls ensure only authorized users can modify schedules
- Data retention policies are enforced

## Testing Strategy

### Unit Tests
- Test timezone conversions and daylight saving time handling
- Test slot generation logic with various durations
- Test conflict detection and prevention
- Test validation rules for all input parameters

### Integration Tests
- Test complete availability creation workflow
- Test search functionality with various filters
- Test recurring pattern generation
- Test appointment slot booking integration

### Performance Tests
- Test with large datasets (thousands of availability slots)
- Test concurrent booking scenarios
- Test search performance with complex filters
- Test timezone conversion performance

## Future Enhancements

### Planned Features
- **Calendar Integration**: Sync with external calendar systems
- **Notification System**: Automated reminders for availability changes
- **Analytics Dashboard**: Provider utilization and availability analytics
- **Mobile App Support**: Native mobile app for availability management
- **AI-Powered Scheduling**: Intelligent slot optimization based on demand

### Scalability Improvements
- **Caching Layer**: Redis caching for frequently accessed availability data
- **Database Optimization**: Indexing strategies for large datasets
- **Microservices Architecture**: Split into separate availability service
- **Event-Driven Architecture**: Real-time updates using message queues 