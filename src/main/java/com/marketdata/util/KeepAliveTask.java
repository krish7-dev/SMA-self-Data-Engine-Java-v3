package com.marketdata.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KeepAliveTask {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String URL = "http://localhost:7000/api/health";

    KeepAliveTask(){
        System.out.println("✅ Starting Scheduler");
    }

    // Run every 5 minutes between 9:15 AM and 3:30 PM (IST)
    @Scheduled(cron = "0 */1 3-10 * * MON-SAT", zone = "UTC")  // 3-10 UTC = 8:30-15:30 IST (adjust buffer if needed)
    public void pingSelf() {
        try {
            restTemplate.getForObject(URL, String.class);
            System.out.println("Pinged self to stay alive");
        } catch (Exception e) {
            System.err.println("Failed to ping self: " + e.getMessage());
        }
    }
}
