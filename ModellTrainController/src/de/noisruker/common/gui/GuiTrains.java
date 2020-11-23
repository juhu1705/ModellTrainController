package de.noisruker.common.gui;

import de.noisruker.common.Train;
import de.noisruker.server.loconet.LocoNet;
import de.noisruker.util.Ref;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class GuiTrains implements Initializable {

    @FXML
    public TableView<Train> trains;

    @FXML
    public TableColumn<Train, String> address, slot;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        address.setCellValueFactory(train ->
            new SimpleStringProperty(String.valueOf(train.getValue().getAddress())));

        slot.setCellValueFactory(train ->
                new SimpleStringProperty(String.valueOf(train.getValue().getSlot())));

        Ref.LOGGER.info("Trains: " + LocoNet.getInstance().getTrains().toString());

        this.trains.setItems(FXCollections.observableArrayList(LocoNet.getInstance().getTrains()));
    }
}
