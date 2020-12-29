package de.noisruker.gui;

import de.noisruker.gui.tables.BasicTrains;
import de.noisruker.loconet.LocoNet;
import de.noisruker.loconet.LocoNetConnection;
import de.noisruker.loconet.messages.LocoNetMessage;
import de.noisruker.loconet.messages.MessageType;
import de.noisruker.main.GUILoader;
import de.noisruker.railroad.Train;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jssc.SerialPortException;
import org.controlsfx.glyphfont.FontAwesome;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class GuiAddTrain implements Initializable {

    public static GuiAddTrain actual = null;

    private ArrayList<Train> old;

    @FXML
    public TableView<Train> trains;

    @FXML
    public TableColumn<Train, String> address, slot;

    @FXML
    public Spinner<Integer> newAddress;

    @FXML
    public Label messages;

    @FXML
    public ProgressBar progress;

    public void onAdd(ActionEvent event) {
        if(newAddress.getValue() == null)
            return;
        for(Train t: LocoNet.getInstance().getTrains())
            if(t.getAddress() == newAddress.getValue())
                return;

        messages.setText(Ref.language.getString("label.waiting_for_response"));
        progress.setProgress(-1d);

        Util.runNext(() -> {
            Train.addTrain(newAddress.getValue().byteValue());
        });
    }

    public void onAdded(SortEvent e) {
        progress.setProgress(0);
        messages.setText(Ref.language.getString("label.waiting_for_input"));
    }

    public void close(WindowEvent event) {
        BasicTrains.getInstance().removeTable(this.trains);
        Util.runNext(() -> {
            for(Train t: LocoNet.getInstance().getTrains()) {
                GuiEditTrain.train = t;

                Platform.runLater(() -> Objects.requireNonNull(
                        Util.openWindow("/assets/layouts/edit_train.fxml",
                                Ref.language.getString("window.edit_train"),
                                GUILoader.getPrimaryStage()))
                        .setResizable(true));

                while (GuiEditTrain.train != null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignore) { }
                }
            }
            BasicTrains.getInstance().setTrains(LocoNet.getInstance().getTrains());
        });
        actual = null;
    }

    public void onClose(ActionEvent event) {
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
        this.close(null);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        old = LocoNet.getInstance().getTrains();

        BasicTrains.getInstance().addTableWithHandler(this::onHandleNewTrains);

        this.address.setCellValueFactory(t -> new SimpleStringProperty(String.valueOf(t.getValue().getAddress())));
        this.slot.setCellValueFactory(t -> new SimpleStringProperty(String.valueOf(t.getValue().getSlot())));

        this.newAddress.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 255, 1));
        actual = this;
    }

    private void onHandleNewTrains(final ArrayList<Train> trains) {
        ArrayList<Train> toAdd = new ArrayList<>();
        for(Train t: trains) {
            if(!old.contains(t))
                toAdd.add(t);
        }
        this.trains.getItems().clear();
        this.trains.refresh();
        this.trains.setItems(FXCollections.observableArrayList(toAdd));
        this.trains.sort();
    }

}
