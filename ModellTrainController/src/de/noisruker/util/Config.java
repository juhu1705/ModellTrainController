package de.noisruker.util;

import de.noisruker.config.ConfigElement;
import de.noisruker.config.ConfigManager;
import de.noisruker.gui.GuiMain;
import de.noisruker.loconet.LocoNet;
import de.noisruker.main.GUILoader;
import de.noisruker.railroad.Train;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import jssc.SerialPortList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PropertyResourceBundle;
import java.util.logging.Level;

public class Config {

    public static final String MODE_MANUAL = "manual";
    public static final String MODE_RANDOM = "random";
    public static final String MODE_PLAN = "plan";

    @ConfigElement(defaultValue = "false", type = "check", description = "startImmediately.description", name = "startImmediately.text", location = "config.loconet", visible = true)
    public static boolean startImmediately;

    @ConfigElement(defaultValue = "false", type = "check", description = "fast_starting.description", name = "fast_starting.text", location = "config.controls", visible = true)
    public static boolean fastStarting;

    @ConfigElement(defaultValue = "false", type = "check", description = "fullScreen.description", name = "fullScreen.text", location = "config", visible = true)
    public static boolean fullScreen;

    @ConfigElement(defaultValue = "true", type = "check", description = "tips.description", name = "tips.text", location = "config.controls", visible = false)
    public static boolean tips;

    @ConfigElement(defaultValue = "", type = "choose", description = "port.description", name = "port.text", location = "config.loconet", visible = true)
    public static String port;

    @ConfigElement(defaultValue = "manual", type = "choose", description = "mode.description", name = "mode.text", location = "config.controls", visible = true)
    public static String mode;

    @ConfigElement(defaultValue = "DARK", type = "choose", description = "theme.description", name = "theme.text", location = "config", visible = true)
    public static String theme;

    @ConfigElement(defaultValue = "GERMAN", type = "choose", description = "language.description", name = "language.text", location = "config", visible = true)
    public static String language;

    @ConfigElement(defaultValue = "0", type = "count", description = "report_address.description", name = "report_address.text", location = "config.loconet", visible = true)
    public static int reportAddress = 0;

    @ConfigElement(defaultValue = "100", type = "count", description = "zoom.description", name = "zoom.text", location = "config", visible = false)
    public static int zoom = 100;

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
                Collections.addAll(modes, Config.MODE_MANUAL,
                        Config.MODE_PLAN);
                return modes;
            });

            ConfigManager.getInstance().registerOptionParameters("theme", () -> {
                ArrayList<String> themes = new ArrayList<>();
                for (Theme t : Theme.values())
                    themes.add(t.name());
                return themes;
            });

            ConfigManager.getInstance().registerOptionParameters("language", () -> {
                ArrayList<String> languages = new ArrayList<>();
                for (Language t : Language.values())
                    languages.add(t.name());
                return languages;
            });

            ConfigManager.getInstance().registerActionListener("fullScreen", () -> {
                if (GUILoader.getPrimaryStage() != null && GUILoader.getPrimaryStage().isResizable()) {
                    GUILoader.getPrimaryStage().setFullScreen(Config.fullScreen);
                }
            });

            ConfigManager.getInstance().registerActionListener("theme", () -> {
                Ref.theme = Theme.valueOf(Config.theme);
                if (Ref.theme == Theme.DARK || Ref.theme == Theme.LIGHT) {
                    Ref.J_METRO.setStyle(Ref.theme == Theme.DARK ? Style.DARK : Style.LIGHT);
                    for (JMetro metro : Ref.other_page_themes) {
                        metro.setStyle(Ref.theme == Theme.DARK ? Style.DARK : Style.LIGHT);
                    }
                }
            });

            ConfigManager.getInstance().registerActionListener("language", () -> {
                try {
                    InputStreamReader r;
                    Ref.language = new PropertyResourceBundle(r = new InputStreamReader(GUILoader.class.getResourceAsStream(Language.valueOf(Config.language).getLocation()), StandardCharsets.UTF_8));
                    r.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (GUILoader.getPrimaryStage() != null && GuiMain.getInstance() != null) {
                    Util.updateWindow(GUILoader.getPrimaryStage(), "/assets/layouts/main.fxml");
                    GUILoader.getPrimaryStage().setTitle(Ref.language.getString("window." + Ref.PROJECT_NAME));
                }
            });

            ConfigManager.getInstance().registerActionListener("mode", () -> {
                if (GuiMain.getInstance() != null) {
                    GuiMain.getInstance().setMode();
                }
                LocoNet.getInstance().getTrains().forEach(Train::reset);
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
