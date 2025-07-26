package com.marketdata.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("api/health")
    public String healthCheck() {
        String appStatus = "✅ Service is up";
        String dbStatus="";

        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (result != null && result == 1) {
                dbStatus = "✅ Database connected";
            } else {
                dbStatus = "⚠️ Database reachable but unexpected result";
            }
        } catch (Exception e) {
            dbStatus = "❌ Database error: " + e.getMessage();
        }

        return appStatus + " | " + dbStatus;
    }
}
