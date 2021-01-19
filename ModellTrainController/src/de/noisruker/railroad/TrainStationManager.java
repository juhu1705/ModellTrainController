package de.noisruker.railroad;

import com.sun.javafx.charts.Legend;
import de.noisruker.gui.GuiMain;
import de.noisruker.railroad.conditions.AbstractDrivingConditions;
import de.noisruker.railroad.conditions.TimeCondition;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.xml.namespace.QName;
import java.util.ArrayList;

public class TrainStationManager {

    private final Train train;

    ArrayList<TrainStation> stations = new ArrayList<>();

    private int actual = -1;

    public TrainStationManager(Train train) {
        this.train = train;
    }

    protected void update() {
        if(this.actual != -1) {
            if(this.stations.get(actual).update()) {
                this.setNextStation();
            }
        }
        if(this.actual == -1 && !stations.isEmpty()) {
            this.actual = 0;
            train.setDestination(this.stations.get(actual).sensor);
            Platform.runLater(() -> this.stations.get(actual).button.setSelected(true));
        }
    }

    public void setNextStation() {
        if(this.stations.get(actual).shouldBeDeleted()) {
            stations.remove(actual);
            if(GuiMain.getInstance() != null)
                Platform.runLater(GuiMain.getInstance()::updateTrainStationManager);
        } else
            actual++;

        if(this.stations.size() <= actual) {
            if(!this.stations.isEmpty())
                actual = 0;
            else {
                actual = -1;
                return;
            }
        }

        train.setDestination(this.stations.get(actual).sensor);
        this.stations.get(actual).init();
    }

    public void activateStation(TrainStation station) {
        if(this.stations.get(actual).shouldBeDeleted()) {
            stations.remove(actual);
            if (GuiMain.getInstance() != null)
                Platform.runLater(GuiMain.getInstance()::updateTrainStationManager);
        }
        if(!this.stations.contains(station)) {
            this.stations.add(station);
            if(GuiMain.getInstance() != null)
                Platform.runLater(GuiMain.getInstance()::updateTrainStationManager);
        }
        this.actual = this.stations.indexOf(station);
        train.stopTrainImmediately();
        train.resetRailway();
        train.setDestination(this.stations.get(actual).sensor);
        this.stations.get(actual).init();
    }

    public void addStation(Sensor sensor, boolean isTemporary) {
        TrainStation station;
        this.stations.add(station = new TrainStation(sensor, isTemporary, this));

        if(GuiMain.getInstance() != null)
            Platform.runLater(GuiMain.getInstance()::updateTrainStationManager);
    }

    public void addStationsToGUI(VBox box) {
        ToggleGroup group = new ToggleGroup();

        this.stations.forEach(s -> s.addStationToGUI(box, group));
    }

    public class TrainStation {

        ToggleButton button = new ToggleButton();
        TrainStationManager myManager;

        private final Sensor sensor;
        private final boolean isTemporary;

        public TrainStation(Sensor sensor, boolean isTemporary , TrainStationManager myManager) {
            this.sensor = sensor;
            this.isTemporary = isTemporary;
            this.myManager = myManager;

            this.addCondition(new TimeCondition(10));
            button.setGraphic(new FontIcon("fas-caret-right"));
            button.setOnAction(action -> {
                button.setSelected(true);
                Util.runNext(() -> this.myManager.activateStation(this));
            });
        }

        private ArrayList<AbstractDrivingConditions> conditions = new ArrayList<>();
        private ArrayList<DrivingConditionMatchmaker> matcher = new ArrayList<>();

        private boolean update() {
            if(!this.conditions.isEmpty()) {
                conditions.get(0).setChecking(true);
                conditions.get(0).updateCondition();
                boolean toReturn = conditions.get(0).isConditionTrue();
                if(this.conditions.size() > 1)
                    for(int i = 1; i < conditions.size(); i++) {
                        AbstractDrivingConditions dc = this.conditions.get(i);
                        if(matcher.get(i - 1) != DrivingConditionMatchmaker.THEN || toReturn) {
                            dc.setChecking(true);
                            dc.updateCondition();
                        }
                        switch (matcher.get(i - 1)) {
                            case OR -> toReturn = toReturn || dc.isConditionTrue();
                            case AND -> toReturn = toReturn && dc.isConditionTrue();
                            case THEN -> {
                                if(!toReturn) {
                                    return false;
                                }
                                toReturn = dc.isConditionTrue();
                            }
                        }
                    }
                return toReturn;
            }
            return true;
        }

        public void init() {
            Platform.runLater(() -> this.button.setSelected(true));
            this.conditions.forEach(AbstractDrivingConditions::start);
        }

        public boolean shouldBeDeleted() {
            this.conditions.forEach(abstractDrivingConditions -> {
                abstractDrivingConditions.setChecking(false);
                Platform.runLater(abstractDrivingConditions::updateCondition);
            });

            return this.isTemporary;
        }

        public void addStationToGUI(VBox box, ToggleGroup group) {
            button.setToggleGroup(group);
            Label sensorName = new Label(sensor.toString());

            HBox station = new HBox(button, sensorName);
            station.getStyleClass().add("plan-area");
            if(this.isTemporary)
                station.getStyleClass().add("temporary");
            station.setPadding(new Insets(5, 10, 5, 10));
            station.setSpacing(20);

            VBox v1 = new VBox(), v2 = new VBox();
            v2.setFillWidth(true);
            v2.setSpacing(10);
            v2.setMaxWidth(1.7976931348623157E308);
            v1.setSpacing(10);
            v1.setPadding(new Insets(30, 0, 0, 0));
            v1.setMinWidth(100);

            for(int i = 0; i < this.matcher.size(); i++) {
                DrivingConditionMatchmaker conditionMatchmaker = this.matcher.get(i);
                Button button = new Button(Ref.language.getString("button." + conditionMatchmaker.name()));
                button.setPadding(new Insets(10));
                button.setMaxWidth(1.7976931348623157E308);
                int finalI = i;
                button.setOnAction(event -> {
                    if(this.matcher.size() > finalI) {
                        Ref.LOGGER.info(button.getText() + " " + Ref.language.getString("button.OR"));
                        if (button.getText().equals(Ref.language.getString("button.OR"))) {
                            this.matcher.remove(finalI);
                            this.matcher.add(finalI, DrivingConditionMatchmaker.AND);
                            Platform.runLater(() -> button.setText(Ref.language.getString("button." + this.matcher.get(finalI).name())));
                        }
                        if (button.getText().equals(Ref.language.getString("button.AND"))) {
                            this.matcher.remove(finalI);
                            this.matcher.add(finalI, DrivingConditionMatchmaker.THEN);
                            Platform.runLater(() -> button.setText(Ref.language.getString("button." + this.matcher.get(finalI).name())));
                        }
                        if (button.getText().equals(Ref.language.getString("button.THEN"))) {
                            this.matcher.remove(finalI);
                            this.matcher.add(finalI, DrivingConditionMatchmaker.OR);
                            Platform.runLater(() -> button.setText(Ref.language.getString("button." + this.matcher.get(finalI).name())));
                        }
                    }
                });
                button.getStyleClass().add("plan-area");
                v1.getChildren().add(button);
            }

            this.conditions.forEach(abstractDrivingConditions -> abstractDrivingConditions.addToGui(v2));

            Button addCondition = new Button(Ref.language.getString("button.add_condition"));

            addCondition.getStyleClass().add("plan-area");
            addCondition.setMaxWidth(1.7976931348623157E308);

            Button addTimeCondition = new Button(Ref.language.getString("button.time_condition"));

            addTimeCondition.setOnAction(event -> {
                this.addCondition(new TimeCondition(5));
            });

            VBox popOverBox = new VBox(addTimeCondition);
            popOverBox.setSpacing(20);
            popOverBox.setPadding(new Insets(10));

            PopOver addConditionPopOver = new PopOver(popOverBox);

            addConditionPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

            addCondition.setOnAction(event -> addConditionPopOver.show(addCondition));

            v2.getChildren().add(addCondition);

            HBox hBox = new HBox(v1, v2);
            hBox.setSpacing(10);
            hBox.setMaxWidth(1.7976931348623157E308);

            box.getChildren().addAll(station, hBox);
        }

        private void addCondition(AbstractDrivingConditions condition) {
            if(!this.conditions.isEmpty())
                this.matcher.add(DrivingConditionMatchmaker.AND);
            this.conditions.add(condition);

            if(GuiMain.getInstance() != null)
                GuiMain.getInstance().updateTrainStationManager();
        }
    }

    public enum DrivingConditionMatchmaker {
        OR,
        AND,
        THEN;
    }

}