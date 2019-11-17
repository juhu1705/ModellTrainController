package de.noisruker.util;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ref {
	
	public static final long UNIVERSAL_SERIAL_VERSION_UID = 983475897014889764L;
	
	public static final int STANDARD_HOST_PORT = 2357;
	
	public static final Logger LOGGER;
	static {
		LOGGER = Logger.getLogger("schiffespiel");
		LOGGER.setUseParentHandlers(false);
		Handler handler = new LoggingHandler();
		handler.setFormatter(new LoggingFormatter());
		handler.setLevel(Level.ALL);
		LOGGER.addHandler(handler);
		LOGGER.setLevel(Level.ALL);
	}

}
