package de.noisruker.server.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

import de.noisruker.common.ChatMessage;
import de.noisruker.net.datapackets.Datapacket;
import de.noisruker.net.datapackets.DatapacketType;
import de.noisruker.server.ClientHandler;
import de.noisruker.server.ClientHandler.PermissionLevel;
import de.noisruker.server.Server;
import de.noisruker.util.Ref;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;

public class GUIServer implements Initializable {

	private static GUIServer instance;

	public static GUIServer getInstance() {
		return instance;
	}

	@FXML
	public TextField send;

	@FXML
	public TextArea messages;

	@FXML
	public TableView<ClientHandler> players;

	@FXML
	public TableColumn<ClientHandler, String> name;

	@FXML
	public TableColumn<ClientHandler, PermissionLevel> permissionlevel;

	public void onSend(ActionEvent ae) {
		ChatMessage m = new ChatMessage(send.getText());
		m.setName("SERVER");
		m.setLevel("HOST");

		GUIServer.getInstance().messages.appendText(m.getFormatted());

		for (ClientHandler ch : Server.getClientHandlers())
			try {
				ch.sendDatapacket(new Datapacket(DatapacketType.SERVER_SEND_CHAT_MESSAGE, m));
			} catch (IOException e) {
				Ref.LOGGER.log(Level.SEVERE, "", e);
			}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
//		Thread modellRailroadController = new Thread(ModellRailroad.getInstance());
//		modellRailroadController.start();

		GUIServer.instance = this;

		ObservableList<PermissionLevel> data = FXCollections.observableArrayList();

		for (PermissionLevel level : PermissionLevel.values())
			data.add(level);

		ObservableList<Byte> bytes = FXCollections.observableArrayList();

		for (byte b = 0; b <= 126; b++)
			bytes.add(b);

		this.name.setCellValueFactory(ch -> {
			return new SimpleStringProperty(ch.getValue().getName());
		});

		this.permissionlevel.setCellValueFactory(ch -> {
			return new SimpleObjectProperty<ClientHandler.PermissionLevel>(ch.getValue().getPermissionLevel());
		});

		this.permissionlevel.setCellFactory(ComboBoxTableCell.forTableColumn(data));

		this.players.setEditable(true);

		this.permissionlevel.setOnEditCommit(event -> {
			TablePosition<ClientHandler, PermissionLevel> pos = event.getTablePosition();

			PermissionLevel newLevel = event.getNewValue();

			int row = pos.getRow();
			ClientHandler handler = event.getTableView().getItems().get(row);

			handler.setPermissionLevel(newLevel);
		});

		new Thread(() -> {
			try {
				Server.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();

	}

}
