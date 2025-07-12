package com.marketdata.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marketdata.model.Tick;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

@Component
public class FileTickLogger {

    private final String basePath = "tick_log/";
    private final ObjectMapper objectMapper;

    public FileTickLogger() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // ✅ Register Java Time support
    }

    public synchronized void logTick(Tick tick) {
        String filename = basePath + "tick-fallback-" + LocalDate.now() + ".log";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            String json = objectMapper.writeValueAsString(tick); // ✅ JSON-safe tick
            writer.write(json);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("❌ Failed to write tick to log file: " + e.getMessage());
        }
    }
}
