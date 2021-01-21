package de.noisruker.railroad.conditions;

import de.noisruker.railroad.TrainStationManager;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;

public class SensorCondition extends AbstractDrivingCondition {

    private final boolean sensorState;
    private Sensor sensor = null;
    HBox condition = new HBox();

    public SensorCondition(boolean sensorState) {
        this.sensorState = sensorState;
    }

    @Override
    public boolean isConditionTrue() {
        return sensor == null || this.sensorState == sensor.getState();
    }

    @Override
    public void updateCondition() {
        Platform.runLater(() -> {
            if (super.isInCheck) {
                if (!this.isConditionTrue() && !condition.getStyleClass().contains("in-progress"))
                    condition.getStyleClass().add("in-progress");
                else if (this.isConditionTrue() && !condition.getStyleClass().contains("checked"))
                    condition.getStyleClass().add("checked");
                if(!this.isConditionTrue())
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
        for(Sensor s: Sensor.getAllSensors()) {
            sensors.add(s.toString());
        }
        if(!sensors.isEmpty()) {
            comboBox.setItems(FXCollections.observableArrayList(sensors));
        }
        comboBox.setOnAction(event -> {
            if(comboBox.getValue() != null) {
                this.sensor = Util.getSensorByString(comboBox.getValue(), Sensor.getAllSensors());
                if(this.sensor != null)
                    sensor.setText(this.sensor.toString());
            }
        });

        VBox popOverBox = new VBox(comboBox);
        popOverBox.setSpacing(20);
        popOverBox.setPadding(new Insets(10));
        PopOver popOver = new PopOver(popOverBox);

        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

        sensor.setOnAction(event -> popOver.show(sensor));
        sensor.setMinWidth(100);

        Label passed = new Label(Ref.language.getString(this.sensorState ? "label.occupied" : "label.free"));

        HBox.setHgrow(passed, Priority.ALWAYS);

        Button delete = new Button();
        delete.setOnAction(event -> {
            station.deleteCondition(this);
        });
        delete.setGraphic(new FontIcon("fas-trash"));

        passed.setMaxWidth(1.7976931348623157E308);
        passed.setMaxHeight(1.7976931348623157E308);
        condition.setMaxHeight(1.7976931348623157E308);
        condition.setMaxWidth(1.7976931348623157E308);

        condition.getChildren().addAll(sensor, passed, delete);
        condition.getStyleClass().add("plan-area");

        box.getChildren().add(condition);
    }
}
