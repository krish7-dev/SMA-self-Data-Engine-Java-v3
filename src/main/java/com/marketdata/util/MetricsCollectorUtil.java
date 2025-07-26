package com.marketdata.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MetricsCollectorUtil {
    private final AtomicInteger totalTicksReceived = new AtomicInteger();
    private final AtomicInteger totalTicksWritten = new AtomicInteger();
    private final AtomicInteger totalTickFailures = new AtomicInteger();

    public void incrementReceived() {
        totalTicksReceived.incrementAndGet();
    }

    public void incrementWritten() {
        totalTicksWritten.incrementAndGet();
    }

    public void incrementFailures() {
        totalTickFailures.incrementAndGet();
    }

    public int getTotalReceived() {
        return totalTicksReceived.get();
    }

    public int getTotalWritten() {
        return totalTicksWritten.get();
    }

    public int getTotalFailures() {
        return totalTickFailures.get();
    }

    public String summary(int currentQueueSize) {
        return "\n🧠 Metrics:\n" +
                " - Received: " + getTotalReceived() + "\n" +
                " - Written: " + getTotalWritten() + "\n" +
                " - Failed: " + getTotalFailures() + "\n" +
                " - Queue Size: " + currentQueueSize;
    }
}
