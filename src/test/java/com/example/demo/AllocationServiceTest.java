package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

class AllocationServiceTest {

    private final AllocationService service = new AllocationService();

    @Test
    void allocatesByValue() {
        // --- Arrange: build a shipment with known numbers ---
        Item widget = new Item();
        widget.setSku("WIDGET");
        widget.setUnitCost(new BigDecimal("10.00"));

        Item gadget = new Item();
        gadget.setSku("GADGET");
        gadget.setUnitCost(new BigDecimal("10.00"));

        ShipmentLine lineA = new ShipmentLine();
        lineA.setItem(widget);
        lineA.setQuantity(6);            // value basis = 10 * 6 = 60

        ShipmentLine lineB = new ShipmentLine();
        lineB.setItem(gadget);
        lineB.setQuantity(4);            // value basis = 10 * 4 = 40

        Shipment shipment = new Shipment();
        shipment.setAllocationMethod(AllocationMethod.VALUE);
        shipment.setFreightCost(new BigDecimal("100.00"));  // duty & insurance null -> treated as 0
        shipment.setShipmentLines(List.of(lineA, lineB));

        // --- Act: run the engine ---
        List<LineAllocation> result = service.allocate(shipment);

        // --- Assert: check the results ---
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAllocatedCost()).isEqualByComparingTo("60.00");
        assertThat(result.get(1).getAllocatedCost()).isEqualByComparingTo("40.00");
        assertThat(result.get(0).getLandedUnitCost()).isEqualByComparingTo("20.00");
    }

     @Test
    void allocationSumsToPoolExactlyDespiteRounding() {
        // Pool of 100 split across 3 equal lines: 100 / 3 = 33.333...
        // Naive rounding would give 33.33 x 3 = 99.99 -- a lost penny.
        // The running-total technique must make them sum to EXACTLY 100.00.

        Shipment shipment = new Shipment();
        shipment.setAllocationMethod(AllocationMethod.VALUE);
        shipment.setFreightCost(new BigDecimal("100.00"));
        shipment.setShipmentLines(List.of(line("A",10), line("B",10), line("C",10)));

        List<LineAllocation> result = service.allocate(shipment);

        assertThat(result.get(0).getAllocatedCost()).isEqualByComparingTo("33.33");
        assertThat(result.get(1).getAllocatedCost()).isEqualByComparingTo("33.34");
        assertThat(result.get(2).getAllocatedCost()).isEqualByComparingTo("33.33");

        BigDecimal total = result.stream()
                .map(LineAllocation::getAllocatedCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(total).isEqualByComparingTo("100.00");

    }
    private ShipmentLine line(String sku, int quantity) {
            Item item = new Item();
            item.setSku(sku);
            item.setUnitCost(new BigDecimal("10.00"));

            ShipmentLine line = new ShipmentLine();
            line.setItem(item);
            line.setQuantity(quantity);
            return line;
        }
}
