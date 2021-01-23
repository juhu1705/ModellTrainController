package de.noisruker.railroad.conditions;

import de.noisruker.loconet.LocoNet;
import de.noisruker.railroad.Train;
import de.noisruker.railroad.TrainStationManager;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;

public class TrainOnSensorCondition extends AbstractDrivingCondition {

    Train train = null;
    Sensor sensor = null;
    HBox condition = new HBox();

    @Override
    public boolean isConditionTrue() {
        return train == null || sensor == null || train.getActualPosition() == sensor;
    }

    @Override
    public void updateCondition() {
        Platform.runLater(() -> {
            if (super.isInCheck) {
                if (!this.isConditionTrue() && !condition.getStyleClass().contains("in-progress"))
                    condition.getStyleClass().add("in-progress");
                else if (this.isConditionTrue() && !condition.getStyleClass().contains("checked"))
                    condition.getStyleClass().add("checked");
                if (!this.isConditionTrue())
                    condition.getStyleClass().remove("checked");
            } else {
                condition.getStyleClass().remove("in-progress");
                condition.getStyleClass().remove("checked");
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void addToGui(VBox box, TrainStationManager.TrainStation station) {
        this.condition.getChildren().clear();
        this.condition.setPadding(new Insets(10));
        this.condition.setSpacing(20);
        Button sensor = new Button(this.sensor == null ? "" : this.sensor.toString());

        ComboBox<String> comboBox = new ComboBox<>();

        ArrayList<String> sensors = new ArrayList<>();
        for (Sensor s : Sensor.getAllSensors()) {
            sensors.add(s.toString());
        }
        if (!sensors.isEmpty()) {
            comboBox.setItems(FXCollections.observableArrayList(sensors));
        }
        comboBox.setOnAction(event -> {
            if (comboBox.getValue() != null) {
                this.sensor = Util.getSensorByString(comboBox.getValue(), Sensor.getAllSensors());
                sensor.setText(this.sensor == null ? "" : this.sensor.toString());
            }
        });

        VBox popOverBox = new VBox(comboBox);
        popOverBox.setSpacing(20);
        popOverBox.setPadding(new Insets(10));
        PopOver popOver = new PopOver(popOverBox);

        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

        sensor.setOnAction(event -> popOver.show(sensor));
        sensor.setMinWidth(100);

        Button train = new Button(this.train == null ? "" : this.train.getName());

        ComboBox<String> comboBoxTrains = new ComboBox<>();

        ArrayList<String> trains = new ArrayList<>();
        for (Train s : LocoNet.getInstance().getTrains()) {
            trains.add(s.getName());
        }
        if (!trains.isEmpty()) {
            comboBoxTrains.setItems(FXCollections.observableArrayList(trains));
        }
        comboBoxTrains.setOnAction(event -> {
            if (comboBoxTrains.getValue() != null) {
                this.train = Util.getTrainByString(comboBoxTrains.getValue(), LocoNet.getInstance().getTrains());
                train.setText(this.train == null ? "" : this.train.getName());
            }
        });

        VBox popOverBox2 = new VBox(comboBoxTrains);
        popOverBox2.setSpacing(20);
        popOverBox2.setPadding(new Insets(10));
        PopOver popOver2 = new PopOver(popOverBox2);

        popOver2.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

        train.setOnAction(event -> popOver2.show(train));
        train.setMinWidth(100);

        Label passed = new Label(Ref.language.getString("label.on"));

        HBox.setHgrow(passed, Priority.ALWAYS);

        Button delete = new Button();
        delete.setOnAction(event -> {
            station.deleteCondition(this);
        });
        delete.setGraphic(new FontIcon("fas-trash"));

        passed.setMaxWidth(1.7976931348623157E308);
        passed.setMaxHeight(1.7976931348623157E308);
        passed.setAlignment(Pos.CENTER);
        condition.setMaxHeight(1.7976931348623157E308);
        condition.setMaxWidth(1.7976931348623157E308);
        condition.setFillHeight(true);

        condition.getChildren().addAll(train, passed, sensor, delete);
        condition.getStyleClass().add("plan-area");

        box.getChildren().add(condition);
    }
}
