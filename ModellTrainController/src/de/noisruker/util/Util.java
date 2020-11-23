package de.noisruker.util;

import de.noisruker.config.ConfigManager;
import de.noisruker.server.loconet.messages.MessageType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.logging.Level;

public class Util {

	public static byte getCheckSum(byte... bytes) {
		byte xoredbydes = 0;

		for (byte b : bytes)
			xoredbydes ^= b;

		return (byte) ~xoredbydes;
	}

	public static byte[] addCheckSum(byte... bytes) {
		byte[] b = new byte[bytes.length + 1];
		System.arraycopy(bytes, 0, b, 0, bytes.length);

		b[b.length - 1] = Util.getCheckSum(bytes);
		return b;
	}

	public static byte[] addOpCode(byte opCode, byte... bytes) {
		byte[] b = new byte[bytes.length + 1];
		b[0] = opCode;

		System.arraycopy(bytes, 0, b, 1, bytes.length);

		return b;
	}

	public static MessageType getMessageType(byte opCode) {
		for (MessageType m : MessageType.values())
			if (m.getOpCode() == opCode)
				return m;
		return null;
	}

	public static Stage openWindow(String resourceLocation, String title, Stage parent, Theme theme) {
		Stage primaryStage = new Stage();
		Image i;

		if (new File("./resources/assets/textures/logo/logo.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/logo.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/logo.png");
		Parent root = null;

		try {
			root = FXMLLoader.load(Util.class.getResource(resourceLocation), Ref.language);
		} catch (IOException e) {
			Ref.LOGGER.log(Level.SEVERE, "Start new Window fail", e);
			return null;
		}
		Scene s = new Scene(root);
		if (!theme.getLocation().equalsIgnoreCase("remove")) {
			s.getStylesheets().add(theme.getLocation());
		}

		primaryStage.setMinWidth(200);
		primaryStage.setMinHeight(158);
		primaryStage.setTitle(title);
		primaryStage.setScene(s);
		primaryStage.initModality(Modality.WINDOW_MODAL);
		if (parent != null)
			primaryStage.initOwner(parent);
		primaryStage.initStyle(StageStyle.DECORATED);

		primaryStage.getIcons().add(i);

		primaryStage.show();

		return primaryStage;
	}

	public static Stage updateWindow(Stage stage, String resourceLocation, Theme theme) {
		Parent root;

		try {
			root = FXMLLoader.load(Util.class.getResource(resourceLocation), Ref.language);
		} catch (IOException e) {
			Ref.LOGGER.log(Level.SEVERE, "Start new Window fail", e);
			return stage;
		}

		Scene s = new Scene(root);
		if (!theme.getLocation().equalsIgnoreCase("remove")) {
			s.getStylesheets().add(theme.getLocation());
		}

		stage.setScene(s);

		stage.show();
		return stage;
	}

	public static void onClose(ActionEvent e) {
		closeConfig();

		Ref.LOGGER.info("Close " + Ref.PROJECT_NAME);

		System.exit(0);
	}

	private static void closeConfig() {
		ConfigManager.getInstance().onConfigChanged();

		Ref.LOGGER.fine("Start saving config");

		if (!Files.exists(FileSystems.getDefault().getPath(Ref.HOME_FOLDER), LinkOption.NOFOLLOW_LINKS))
			new File(Ref.HOME_FOLDER).mkdir();

		try {
			ConfigManager.getInstance().save(new File(Ref.HOME_FOLDER + "config.cfg"));
		} catch (IOException e) {
			Ref.LOGGER.log(Level.SEVERE, "Error due to write config data!", e);
		}

		Ref.LOGGER.fine("Finished saving config");

	}


}
