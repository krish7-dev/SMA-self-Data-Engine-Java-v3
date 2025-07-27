package com.marketdata.controller;

import com.marketdata.util.KeepAliveTask;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scheduler")
public class SchedulerController {

    private final KeepAliveTask keepAliveTask;

    public SchedulerController(KeepAliveTask keepAliveTask) {
        this.keepAliveTask = keepAliveTask;
    }

    @PostMapping("/enable")
    public String enableScheduler() {
        keepAliveTask.setEnabled(true);
        System.out.println("✅ KeepAlive scheduler ENABLED.");
        return "✅ KeepAlive scheduler ENABLED.";
    }

    @PostMapping("/disable")
    public String disableScheduler() {
        keepAliveTask.setEnabled(false);
        System.out.println("⏸️ KeepAlive scheduler DISABLED.");
        return "⏸️ KeepAlive scheduler DISABLED.";
    }

    @GetMapping("/status")
    public String schedulerStatus() {
        return keepAliveTask.isEnabled()
                ? "🟢 Scheduler is running."
                : "⏸️ Scheduler is paused.";
    }
}
