package com.marketdata.util;


import com.marketdata.engine.TickQueue;
import com.marketdata.model.Tick;
import com.marketdata.util.FileTickLogger;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class ShutDownDBWriter {

    private final TickQueue tickQueue;
    private final FileTickLogger fileLogger;

    public ShutDownDBWriter(TickQueue tickQueue, FileTickLogger fileLogger) {
        this.tickQueue = tickQueue;
        this.fileLogger = fileLogger;
    }

    @PreDestroy
    public void onShutdown() {
        System.out.println("🛑 Shutdown initiated...");

        int remaining = tickQueue.size();
        System.out.println("📦 Remaining ticks in queue: " + remaining);

        for (int i = 0; i < remaining; i++) {
            try {
                Tick tick = tickQueue.dequeue();
                fileLogger.logTick(tick); // fallback log
                System.out.println("💾 Flushed tick on shutdown: " + tick);
            } catch (InterruptedException e) {
                System.err.println("⚠️ Interrupted while flushing ticks.");
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("✅ Graceful shutdown complete.");
    }
}
