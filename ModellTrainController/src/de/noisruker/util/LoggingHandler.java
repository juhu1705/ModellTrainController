package de.noisruker.util;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Handler für Logging-API
 * @author Niklas
 */
public class LoggingHandler extends Handler {

	@Override
	public void publish(LogRecord record) {
		String output = this.getFormatter().format(record);
		
		if (record.getLevel().intValue() >= Level.WARNING.intValue())
			System.err.print(output);
		else
			System.out.print(output);
	}
	
	@Override
	public synchronized void flush() {
		
	}
	
	@Override
	public synchronized void close() throws SecurityException {
		
	}
	
}
