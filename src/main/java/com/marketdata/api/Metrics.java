package com.marketdata.api;

import com.marketdata.engine.TickQueue;
import com.marketdata.util.MetricsCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metrics")
public class Metrics {

    @Autowired
    private MetricsCollector metrics;

    @Autowired
    private TickQueue tickQueue;

    @GetMapping
    public String getMetrics() {
        return metrics.summary(tickQueue.size());
    }
}
