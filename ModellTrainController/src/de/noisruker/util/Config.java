package de.noisruker.util;

import de.noisruker.config.ConfigElement;
import de.noisruker.config.ConfigManager;
import javafx.collections.FXCollections;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.logging.Level;

public class Config {

    @ConfigElement(defaultValue = "false", type = "check", description = "startImmediately.description", name = "startImmediately.text", location = "config", visible = true)
    public static boolean startImmediately;

    @ConfigElement(defaultValue = "false", type = "check", description = "addTrainsFirst.description", name = "addTrainsFirst.text", location = "config", visible = true)
    public static boolean addTrainsFirst;

    @ConfigElement(defaultValue = "", type = "choose", description = "port.description", name = "port.text", location = "config", visible = true)
    public static String port;

    public static void register() {
        try {
            ConfigManager.getInstance().register(Config.class);

            if (!Files.exists(FileSystems.getDefault().getPath(Ref.HOME_FOLDER + "config.cfg"),
                    LinkOption.NOFOLLOW_LINKS))
                ConfigManager.getInstance().loadDefault();
            else
                ConfigManager.getInstance().load(Ref.HOME_FOLDER + "config.cfg");

            ConfigManager.getInstance().registerOptionParameters("port.text", () -> {
                ArrayList<String> ports = new ArrayList<>();
                for (String s : jssc.SerialPortList.getPortNames())
                    ports.add(s);

                ports.add("Connect to Server");
                return ports;
            });
        } catch (IOException | SAXException e) {
            Ref.LOGGER.log(Level.SEVERE, "Error while loading Config", e);
        }

    }
}
