package com.rachana.processor.log.generator;

import java.util.List;
import java.util.Map;

public final class MessagePool {
    public static final Map<String, List<String>> MESSAGE_POOL = Map.of(
            "INFO", List.of(
                    "User logged in successfully",
                    "Database connection established",
                    "Cache cleared",
                    "Background task completed",
                    "Request processed in 120ms"
            ),
            "WARN", List.of(
                    "High memory usage detected",
                    "Slow query detected in module X",
                    "Deprecated API called by client",
                    "Disk space reaching 90% threshold",
                    "Retry attempt 1 for service Y"
            ),
            "ERROR", List.of(
                    "NullPointerException in AuthController",
                    "Failed to connect to microservice",
                    "Invalid JWT signature",
                    "User not found with ID: 404",
                    "File upload failed: size limit exceeded"
            ),
            "FATAL", List.of(
                    "System shutdown initiated",
                    "Kernel panic: out of memory",
                    "Primary database cluster unreachable",
                    "Encryption key corrupted"
            )
    );

}
