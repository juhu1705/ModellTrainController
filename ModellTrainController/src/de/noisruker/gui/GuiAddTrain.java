package de.noisruker.gui;

import de.noisruker.gui.tables.BasicTrains;
import de.noisruker.loconet.LocoNet;
import de.noisruker.loconet.LocoNetConnection;
import de.noisruker.loconet.messages.LocoNetMessage;
import de.noisruker.loconet.messages.MessageType;
import de.noisruker.railroad.Train;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jssc.SerialPortException;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiAddTrain implements Initializable {

    @FXML
    public TableView<Train> trains;

    @FXML
    public TableColumn<Train, String> address, slot;

    @FXML
    public Spinner<Integer> newAddress;

    @FXML
    public Label messages;

    public void onAdd(ActionEvent event) {
        if(newAddress.getValue() == null)
            return;
        for(Train t: LocoNet.getInstance().getTrains())
            if(t.getAddress() == newAddress.getValue())
                return;

        Util.runNext(() -> {
            try {
                new LocoNetMessage(MessageType.OPC_LOCO_ADR, (byte) 0, newAddress.getValue().byteValue()).send();
            } catch (SerialPortException | LocoNetConnection.PortNotOpenException e) {
                Ref.LOGGER.info("Could not add train, please try again later");
                messages.setText("Could not add Train, please check your connection and try again!");
            }
        });
    }

    public void onClose(ActionEvent event) {
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        BasicTrains.getInstance().addTable(this.trains);

        this.address.setCellValueFactory(t -> new SimpleStringProperty(String.valueOf(t.getValue().getAddress())));
        this.slot.setCellValueFactory(t -> new SimpleStringProperty(String.valueOf(t.getValue().getSlot())));

        this.newAddress.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 255, 1));
    }

}
