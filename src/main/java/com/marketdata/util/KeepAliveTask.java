package com.marketdata.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KeepAliveTask {

    @Value("${health.url}")  // inject from application-local or test
    private String healthUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    KeepAliveTask(){
        System.out.println("✅ Starting Scheduler");
    }

    // Run every 5 minutes between 9:15 AM and 3:30 PM (IST)
    @Scheduled(cron = "0 */10 * * * MON-FRI", zone = "UTC")  // 3-10 UTC = 8:30-15:30 IST (adjust buffer if needed)
    public void pingSelf() {
        try {
            restTemplate.getForObject(healthUrl, String.class);
            System.out.println("Pinged self to stay alive : "+healthUrl);
        } catch (Exception e) {
            System.err.println("Failed to ping self: " + e.getMessage());
        }
    }
}
