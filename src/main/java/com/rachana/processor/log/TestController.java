package com.rachana.processor.log;

import com.rachana.processor.log.generator.LogFileGenerator;
import com.rachana.processor.log.processor.LogProcessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TestController {
    private final LogFileGenerator logFileGenerator;
    private final LogProcessor logProcessor;

    public TestController(LogFileGenerator logFileGenerator, LogProcessor logProcessor) {
        this.logFileGenerator = logFileGenerator;
        this.logProcessor = logProcessor;
    }

    @GetMapping("/write")
    public void write() throws IOException {
            logFileGenerator.generateRandomLogs(200L, 1000);
    }

    @GetMapping("/analyse")
    public void analyse() throws IOException {
        logProcessor.processLogs("logs");
    }
}
