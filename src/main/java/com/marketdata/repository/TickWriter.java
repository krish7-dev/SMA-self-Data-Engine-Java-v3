package com.marketdata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketdata.model.Tick;
import com.marketdata.util.MetricsCollectorUtil;
import com.marketdata.util.TickQueueUtil;
import com.marketdata.ws.TickWebSocketHandler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class TickWriter {

    private final TickQueueUtil tickQueue;
    private final TickQuery tickQuery;
    private final MetricsCollectorUtil metrics;
    private final TickWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    private volatile boolean keepRunning = true;
    private Thread writerThread;

    public TickWriter(
            TickQueueUtil tickQueue,
            MetricsCollectorUtil metrics,
            TickQuery tickQuery,
            TickWebSocketHandler webSocketHandler,
            ObjectMapper objectMapper // ✅ injected
    ) {
        this.tickQueue = tickQueue;
        this.metrics = metrics;
        this.tickQuery = tickQuery;
        this.webSocketHandler = webSocketHandler;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void startWriterThread() {
        writerThread = new Thread(() -> {
            System.out.println("🟢 TickWriterService started...");

            while (keepRunning || !tickQueue.isEmpty()) {
                try {
                    Tick tick = tickQueue.dequeue(); // blocks if empty
                    processTick(tick);
                } catch (InterruptedException e) {
                    System.err.println("⛔ Writer thread interrupted!");
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("❌ Failed to write tick: " + e.getMessage());
                }
            }
            System.out.println("✅ Graceful shutdown complete.");
        });

        writerThread.setDaemon(true);
        writerThread.start();
    }

    private void processTick(Tick tick) {
        try {
            if (isWithinMarketHours(tick.getTickTimestamp())) {
                System.out.println("⏱️ Ignored tick outside market hours: " + tick);
                return;
            }
            System.out.println("💾 Writing tick: " + tick);
            tickQuery.save(tick);
            metrics.incrementWritten();

            // ✅ Use injected ObjectMapper with JavaTimeModule
            String tickJson = objectMapper.writeValueAsString(tick);
            webSocketHandler.broadcast(tickJson);

        } catch (Exception e) {
            metrics.incrementFailures();
            System.err.println("❌ Failed to log, insert or broadcast tick: " + e.getMessage());
        }
    }

    private boolean isWithinMarketHours(Instant timestamp) {
        ZonedDateTime indiaTime = timestamp.atZone(ZoneId.of("Asia/Kolkata"));
        LocalTime tickTime = indiaTime.toLocalTime();

        LocalTime marketOpen = LocalTime.of(9, 15);
        LocalTime marketClose = LocalTime.of(15, 30);

        return !tickTime.isBefore(marketOpen) && !tickTime.isAfter(marketClose);
    }


    @PreDestroy
    public void shutdown() {
        System.out.println("🛑 Shutdown initiated...");
        keepRunning = false;
        if (writerThread != null) {
            writerThread.interrupt();
        }
    }
}
