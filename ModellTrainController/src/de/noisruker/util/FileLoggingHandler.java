package de.noisruker.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Handler für Logging-API
 *
 * @author Niklas
 */
public class FileLoggingHandler extends Handler {

    private final PrintWriter writer;

    public FileLoggingHandler(String file) throws IOException {
        this.writer = new PrintWriter(file, StandardCharsets.UTF_8);
    }

    @Override
    public void publish(LogRecord record) {
        String output = this.getFormatter().format(record);

        this.writer.print(output);
    }

    @Override
    public synchronized void flush() {
        this.writer.flush();
    }

    @Override
    public synchronized void close() throws SecurityException {
        this.writer.close();
    }

}
