package com.marketdata.util;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;

@Component
public class FileArchiverService {

    private static final String LOG_DIR = "tick_log/";
    private static final String ARCHIVE_DIR = "tick_archive/";

    @PostConstruct
    public void archiveOldFiles() {
        System.out.println("📦 ArchiverService started...");

        try {
            Files.createDirectories(Paths.get(ARCHIVE_DIR)); // ensure archive folder exists
            DirectoryStream<Path> logFiles = Files.newDirectoryStream(Paths.get(LOG_DIR), "*.log");

            for (Path file : logFiles) {
                String fileName = file.getFileName().toString();

                if (!fileName.contains(LocalDate.now().toString())) {
                    Path target = Paths.get(ARCHIVE_DIR + file.getFileName());
                    Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("📁 Archived old file: " + fileName);
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error during log archiving: " + e.getMessage());
        }

        System.out.println("✅ ArchiverService completed.");

    }

}
