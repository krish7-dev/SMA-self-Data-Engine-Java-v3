package com.marketdata.engine;

import com.marketdata.db.TickQuery;
import com.marketdata.model.Tick;
import com.marketdata.util.FileTickLogger;
import com.marketdata.util.MetricsCollector;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class TickWriter {

    private final TickQueue tickQueue;
    private final FileTickLogger fileLogger;
    private final TickQuery tickQuery;
    private final MetricsCollector metrics;

    private volatile boolean keepRunning = true;
    private Thread writerThread;

    public TickWriter(TickQueue tickQueue, FileTickLogger fileLogger, MetricsCollector metrics, TickQuery tickQuery
    ) {
        this.tickQueue = tickQueue;
        this.fileLogger = fileLogger;
        this.metrics = metrics;
        this.tickQuery = tickQuery;
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
            System.out.println("💾 Writing tick: " + tick);
            fileLogger.logTick(tick);     // fallback log
            tickQuery.save(tick);           // insert to DB
            metrics.incrementWritten();
        } catch (Exception e) {
            metrics.incrementFailures();
            System.err.println("❌ Failed to log or insert tick: " + e.getMessage());
        }
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
