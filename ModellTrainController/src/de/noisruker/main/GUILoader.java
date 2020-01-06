package de.noisruker.main;

import static de.noisruker.util.Ref.LOGGER;
import static de.noisruker.util.Ref.PROJECT_NAME;
import static de.noisruker.util.Ref.VERSION;

import java.io.File;
import java.io.IOException;
import java.util.PropertyResourceBundle;

import de.noisruker.util.Ref;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GUILoader extends Application {

	public static Stage secondaryStage;
	public static Scene scene;
	private static Stage primaryStage;

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		LOGGER.info("Starte: " + PROJECT_NAME + " | Version: " + VERSION);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// LOGGER.info(getClass().getResource("/de/juhu/guiFX/GUI.fxml") + "");
//		LOGGER.info(new File("./de/juhu/guiFX/GUI.fxml").toURI() + "");
//		LOGGER.info(new File("./de/juhu/guiFX/GUI.fxml").toURI().toURL() + "");

		Image i;

		if (new File("./resources/assets/textures/logo/logo.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/logo.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/logo.png");

		Parent root = FXMLLoader.load(getClass().getResource("/assets/layouts/GUIStart.fxml"),
				Ref.language = new PropertyResourceBundle(
						getClass().getResourceAsStream("/assets/language/de.properties")));
		Scene s = new Scene(root);

		s.getStylesheets().add("/assets/styles/dark_theme.css");

		primaryStage.setMinWidth(222);
		primaryStage.setMinHeight(222);
		primaryStage.setTitle(PROJECT_NAME + " | " + VERSION);
		primaryStage.setScene(s);
		primaryStage.setOnCloseRequest(c -> System.exit(0));

		primaryStage.centerOnScreen();
		primaryStage.initStyle(StageStyle.DECORATED);

		primaryStage.getIcons().add(i);

		primaryStage.show();
		GUILoader.primaryStage = primaryStage;
		scene = s;
	}

	public void starting2() throws IOException {

		secondaryStage.show();
		primaryStage.close();
		primaryStage = secondaryStage;
	}

}
