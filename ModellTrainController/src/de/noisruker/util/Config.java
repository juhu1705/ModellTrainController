package de.noisruker.util;

import de.noisruker.config.ConfigElement;
import de.noisruker.config.ConfigManager;
import javafx.collections.FXCollections;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
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

    @ConfigElement(defaultValue = "", type = "choose", description = "port.description", name = "port.text", location = "config.loconet", visible = true)
    public static String port;

    @ConfigElement(defaultValue = "", type = "choose", description = "mode.description", name = "mode.text", location = "config", visible = true)
    public static String mode;

    @ConfigElement(defaultValue = "DARK", type = "choose", description = "theme.description", name = "theme.text", location = "config", visible = true)
    public static String theme;

    public static void register() {
        try {
            ConfigManager.getInstance().register(Config.class);

            ConfigManager.getInstance().registerOptionParameters("port", () -> {
                ArrayList<String> ports = new ArrayList<>();
                Collections.addAll(ports, SerialPortList.getPortNames());
                return ports;
            });

            ConfigManager.getInstance().registerOptionParameters("mode", () -> {
                ArrayList<String> modes = new ArrayList<>();
                Collections.addAll(modes, "Drive Manual",
                        "Drive With Plan",
                        "Drive Randomly");
                return modes;
            });

            ConfigManager.getInstance().registerOptionParameters("theme", () -> {
                ArrayList<String> themes = new ArrayList<>();
                for(Theme t: Theme.values())
                    themes.add(t.name());
                return themes;
            });

            ConfigManager.getInstance().registerActionListener("theme", () -> {
                Ref.theme = Theme.valueOf(Config.theme);
                if(Ref.theme == Theme.DARK || Ref.theme == Theme.LIGHT) {
                    Ref.J_METRO.setStyle(Ref.theme == Theme.DARK ? Style.DARK : Style.LIGHT);
                    for(JMetro metro: Ref.other_page_themes) {
                        metro.setStyle(Ref.theme == Theme.DARK ? Style.DARK : Style.LIGHT);
                    }
                }
            });

            if (!Files.exists(FileSystems.getDefault().getPath(Ref.HOME_FOLDER + "config.cfg"),
                    LinkOption.NOFOLLOW_LINKS))
                ConfigManager.getInstance().loadDefault();
            else
                ConfigManager.getInstance().load(Ref.HOME_FOLDER + "config.cfg");
        } catch (IOException | SAXException e) {
            Ref.LOGGER.log(Level.SEVERE, "Error while loading Config", e);
        }

    }
}
