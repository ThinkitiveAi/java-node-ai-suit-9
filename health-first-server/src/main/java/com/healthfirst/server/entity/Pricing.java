package com.healthfirst.server.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Embeddable
public class Pricing {

    @DecimalMin(value = "0.0", inclusive = false, message = "Base fee must be greater than 0")
    private BigDecimal baseFee;

    @NotNull(message = "Insurance accepted status is required")
    private Boolean insuranceAccepted = false;

    @NotNull(message = "Currency is required")
    private String currency = "USD";

    // Constructors
    public Pricing() {}

    public Pricing(BigDecimal baseFee, Boolean insuranceAccepted, String currency) {
        this.baseFee = baseFee;
        this.insuranceAccepted = insuranceAccepted;
        this.currency = currency;
    }

    // Getters and Setters
    public BigDecimal getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(BigDecimal baseFee) {
        this.baseFee = baseFee;
    }

    public Boolean getInsuranceAccepted() {
        return insuranceAccepted;
    }

    public void setInsuranceAccepted(Boolean insuranceAccepted) {
        this.insuranceAccepted = insuranceAccepted;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
} 