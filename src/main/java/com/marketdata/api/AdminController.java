package com.marketdata.api;

import com.marketdata.util.ExitService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ExitService exitService;

    public AdminController(ExitService exitService) {
        this.exitService = exitService;
    }

    @PostMapping("/shutdown")
    public String shutdown() {
        new Thread(() -> {
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            exitService.shutdown(0);
        }).start();
        return "Shutting down service...";
    }
}
