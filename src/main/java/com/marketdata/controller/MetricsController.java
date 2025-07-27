package com.marketdata.controller;

import com.marketdata.util.MetricsCollectorUtil;
import com.marketdata.util.TickQueueUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    @Autowired
    private MetricsCollectorUtil metrics;

    @Autowired
    private TickQueueUtil tickQueue;

    @GetMapping
    public String getMetrics() {
        return metrics.summary(tickQueue.size());
    }
}
