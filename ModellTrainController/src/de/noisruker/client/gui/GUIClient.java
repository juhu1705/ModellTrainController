package de.noisruker.client.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

import de.noisruker.client.Client;
import de.noisruker.common.messages.ChatMessage;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketType;
import de.noisruker.util.Ref;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GUIClient implements Initializable {

	private static GUIClient instance;

	public static GUIClient getInstance() {
		return instance;
	}

	@FXML
	public SplitPane setTo;

	@FXML
	public TextArea messages;

	@FXML
	public ImageView stream;

	@FXML
	public TextField send;

	public void onSend(ActionEvent event) {
		try {
			Ref.LOGGER.info("Send chat message: " + this.send.getText());
			Client.getConnectionHandler().sendDatapacket(
					new Datapacket(DatapacketType.CLIENT_SEND_CHAT_MESSAGE, new ChatMessage(this.send.getText())));
		} catch (IOException e) {
			Ref.LOGGER.log(Level.SEVERE, "", e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;

		new Thread(() -> {
			String imageSource = "http://192.168.0.172:8080/";

			while (true) {

				Image img;

				Ref.LOGGER.info((img = new Image(imageSource, true)).toString());

				Ref.LOGGER.info(img.getWidth() + "");
				Ref.LOGGER.info(img.isError() + "");
				Platform.runLater(() -> {
					stream.setImage(img);
				});

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
