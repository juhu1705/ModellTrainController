package de.noisruker.main;

import static de.noisruker.util.Ref.PROJECT_NAME;
import static de.noisruker.util.Ref.VERSION;

import java.io.File;
import java.io.IOException;
import java.util.PropertyResourceBundle;

import de.noisruker.net.Side;
import de.noisruker.util.Ref;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GUIStart {

	@FXML
	void onStartClient(ActionEvent event) {
		Stage primaryStage = new Stage();

		Image i;

		if (new File("./resources/assets/textures/logo/logo.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/logo.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/logo.png");

		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/assets/layouts/GUIConnect.fxml"),
					new PropertyResourceBundle(getClass().getResourceAsStream("/assets/language/de.properties")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (root == null)
			return;
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

		Ref.side = Side.CLIENT;

		((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
	}

	@FXML
	void onStartServer(ActionEvent event) {
		Stage primaryStage = new Stage();

		Image i;

		if (new File("./resources/assets/textures/logo/logo.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/logo.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/logo.png");

		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/assets/layouts/ServerStart.fxml"),
					new PropertyResourceBundle(getClass().getResourceAsStream("/assets/language/de.properties")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (root == null)
			return;
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

		Ref.side = Side.SERVER;

		((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
	}

}
