package com.example.demo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AllocationService {

    public List<LineAllocation> allocate(Shipment shipment) {
        AllocationMethod method = shipment.getAllocationMethod();
        List<ShipmentLine> lines = shipment.getShipmentLines();

        // 1. The pool of money to spread: freight + duty + insurance.
        BigDecimal pool = nz(shipment.getFreightCost())
                .add(nz(shipment.getDutyCost()))
                .add(nz(shipment.getInsuranceCost()));

        // 2. Total basis across all lines (value / weight / quantity).
        BigDecimal totalBasis = BigDecimal.ZERO;
        for (ShipmentLine line : lines) {
            totalBasis = totalBasis.add(basisFor(line, method));
        }
        if (totalBasis.signum() == 0) {
            throw new IllegalStateException("Total allocation basis is zero; cannot allocate.");
        }

        // 3. Running-total allocation so pennies always sum to the pool.
        List<LineAllocation> results = new ArrayList<>();
        BigDecimal cumulativeBasis = BigDecimal.ZERO;
        BigDecimal allocatedSoFar = BigDecimal.ZERO;

        for (ShipmentLine line : lines) {
            cumulativeBasis = cumulativeBasis.add(basisFor(line, method));

            BigDecimal cumulativeAlloc = pool
                    .multiply(cumulativeBasis)
                    .divide(totalBasis, 2, RoundingMode.HALF_UP);

            BigDecimal lineAlloc = cumulativeAlloc.subtract(allocatedSoFar);
            allocatedSoFar = cumulativeAlloc;

            BigDecimal qty = new BigDecimal(line.getQuantity());
            BigDecimal landedUnitCost = nz(line.getItem().getUnitCost())
                    .add(lineAlloc.divide(qty, 4, RoundingMode.HALF_UP));

            LineAllocation r = new LineAllocation();
            r.setItemSku(line.getItem().getSku());
            r.setAllocatedCost(lineAlloc);
            r.setLandedUnitCost(landedUnitCost);
            results.add(r);
        }

        return results;
    }

    // Picks the per-line number to allocate against.
    private BigDecimal basisFor(ShipmentLine line, AllocationMethod method) {
        switch (method) {
            case VALUE:
                return nz(line.getItem().getUnitCost())
                        .multiply(new BigDecimal(line.getQuantity()));
            case WEIGHT:
                return nz(line.getWeight());
            case QUANTITY:
                return new BigDecimal(line.getQuantity());
            default:
                throw new IllegalArgumentException("Unknown method: " + method);
        }
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
