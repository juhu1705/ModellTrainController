package de.noisruker.util;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.*;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ref {

	public static final long UNIVERSAL_SERIAL_VERSION_UID = 983475897014889764L;

	public static final int STANDARD_HOST_PORT = 12346;

	public static final String password = "1";

	public static final Logger LOGGER;
	public static final String VERSION;
	public static final String PROJECT_NAME;

	static {
		LOGGER = Logger.getLogger("schiffespiel");
		LOGGER.setUseParentHandlers(false);
		Handler handler = new LoggingHandler();
		handler.setFormatter(new LoggingFormatter());
		handler.setLevel(Level.ALL);
		LOGGER.addHandler(handler);
		LOGGER.setLevel(Level.ALL);
	}

	static {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = null;

		try {
			if ((new File("pom.xml")).exists())
				model = reader.read(new FileReader("pom.xml"));
			else
				model = reader.read(new InputStreamReader(Ref.class
						.getResourceAsStream("/META-INF/maven/ModellTrainController/ModellTrainController/pom.xml")));
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (model == null)
			VERSION = "0.1.0";
		else
			VERSION = model.getVersion();

		// VERSION = "Snapshot-0.0.1";
	}

	static {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = null;
		try {
			if ((new File("pom.xml")).exists())
				model = reader.read(new FileReader("pom.xml"));
			else
				model = reader.read(new InputStreamReader(Ref.class
						.getResourceAsStream("/META-INF/maven/ModellTrainController/ModellTrainController/pom.xml")));
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		} catch (XmlPullParserException e) {

		}
		if (model == null)
			PROJECT_NAME = "CaRP Assigner";
		else
			PROJECT_NAME = model.getName();
		// PROJECT_NAME = "KuFa Zuweiser";
	}

}
