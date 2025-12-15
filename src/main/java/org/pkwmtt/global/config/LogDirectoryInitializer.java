package org.pkwmtt.global.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Ensures the log directory and app.log exist on application startup.
 * Implemented as a Spring component so it runs early during context initialization.
 */
@Component
@SuppressWarnings("unused")
public class LogDirectoryInitializer {
    
    @PostConstruct
    public void ensureLogFile () {
        Path logsDir = Paths.get("logs");
        try {
            // create directory if missing (no-op if exists)
            Files.createDirectories(logsDir);
            
            Path appLog = logsDir.resolve("app.log");
            // Open with CREATE and APPEND so it is created atomically if missing and left intact otherwise
            try (
              OutputStream os = Files.newOutputStream(
                appLog,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND
              )
            ) {
                // ensure the stream is valid without writing data
                os.flush();
            }
        } catch (IOException e) {
            // Avoid logging frameworks here because this runs during logging initialization
            System.err.println("Could not ensure logs/app.log: " + e.getMessage());
        }
    }
}
