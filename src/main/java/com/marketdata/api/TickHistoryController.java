package com.marketdata.api;

import com.marketdata.db.TickQuery;
import com.marketdata.model.Tick;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/history")
public class TickHistoryController {

    private final TickQuery tickQuery;

    public TickHistoryController(TickQuery tickQuery) {
        this.tickQuery = tickQuery;
    }

    @GetMapping
    public List<Tick> getTicks(
            @RequestParam String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end
    ) {
        return tickQuery.getTicksBetween(symbol.toUpperCase(), start, end);
    }
}
