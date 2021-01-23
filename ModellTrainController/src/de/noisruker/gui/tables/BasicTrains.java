package de.noisruker.gui.tables;

import de.noisruker.railroad.Train;
import de.noisruker.util.Ref;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;

import java.util.ArrayList;

public class BasicTrains {

    private static BasicTrains instance = null;

    public static BasicTrains getInstance() {
        return instance == null ? (instance = new BasicTrains()) : instance;
    }

    protected BasicTrains() {
    }

    private ArrayList<TableView<Train>> trains = new ArrayList<>();
    private ArrayList<TableInputHandler> handlers = new ArrayList<>();

    public void addTable(TableView<Train> trains) {
        this.trains.add(trains);
    }

    public void addTableWithHandler(TableInputHandler handler) {
        this.handlers.add(handler);
    }

    public void setTrains(final ArrayList<Train> allTrains) {
        Platform.runLater(() -> {
            Ref.LOGGER.info(allTrains.toString());

            for (TableView<Train> t : this.trains) {
                t.getItems().clear();
                t.refresh();
                t.setItems(FXCollections.observableArrayList(allTrains));
                t.sort();
            }
            handlers.forEach(handler -> handler.onHandleTrains(allTrains));
        });
    }

    public void removeTable(TableView<Train> trains) {
        this.trains.remove(trains);
    }

    public interface TableInputHandler {
        public void onHandleTrains(final ArrayList<Train> allTrains);
    }
}
