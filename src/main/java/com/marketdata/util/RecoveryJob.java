package com.marketdata.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // ✅ Import JavaTimeModule
import com.marketdata.db.TickQuery;
import com.marketdata.model.Tick;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;

@Component
public class RecoveryJob {

    private static final String LOG_DIR = "tick_log/";
    private final ObjectMapper objectMapper;

    private final TickQuery tickQuery;

    @Autowired
    public RecoveryJob(TickQuery tickDao) {
        this.tickQuery = tickDao;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // ✅ Register support for java.time.Instant
    }

    @PostConstruct
    public void runRecovery() {
        System.out.println("🛠️ Running tick recovery...");

        try (Stream<Path> paths = Files.walk(Paths.get(LOG_DIR))) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".log"))
                    .forEach(this::recoverFromFile);
        } catch (IOException e) {
            System.err.println("❌ Failed to read tick_log directory: " + e.getMessage());
        }

        System.out.println("✅ Recovery process completed.");
    }

    private void recoverFromFile(Path filePath) {
        System.out.println("📂 Processing recovery file: " + filePath.getFileName());

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Tick tick = objectMapper.readValue(line, Tick.class);
                    tickQuery.save(tick); // Will implement save() in TickDao next
                } catch (Exception e) {
                    System.err.println("❌ Failed to parse/insert tick: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error reading log file: " + e.getMessage());
        }

        System.out.println("✅ Finished file: " + filePath.getFileName());
    }
}
