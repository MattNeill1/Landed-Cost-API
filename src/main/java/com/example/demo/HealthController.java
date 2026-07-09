package com.example.demo;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A tiny "is the app alive?" endpoint.
 * Visiting http://localhost:8080/api/health returns a small JSON object.
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "ok",
                "service", "landed-cost-api"
        );
    }
}
