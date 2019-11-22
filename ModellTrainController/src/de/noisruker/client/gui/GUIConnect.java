package de.noisruker.client.gui;

import de.noisruker.client.Client;
import de.noisruker.util.Ref;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import static de.noisruker.util.Ref.PROJECT_NAME;
import static de.noisruker.util.Ref.VERSION;

public class GUIConnect {

	public static String password, name;
	public static boolean canOpen = false;

	@FXML
	private TextField taddress;

	@FXML
	private TextField tname;

	@FXML
	private TextField tpassword;

	@FXML
	void onConnect(ActionEvent event) {

		GUIConnect.password = this.tpassword.getText();
		GUIConnect.name = this.tname.getText();

		try {
			Client.connectToServer(this.taddress.getText());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		((Stage) ((Button) event.getSource()).getScene().getWindow()).close();

		while (!canOpen)
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

		Ref.LOGGER.info("g");
		Stage primaryStage = new Stage();

		Image i;

		if (new File("./resources/assets/textures/logo/logo.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/logo.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/logo.png");

		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/assets/layouts/GUIClient.fxml"));
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
		Ref.LOGGER.info("g");
	}

}
