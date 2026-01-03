package com.rachana.processor.log.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Component
public class LogFileGenerator {
    // Added SSS for milliseconds to ensure unique filenames and precise logs
    private static final DateTimeFormatter LOG_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static final DateTimeFormatter FILE_NAME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private final Random randomGenerator = new Random();
    private final List<String> levels = List.of("INFO", "WARN", "ERROR", "FATAL");
    @Autowired
    private ApiLogService apiLogService;

    public void generateRandomLogs(Long totalLinesRequested, long maxFileSizeBytes) throws IOException {
        long totalLinesWritten = 0L;
        int fileNum = 0;

        while (totalLinesWritten < totalLinesRequested) {
            String fileTimestamp = LocalDateTime.now().format(FILE_NAME_FORMATTER);
            String fileName = String.format("log_batch_%03d_%s.log", fileNum, fileTimestamp);
            Path path = Paths.get("./logs/" + fileName);
            Files.createDirectories(path.getParent());

            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {
                boolean fileMaxSizeReached = false;

                while (!fileMaxSizeReached && totalLinesWritten < totalLinesRequested) {
                    var level = levels.get(randomGenerator.nextInt(levels.size()));
                    var messages = MessagePool.MESSAGE_POOL.get(level);
                    var randomMessage = messages.get(randomGenerator.nextInt(messages.size()));

                    String logEntry = String.format("%s [%s] %s%n",
                            LocalDateTime.now().format(LOG_FORMATTER), level.toUpperCase(), randomMessage);

                    writer.write(logEntry);
                    totalLinesWritten++;

                    // Check size: We flush to ensure the disk reflects the current size
                    writer.flush();
                    if (Files.size(path) >= maxFileSizeBytes) {
                        fileMaxSizeReached = true;
                    }
                }
            }
            fileNum++;
        }
    }

    public void generateApiLogs(int count) throws IOException {
        for (int i = 0; i < count; i++) {
            String liveMessage = apiLogService.fetchLiveLogMessage();
            String level = liveMessage.contains("API_ERROR") ? "ERROR" : "INFO";

            // You can reuse your writeToFile logic here or the rotated logic
            writeSingleLog("./logs/api_live.log", liveMessage, level);

            // Add a small delay to respect API rate limits
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }
    }

    /**
     * Writes a single log entry to a specified file.
     * Useful for one-off API logs or error tracking.
     */
    private void writeSingleLog(String targetPath, String description, String level) throws IOException {
        Path logPath = Paths.get(targetPath);

        // 1. Ensure the parent directory exists (e.g., the /logs/ folder)
        if (logPath.getParent() != null) {
            Files.createDirectories(logPath.getParent());
        }

        // 2. Format the entry
        String timestamp = LocalDateTime.now().format(LOG_FORMATTER);
        String logLine = String.format("%s [%s] %s%n",
                timestamp,
                level.toUpperCase(),
                description);

        // 3. Write/Append the string to the file
        try {
            Files.writeString(
                    logPath,
                    logLine,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            // Log the error to console so you know why the write failed
            System.err.println("Failed to write to " + targetPath + ": " + e.getMessage());
            throw e;
        }
    }
}
