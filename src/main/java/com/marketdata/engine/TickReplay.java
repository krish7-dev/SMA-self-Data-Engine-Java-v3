package com.marketdata.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marketdata.db.TickQuery;
import com.marketdata.model.Tick;
import com.marketdata.websocket.TickWebSocketHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TickReplay {

    private final TickQuery tickQuery;
    private final TickWebSocketHandler tickWebSocketHandler;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private Thread replayThread;
    private volatile boolean running = false;
    private volatile String runningSymbol = null;
    public TickReplay(TickQuery tickQuery, TickWebSocketHandler tickWebSocketHandler) {
        this.tickQuery = tickQuery;
        this.tickWebSocketHandler = tickWebSocketHandler;
    }

    public void startReplay(String symbol) {
        if (running) return;
        running = true;

        runningSymbol = symbol;

        replayThread = new Thread(() -> {
            try {
                List<Tick> ticks = tickQuery.getRecentTicks(symbol, 500);
                for (Tick tick : ticks) {
                    if (!running) break;
                    String json = objectMapper.writeValueAsString(tick);
                    tickWebSocketHandler.broadcast(json); // ✅ correct usage
                    Thread.sleep(300); // adjustable replay speed
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        replayThread.start();
    }

    public void stopReplay() {
        running = false;
        if (replayThread != null) replayThread.interrupt();
    }

    public boolean isRunning() {
        return running;
    }

    public String getRunningSymbol() {
        return runningSymbol;
    }
}
