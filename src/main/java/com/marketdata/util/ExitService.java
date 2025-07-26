package com.marketdata.util;

import org.springframework.stereotype.Component;

@Component
public class ExitService {

    /**
     * Gracefully shuts down the application (Spring Boot context).
     * @param exitCode The exit code (0 = success, non-zero = error)
     */
    public void shutdown(int exitCode) {
        System.out.println("🛑 Shutting down application with exit code " + exitCode + "...");
        // Exit gracefully so Spring closes beans/resources
        System.exit(exitCode);
    }

    /**
     * Force-kills the process immediately (use with caution).
     */
    public void forceKill() {
        System.out.println("💀 Force killing application...");
        Runtime.getRuntime().halt(1);  // Immediately stops JVM, skipping cleanup
    }
}
