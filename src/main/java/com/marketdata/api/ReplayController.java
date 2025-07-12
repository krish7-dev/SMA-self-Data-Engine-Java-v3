package com.marketdata.api;

import com.marketdata.engine.TickReplay;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/replay")
public class ReplayController {

    private final TickReplay replayService;

    public ReplayController(TickReplay replayService){
        this.replayService = replayService;
    }

    @PostMapping("/start")
    public String start(@RequestParam String symbol) {
        if (replayService.isRunning()) {
            return "⚠️ Replay already running for " + replayService.getRunningSymbol();
        }
        replayService.startReplay(symbol);
        return "🔁 Replay started for " + symbol;
    }

    @PostMapping("/stop")
    public String stop() {
        replayService.stopReplay();
        return "⛔ Replay stopped";
    }
}
