package com.healthfirst.server.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class Location {

    @NotNull(message = "Location type is required")
    @Enumerated(EnumType.STRING)
    private LocationType type;

    private String address;

    private String roomNumber;

    // Constructors
    public Location() {}

    public Location(LocationType type, String address, String roomNumber) {
        this.type = type;
        this.address = address;
        this.roomNumber = roomNumber;
    }

    // Getters and Setters
    public LocationType getType() {
        return type;
    }

    public void setType(LocationType type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    // Enum for location type
    public enum LocationType {
        CLINIC, HOSPITAL, TELEMEDICINE, HOME_VISIT
    }
} 