package de.noisruker.client.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

import de.noisruker.client.Client;
import de.noisruker.common.ChatMessage;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketType;
import de.noisruker.util.Ref;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class GUIClient implements Initializable {

	private static GUIClient instance;

	public static GUIClient getInstance() {
		return instance;
	}

	@FXML
	public TextArea messages;

	@FXML
	public TextField send;

	public void onSend(ActionEvent event) {
		try {
			Client.getConnectionHandler().sendDatapacket(
					new Datapacket(DatapacketType.CLIENT_SEND_CHAT_MESSAGE, new ChatMessage(this.send.getText())));
		} catch (IOException e) {
			Ref.LOGGER.log(Level.SEVERE, "", e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
	}

}
