package de.noisruker.server.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import de.noisruker.common.Train;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GUIServer implements Initializable {

	private static GUIServer instance;

	public static GUIServer getInstance() {
		return instance;
	}

	@FXML
	public TextField send;

	@FXML
	public TableView<ClientHandler> players;

	@FXML
	public TableColumn<ClientHandler, String> name;

	@FXML
	public TableColumn<ClientHandler, PermissionLevel> permissionlevel;

	@FXML
	public TableView<Train> trains;

	@FXML
	public TableColumn<Train, Byte> slot, speed;

	public void addTrain(ActionEvent event) {

		Stage primaryStage = new Stage();

		Image i;

		if (new File("./resources/assets/textures/logo/logo.png").exists())
			i = new Image(new File("./resources/assets/textures/logo/logo.png").toURI().toString());
		else
			i = new Image("/assets/textures/logo/logo.png");
		Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("/assets/layouts/GUIAddTrain.fxml"), Ref.language);
		} catch (IOException e) {
			return;
		}
		Scene s = new Scene(root);

		s.getStylesheets().add("/assets/styles/dark_theme.css");

		primaryStage.setMinWidth(200);
		primaryStage.setMinHeight(158);
		primaryStage.setTitle("ERROR");
		primaryStage.setScene(s);
		primaryStage.initModality(Modality.WINDOW_MODAL);
		primaryStage.initStyle(StageStyle.DECORATED);

		primaryStage.getIcons().add(i);

		primaryStage.show();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
//		Thread modellRailroadController = new Thread(ModellRailroad.getInstance());
//		modellRailroadController.start();

		this.instance = this;

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

		this.speed.setCellValueFactory(ch -> {
			return new SimpleObjectProperty<Byte>(ch.getValue().getActualSpeed());
		});

		this.slot.setCellValueFactory(ch -> new SimpleObjectProperty<Byte>(ch.getValue().getSlot()));

		this.speed.setCellFactory(ComboBoxTableCell.forTableColumn(bytes));

		this.trains.setEditable(true);

		this.speed.setOnEditCommit(event -> {
			TablePosition<Train, Byte> pos = event.getTablePosition();

			Byte newLevel = event.getNewValue();

			int row = pos.getRow();
			Train train = event.getTableView().getItems().get(row);

			train.setActualSpeed(newLevel);
		});

	}

}
