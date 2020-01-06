package de.noisruker.server.gui;

import static de.noisruker.util.Ref.PROJECT_NAME;
import static de.noisruker.util.Ref.VERSION;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import de.noisruker.net.Side;
import de.noisruker.server.loconet.LocoNet;
import de.noisruker.util.Ref;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jssc.SerialPortException;

public class GUIConnect implements Initializable {

	@FXML
	public ComboBox<String> box;

	@FXML
	public TextField textfield;

	public void onStart(ActionEvent event) {
		if (box.valueProperty().get() == null)
			return;

		if (textfield.getText() == null || textfield.getText().isEmpty())
			return;

		Ref.password = textfield.getText();

		try {
			LocoNet.getInstance().start(box.getValue());
		} catch (SerialPortException e1) {
			e1.printStackTrace();
		}

		Stage primaryStage = new Stage();

		Image i;

		if (new File("./resources/assets/textures/logo/logo.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/logo.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/logo.png");

		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/assets/layouts/GUIServer.fxml"),
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		for (String s : jssc.SerialPortList.getPortNames())
			box.getItems().setAll(s);

	}

}
