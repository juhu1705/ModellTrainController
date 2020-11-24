package de.noisruker.main;


import de.noisruker.gui.GuiLoading;
import de.noisruker.gui.GuiStart;
import de.noisruker.util.Config;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.InputStreamReader;
import java.util.PropertyResourceBundle;

public class GUILoader extends Application {

    private static Stage primaryStage = null;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        InputStreamReader r;
        PropertyResourceBundle language = new PropertyResourceBundle(r = new InputStreamReader(GUILoader.class.getResourceAsStream("/assets/language/de.properties")));
        r.close();

        Parent root = FXMLLoader.load(GUILoader.class.getResource("/assets/layouts/loading.fxml"), language);
        Ref.language = language;

        Scene s = new Scene(root);
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
