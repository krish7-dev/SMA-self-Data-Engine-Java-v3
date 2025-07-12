package com.marketdata.api;

import com.marketdata.engine.TickQueue;
import com.marketdata.model.Tick;
import com.marketdata.enums.TickSource;
import com.marketdata.util.MetricsCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tick")
public class TickController {

    private final TickQueue tickQueue;

    @Autowired
    public TickController(TickQueue tickQueue) {
        this.tickQueue = tickQueue;
    }

    @Autowired
    private MetricsCollector metrics;

    @PostMapping
    public ResponseEntity<String> receiveTick(@RequestBody Tick tick) {
        if(true){
            tick.setSource(TickSource.post);
            tickQueue.enqueue(tick);
            metrics.incrementReceived();
            return ResponseEntity.ok("✅ Tick accepted and enqueued");
        }
        else{
            System.out.println("⛔ Rejected tick : " + tick.getSymbol());
            return ResponseEntity.ok("🚫 Tick Rejected ");
        }
    }
}
