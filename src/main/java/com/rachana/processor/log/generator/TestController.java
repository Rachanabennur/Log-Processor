package com.rachana.processor.log.generator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TestController {
    private final LogFileGenerator logFileGenerator;

    public TestController(LogFileGenerator logFileGenerator) {
        this.logFileGenerator = logFileGenerator;
    }

    @GetMapping("/write")
    public void write() throws IOException {
            logFileGenerator.generateRandomLogs(200L);
    }
}
