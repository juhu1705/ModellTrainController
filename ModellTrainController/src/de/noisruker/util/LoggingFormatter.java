package de.noisruker.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formatter für den {@link LoggingHandler}
 * @author Niklas
 */
class LoggingFormatter extends Formatter {

	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	@Override
	public String format(LogRecord record) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("[" + LocalDateTime.now().format(dateTimeFormatter) + "] [" + record.getLevel()
		+ " | " + record.getSourceClassName() + "] " + record.getMessage() + "\n");
		
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
