package de.noisruker.main;


import de.noisruker.gui.GuiLoading;
import de.noisruker.gui.GuiStart;
import de.noisruker.util.Config;
import de.noisruker.util.Ref;
import de.noisruker.util.Theme;
import de.noisruker.util.Util;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.PropertyResourceBundle;

public class GUILoader extends Application {

    private static Stage primaryStage = null;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void closePrimaryStage() {
        primaryStage = null;
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(GUILoader.class.getResource("/assets/layouts/loading.fxml"), Ref.language);

        Scene s = new Scene(root);

        if (!Ref.theme.getLocation().equalsIgnoreCase("remove")) {
            if(Ref.theme.equals(Theme.LIGHT) || Ref.theme.equals(Theme.DARK)) {
                Ref.J_METRO.setStyle(Ref.theme == Theme.DARK ? Style.DARK : Style.LIGHT);
                Ref.J_METRO.setScene(s);
            } else
                s.getStylesheets().add(Ref.theme.getLocation());
        }
        if(Ref.theme.equals(Theme.DARK))
            s.getStylesheets().add(Ref.DARK_THEME_FIXES);

        s.getStylesheets().add(Ref.THEME_IMPROVEMENTS);
        stage.setTitle(Ref.PROJECT_NAME);
        stage.setScene(s);
        stage.centerOnScreen();
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> Util.onClose(null));

        Image i;

        if (new File("./resources/assets/textures/logo/logo.png").exists())
            i = new Image(new File("./resources/assets/textures/logo/logo.png").toURI().toString());
        else
            i = new Image("/assets/textures/logo/logo.png");
        stage.getIcons().add(i);

        stage.show();

        GUILoader.primaryStage = stage;

        GuiStart.stage = primaryStage;

        GuiLoading.checkForUpdates();
    }

    public static void main(String[] args) {
        Config.register();

        launch(args);
    }
}
