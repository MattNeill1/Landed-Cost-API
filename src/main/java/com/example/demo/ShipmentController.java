package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentRepository shipmentRepository;
    private final AllocationService allocationService;

    public ShipmentController(ShipmentRepository shipmentRepository,
    AllocationService allocationService) {
        this.shipmentRepository = shipmentRepository;
        this.allocationService = allocationService;
    }

    // Create a shipment (with its lines) and save it.
    @PostMapping
    public Shipment create(@Valid @RequestBody Shipment shipment) {
        for (ShipmentLine line : shipment.getShipmentLines()) {
            line.setShipment(shipment);   // wire the back-reference so the FK isn't null
        }
        return shipmentRepository.save(shipment);
    }

    // Run the allocation engine for one shipment.
    @GetMapping("/{id}/allocation")
    public ResponseEntity<List<LineAllocation>> allocate(@PathVariable Long id) {
        Optional<Shipment> found = shipmentRepository.findById(id);
        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<LineAllocation> result = allocationService.allocate(found.get());
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public List<Shipment> list() {
        return shipmentRepository.findAll();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        if (!shipmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        shipmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
}
