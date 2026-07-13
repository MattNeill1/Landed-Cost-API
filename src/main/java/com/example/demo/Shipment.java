package com.example.demo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "shipment")
    private List<ShipmentLine> shipmentLines = new ArrayList<>();

    private String shipmentNumber;
    private BigDecimal freightCost;
    private BigDecimal dutyCost;
    private BigDecimal insuranceCost;

    @Enumerated(EnumType.STRING)
    private AllocationMethod allocationMethod;



    public Shipment() {
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ShipmentLine> getShipmentLines() {
        return shipmentLines;
    }

    public void setShipmentLines(List<ShipmentLine> shipmentLines) {
        this.shipmentLines = shipmentLines;
    }

    public String getShipmentNumber() {
        return shipmentNumber;
    }

    public void setShipmentNumber(String shipmentNumber) {
        this.shipmentNumber = shipmentNumber;
    }

    public BigDecimal getFreightCost() {
        return freightCost;
    }

    public void setFreightCost(BigDecimal freightCost) {
        this.freightCost = freightCost;
    }

    public BigDecimal getDutyCost() {
        return dutyCost;
    }

    public void setDutyCost(BigDecimal dutyCost) {
        this.dutyCost = dutyCost;
    }

    public BigDecimal getInsuranceCost() {
        return insuranceCost;
    }

    public void setInsuranceCost(BigDecimal insuranceCost) {
        this.insuranceCost = insuranceCost;
    }

    public AllocationMethod getAllocationMethod() {
        return allocationMethod;
    }

    public void setAllocationMethod(AllocationMethod allocationMethod) {
        this.allocationMethod = allocationMethod;
    }
    
}
