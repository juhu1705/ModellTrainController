package de.noisruker.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formatter für den {@link LoggingHandler}
 *
 * @author Niklas
 */
class LoggingFormatter extends Formatter {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public String format(LogRecord record) {

        StringBuilder sb = new StringBuilder();

        sb.append("[").append(LocalDateTime.now().format(dateTimeFormatter)).append("] [").append(record.getLevel())
                .append(" | ").append(record.getSourceClassName()).append("] ").append(record.getMessage()).append("\n");

        Throwable thrown = record.getThrown();
        if (thrown != null) {
            sb.append(thrown);
            sb.append("\n");

            for (StackTraceElement ste : thrown.getStackTrace()) {
                sb.append("    at ");
                sb.append(ste);
                sb.append("\n");
            }
        }

        return sb.toString();
    }

}
