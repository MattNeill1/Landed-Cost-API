package com.example.demo;

import java.math.BigDecimal;

public class LineAllocation {
    private String itemSku;
    private BigDecimal allocatedCost;
    private BigDecimal landedUnitCost;

    public String getItemSku() { return itemSku; }
    public void setItemSku(String itemSku) { this.itemSku = itemSku; }

    public BigDecimal getAllocatedCost() { return allocatedCost; }
    public void setAllocatedCost(BigDecimal allocatedCost) { this.allocatedCost = allocatedCost; }

    public BigDecimal getLandedUnitCost() { return landedUnitCost; }
    public void setLandedUnitCost(BigDecimal landedUnitCost) { this.landedUnitCost = landedUnitCost; }
}
