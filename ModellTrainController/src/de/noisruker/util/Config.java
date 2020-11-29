package de.noisruker.util;

import de.noisruker.config.ConfigElement;
import de.noisruker.config.ConfigManager;
import javafx.collections.FXCollections;
import jssc.SerialPortList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;

public class Config {

    @ConfigElement(defaultValue = "false", type = "check", description = "startImmediately.description", name = "startImmediately.text", location = "config", visible = true)
    public static boolean startImmediately;

    @ConfigElement(defaultValue = "", type = "choose", description = "port.description", name = "port.text", location = "config", visible = true)
    public static String port;

    @ConfigElement(defaultValue = "", type = "choose", description = "mode.description", name = "mode.text", location = "config", visible = true)
    public static String mode;

    public static void register() {
        try {
            ConfigManager.getInstance().register(Config.class);

            if (!Files.exists(FileSystems.getDefault().getPath(Ref.HOME_FOLDER + "config.cfg"),
                    LinkOption.NOFOLLOW_LINKS))
                ConfigManager.getInstance().loadDefault();
            else
                ConfigManager.getInstance().load(Ref.HOME_FOLDER + "config.cfg");

            ConfigManager.getInstance().registerOptionParameters("port", () -> {
                ArrayList<String> ports = new ArrayList<>();
                Collections.addAll(ports, SerialPortList.getPortNames());
                return ports;
            });

            ConfigManager.getInstance().registerOptionParameters("mode", () -> {
                ArrayList<String> ports = new ArrayList<>();
                Collections.addAll(ports, "Drive Manual",
                        "Drive With Plan",
                        "Drive Randomly");
                return ports;
            });
        } catch (IOException | SAXException e) {
            Ref.LOGGER.log(Level.SEVERE, "Error while loading Config", e);
        }

    }
}
