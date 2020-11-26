package de.noisruker.gui.tables;

import de.noisruker.loconet.LocoNet;
import de.noisruker.railroad.Train;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;

public class BasicTrains {

    private static final BasicTrains instance = null;

    public static BasicTrains getInstance() {
        return instance == null ? new BasicTrains() : instance;
    }

    protected BasicTrains() {}

    public TableView<Train> trains = null;

    public void writeTable(TableView<Train> trains) {
        this.trains = trains;
    }

    public void setTrains(ArrayList<Train> trains) {
        if(this.trains != null) {
            this.trains.getItems().clear();
            this.trains.setItems(FXCollections.observableArrayList(trains));
            this.trains.sort();
        }
    }

}
