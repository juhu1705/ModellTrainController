package de.noisruker.gui.tables;

import de.noisruker.loconet.LocoNet;
import de.noisruker.railroad.Train;
import de.noisruker.util.Ref;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;

public class BasicTrains {

    private static BasicTrains instance = null;

    public static BasicTrains getInstance() {
        return instance == null ? (instance = new BasicTrains()) : instance;
    }

    protected BasicTrains() {}

    private ArrayList<TableView<Train>> trains = new ArrayList<>();

    public void addTable(TableView<Train> trains) {
        this.trains.add(trains);
    }

    public void setTrains(final ArrayList<Train> trains) {
        Platform.runLater(() -> {
            for(TableView<Train> t: this.trains) {
                t.getItems().clear();
                t.setItems(FXCollections.observableArrayList(trains));
                t.sort();
            }
        });
    }

    public void removeTable(TableView<Train> trains) {
        this.trains.remove(trains);
    }
}
