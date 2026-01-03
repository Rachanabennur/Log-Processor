package com.rachana.processor;

public class LogEntry {
    private String timestamp;
    private String level;
    private String message;

    public LogEntry(String timestamp, String level, String message) {
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
    }

    // Getters
    public String getLevel() { return level; }
    public String getMessage() { return message; }
}
