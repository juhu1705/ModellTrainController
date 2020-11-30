package de.noisruker.util;

import de.noisruker.config.ConfigManager;
import de.noisruker.loconet.messages.MessageType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
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

	public static Stage openWindow(String resourceLocation, String title, Stage parent) {
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
		if (!Ref.theme.getLocation().equalsIgnoreCase("remove")) {
			if(Ref.theme.equals(Theme.LIGHT) || Ref.theme.equals(Theme.DARK)) {
				JMetro theme = new JMetro(s, Theme.DARK == Ref.theme ? Style.DARK : Style.LIGHT);
				Ref.other_page_themes.add(theme);
				primaryStage.setOnCloseRequest(event -> Ref.other_page_themes.remove(theme));
			} else
				s.getStylesheets().add(Ref.theme.getLocation());
		}
		s.getStylesheets().add(Ref.THEME_IMPROVEMENTS);

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

	public static Stage updateWindow(Stage stage, String resourceLocation) {
		Parent root;

		try {
			root = FXMLLoader.load(Util.class.getResource(resourceLocation), Ref.language);
		} catch (IOException e) {
			Ref.LOGGER.log(Level.SEVERE, "Start new Window fail", e);
			return stage;
		}

		Scene s = new Scene(root);
		if (!Ref.theme.getLocation().equalsIgnoreCase("remove")) {
			if(Ref.theme.equals(Theme.LIGHT) || Ref.theme.equals(Theme.DARK)) {
				Ref.J_METRO.setScene(s);
			} else
				s.getStylesheets().add(Ref.theme.getLocation());
		}
		s.getStylesheets().add(Ref.THEME_IMPROVEMENTS);
		stage.setScene(s);

		stage.show();
		return stage;
	}

	private static final ArrayList<Runnable> runnables = new ArrayList<>();
	private static boolean isRunning = false;

	public static void runNext(Runnable runnable) {
		runnables.add(runnable);
		if(!isRunning)
			threadRunner();
	}

	private static void threadRunner() {
		if(isRunning)
			return;

		new Thread(() -> {
			isRunning = true;
			while (!runnables.isEmpty()) {
				Runnable r = runnables.remove(0);
				r.run();
			}
			isRunning = false;
		}).start();

	}



	public static void onClose(ActionEvent e) {
		closeConfig();

		Ref.LOGGER.info("Close " + Ref.PROJECT_NAME);

		System.exit(0);
	}

	private static void closeConfig() {
		ConfigManager.getInstance().onConfigChangedGeneral();

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
