package de.noisruker.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PropertyResourceBundle;
import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class Ref {

	public static final long UNIVERSAL_SERIAL_VERSION_UID = 983475897014889764L;

	public static String password = "1";

	public static PropertyResourceBundle language;

	public static int autoDriveNumber = 1;

	public static final Random rand = new Random();

	public static final Logger LOGGER;
	public static final String VERSION;
	public static final String PROJECT_NAME;
	public static Theme theme = Theme.LIGHT;
	public static final String HOME_FOLDER;
	static {
		HOME_FOLDER = System.getProperty("user.home") + "/.TrainController/";
	}


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
		} catch (IOException | NullPointerException | XmlPullParserException e) {

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
		} catch (IOException | NullPointerException | XmlPullParserException e) {
			//LOGGER.log(Level.SEVERE, "Error while reading pom.xml", e);
		}
		if (model == null)
			PROJECT_NAME = "ModellTrainController";
		else
			PROJECT_NAME = model.getName();
	}

}
