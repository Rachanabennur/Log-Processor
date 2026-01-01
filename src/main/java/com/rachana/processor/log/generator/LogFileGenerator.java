package com.rachana.processor.log.generator;

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

    public void generateRandomLogs(Long totalLinesRequested) throws IOException {
        long totalLinesWritten = 0L;
        int fileNum = 0;

        // FIXED: Use < instead of <= to prevent an extra empty file at the end
        while (totalLinesWritten < totalLinesRequested) {

            String fileTimestamp = LocalDateTime.now().format(FILE_NAME_FORMATTER);
            // Including fileNum first helps with alphabetical sorting in folders
            String fileName = String.format("log_batch_%03d_%s.log", fileNum, fileTimestamp);
            Path path = Paths.get("./logs/" + fileName);

            Files.createDirectories(path.getParent());

            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {
                long linesInCurrentFile = 0L;

                while (linesInCurrentFile < 100L && totalLinesWritten < totalLinesRequested) {
                    var level = levels.get(randomGenerator.nextInt(levels.size()));
                    var messages = MessagePool.MESSAGE_POOL.get(level);
                    var randomMessage = messages.get(randomGenerator.nextInt(messages.size()));

                    // Use a format that is easy for Phase 2 to parse
                    String logEntry = String.format("%s [%s] %s%n",
                            LocalDateTime.now().format(LOG_FORMATTER),
                            level.toUpperCase(),
                            randomMessage);

                    writer.write(logEntry);

                    linesInCurrentFile++;
                    totalLinesWritten++;
                }
            }
            fileNum++;

            // Optional: Small sleep to ensure the system clock moves forward
            // if you aren't using milliseconds in filenames.
        }
    }
}
