package com.rachana.processor.log.processor;

import com.rachana.processor.LogEntry;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LogProcessor {

    public void processLogs(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);

        // 1. Read all lines from all .log files in the folder
        List<LogEntry> allEntries = Files.list(path)
                .filter(file -> file.toString().endsWith(".log"))
                .flatMap(file -> {
                    try {
                        return Files.lines(file);
                    } catch (IOException e) {
                        return Stream.empty();
                    }
                })
                .map(this::parseLine)
                .filter(Objects::nonNull)
                .toList();

        // 2. Statistics: Count by Level
        Map<String, Long> statsByLevel = allEntries.stream()
                .collect(Collectors.groupingBy(LogEntry::getLevel, Collectors.counting()));

        // 3. Find Most Common Error Message
        String topError = allEntries.stream()
                .filter(e -> e.getLevel().equals("ERROR"))
                .collect(Collectors.groupingBy(LogEntry::getMessage, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No errors found");

        // 4. Print Summary
        System.out.println("--- LOG SUMMARY REPORT ---");
        statsByLevel.forEach((level, count) -> System.out.println(level + ": " + count));
        System.out.println("Most Common Error: " + topError);
    }

    private LogEntry parseLine(String line) {
        try {
            // Regex or Split based on your format: "timestamp [LEVEL] message"
            // Example: 2026-01-03 21:00:00.SSS [INFO] User logged in
            String[] parts = line.split(" ", 3);
            String timestamp = parts[0] + " " + parts[1];
            String level = parts[2].substring(parts[2].indexOf("[") + 1, parts[2].indexOf("]"));
            String message = parts[2].substring(parts[2].indexOf("]") + 2);

            return new LogEntry(timestamp, level, message);
        } catch (Exception e) {
            return null; // Skip malformed lines
        }
    }
}
