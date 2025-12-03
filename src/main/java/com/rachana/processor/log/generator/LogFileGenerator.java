package com.rachana.processor.log.generator;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class LogFileGenerator {
    private String filePath = "./log-file.log";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public void writeToFile(String description, String level) throws IOException {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logLine = String.format("%s %s %s%n",
                level.toUpperCase(),
                description,
                timestamp);
        Path logPath = Paths.get(filePath);
        try {
            Files.write(logPath, logLine.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
        catch (IOException exception){
            throw new IOException(exception);
        }
    }

    public void writeWithWriter(String description, String level) throws IOException {
        Writer writer = new FileWriter(filePath);
        String logLine = String.format("%s %s",
                level.toUpperCase(),
                description);
        writer.write(logLine);
        writer.flush();
    }
}
