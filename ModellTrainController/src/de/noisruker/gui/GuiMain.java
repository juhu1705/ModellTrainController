package de.noisruker.gui;

import de.noisruker.config.ConfigManager;
import de.noisruker.loconet.LocoNet;
import de.noisruker.loconet.messages.RailroadOffMessage;
import de.noisruker.loconet.messages.RailroadOnMessage;
import de.noisruker.main.GUILoader;
import de.noisruker.railroad.Position;
import de.noisruker.railroad.Train;
import de.noisruker.railroad.elements.AbstractRailroadElement;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.railroad.elements.Signal;
import de.noisruker.railroad.elements.Switch;
import de.noisruker.util.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.ToggleSwitch;
import org.kordamp.ikonli.javafx.FontIcon;
import org.xml.sax.SAXException;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class GuiMain implements Initializable {

    private static GuiMain instance = null;

    public static GuiMain getInstance() {
        return instance;
    }

    public int start;

    @FXML
    public TreeView<String> tree, trains, sensors, switches;

    @FXML
    public VBox railroadSection, trainStations;

    public VBox allSensors = new VBox();

    public ArrayList<HBox> railroadLines = new ArrayList<>();
    public HashMap<HBox, ArrayList<ImageView>> railroadCells = new HashMap<>();

    public TreeItem<String> trainsRoot, switchesRoot, sensorsRoot;

    private final ToggleGroup group = new ToggleGroup();
    private ToggleButton up, down, right, left;

    @FXML
    public Menu theme, language;

    @FXML
    public VBox config, controls, manualControls, automaticControls;

    @FXML
    public ComboBox<String> mode;

    public ComboBox<String> actualPosition;

    @FXML
    public Button actualSensor, addStation;

    public Train actual;
    private Sensor sensor;

    @FXML
    public Label trainName, trainName1, sensorLabel;

    @FXML
    public ToggleSwitch sensorListed;
    public ToggleSwitch temporary = new ToggleSwitch();

    @FXML
    public TextField sensorName;

    public void checkOutRailroad() {
        Platform.runLater(this::updateRailroad);
    }

    private void updateRailroad() {
        if (LocoNet.getRailroad().getRailroad() == null)
            return;

        final AbstractRailroadElement[][] railroadElements = LocoNet.getRailroad().getRailroad();

        for (int y = 0; y < 100; y++) {
            HBox box = this.railroadLines.get(y);
            for (int x = 0; x < 100; x++) {
                if (railroadElements[x][y] != null && railroadElements[x][y].getImage() != null) {
                    this.railroadCells.get(box).get(x).setImage(railroadElements[x][y].getImage());
                    if (railroadElements[x][y] instanceof Switch) {
                        int finalX = x;
                        int finalY = y;
                        this.railroadCells.get(box).get(x).setOnMouseClicked(event -> {
                            Switch s = (Switch) railroadElements[finalX][finalY];
                            s.changeDirection();
                            this.railroadCells.get(box).get(finalX).setImage(railroadElements[finalX][finalY].getImage());
                        });
                    } else if (railroadElements[x][y] instanceof Signal) {
                        int finalX = x;
                        int finalY = y;
                        this.railroadCells.get(box).get(x).setOnMouseClicked(event -> {
                            Signal s = (Signal) railroadElements[finalX][finalY];
                            s.changeState();
                            this.railroadCells.get(box).get(finalX).setImage(railroadElements[finalX][finalY].getImage());
                        });
                    }
                } else
                    this.railroadCells.get(box).get(x).setImage(RailroadImages.EMPTY_2);
            }
        }
        this.updateSensors();
        this.updateSwitches();
        this.updateTrains();
    }

    public void onResetAllTrains(ActionEvent event) {
        LocoNet.getInstance().getTrains().forEach(Train::reset);
        this.updateTrainStationManager();
    }

    public void onStopAllTrains(ActionEvent event) {
        LocoNet.getInstance().getTrains().forEach(train -> train.stopTrainImmediately());
    }

    public void onStopRailroad(ActionEvent event) {
        Util.runNext(new RailroadOffMessage()::send);
    }

    public void onStartRailroad(ActionEvent event) {
        Util.runNext(new RailroadOnMessage()::send);
    }

    public void onSensorSelectionChanged(MouseEvent event) {
        if (this.sensors.getSelectionModel().getSelectedItem() == null)
            return;
        String name = this.sensors.getSelectionModel().getSelectedItem().getValue();
        for (Sensor sensor : Sensor.getAllSensors())
            if (name.equals("Sensor: " + sensor.getAddress() + " [" + sensor.getPosition().getX() + "] [" + sensor.getPosition().getY() + "]")) {
                this.sensor = sensor;
                sensorLabel.setText(name);
                sensorListed.setSelected(sensor.shouldBeListed());
                sensorListed.setDisable(false);
                sensorName.setText(sensor.getName());
                sensorName.setPromptText("");
                sensorName.setDisable(false);
            }
    }

    public void onSensorNameChanged(ActionEvent event) {
        if (Util.getSensorByString(this.sensorName.getText(), Sensor.getAllSensors()) == null) {
            this.sensor.setName(this.sensorName.getText());
            this.updatePlanMode();
        } else {
            this.sensorName.setText("");
            this.sensorName.setPromptText(Ref.language.getString("error.duplicated_name"));
        }
    }

    public void onSensorListedChanged() {
        this.sensor.setShouldBeListed(this.sensorListed.isSelected());
        this.updatePlanMode();
    }

    public void onNormalSpeed(ActionEvent event) {
        if (Config.mode.equals(Config.MODE_MANUAL) && actual != null)
            actual.applyNormalSpeed();
    }

    public void onMinSpeed(ActionEvent event) {
        if (Config.mode.equals(Config.MODE_MANUAL) && actual != null)
            actual.applyBreakSpeed();
    }

    public void onMaxSpeed(ActionEvent event) {
        if (Config.mode.equals(Config.MODE_MANUAL) && actual != null)
            actual.applyMaxSpeed();
    }

    public void onStop(ActionEvent event) {
        if (Config.mode.equals(Config.MODE_MANUAL) && actual != null)
            actual.setSpeed((byte) 0);
    }

    public void onStopImmediately(ActionEvent event) {
        if (actual != null)
            Util.runNext(actual::stopTrainImmediately);
    }

    public void onTrainSelectionChanged(MouseEvent event) {
        if (this.trains.getSelectionModel().getSelectedItem() == null)
            return;
        String name = this.trains.getSelectionModel().getSelectedItem().getValue();
        for (Train train : LocoNet.getInstance().getTrains())
            if (train.equals(name)) {
                this.setTrain(train);
            }
    }

    public void setTrain(Train train) {
        this.actual = train;
        trainName.setText(this.actual.getName());
        trainName1.setText(this.actual.getName());
        Util.runNext(() -> train.setDirection(true));
        if (Config.mode.equals(Config.MODE_PLAN)) {
            this.actualPosition.setValue(train.getActualPosition() != null ? train.getActualPosition().toString() : "");
            if (this.actualPosition.getValue().isBlank()) {
                this.addStation.setDisable(true);
                this.up.setDisable(true);
                this.down.setDisable(true);
                this.left.setDisable(true);
                this.right.setDisable(true);
            }
            this.actualPosition.setDisable(false);
            actualSensor.setDisable(false);
            this.actualSensor.setText(train.getActualPosition() != null ?
                    train.getActualPosition().toString() :
                    Ref.language.getString("button.unset"));
        }
        this.updateTrainStationManager();
    }

    public void onCreateRailroad(ActionEvent event) {
        Stage s = Util.openWindow("/assets/layouts/edit_railroad.fxml", Ref.language.getString("window.railroad"), GUILoader.getPrimaryStage());
        if (s != null) {
            s.setResizable(true);
        }
    }

    public void onAddTrains(ActionEvent event) {
        Stage s = Util.openWindow("/assets/layouts/add_train.fxml", Ref.language.getString("window.add_train"), GUILoader.getPrimaryStage());
        if (s != null)
            s.setResizable(true);
    }

    public void onStartTrainControl(ActionEvent event) {
        if (this.trains.getSelectionModel() == null || this.trains.getSelectionModel().getSelectedItem() == null) {
            Notifications.create().darkStyle().title(Ref.language.getString("window.error")).text(Ref.language.getString("error.no_train_selected")).showError();
            return;
        }

        String s = this.trains.getSelectionModel().getSelectedItem().getValue();
        Train t = null;
        for (Train train : LocoNet.getInstance().getTrains())
            if (train.equals(s))
                t = train;
        if (t == null)
            Notifications.create().darkStyle().title(Ref.language.getString("window.error")).text(Ref.language.getString("error.no_train_selected")).showError();
        else {
            final Train finalT = t;
            Util.runNext(() -> {
                while (GuiControlTrain.toAdd != null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored) {
                    }
                }
                GuiControlTrain.toAdd = finalT;
                Platform.runLater(() -> Util.openWindow("/assets/layouts/control_train.fxml", finalT.getName(), null));
            });
        }
    }

    public void onDeleteTrain(ActionEvent event) {
        if (this.trains.getSelectionModel() == null || this.trains.getSelectionModel().getSelectedItem() == null) {
            Notifications.create().darkStyle().title(Ref.language.getString("window.error")).text(Ref.language.getString("error.no_train_selected")).showError();
            return;
        }

        String s = this.trains.getSelectionModel().getSelectedItem().getValue();
        Train t = null;
        for (Train train : LocoNet.getInstance().getTrains())
            if (train.equals(s))
                t = train;
        if (t == null)
            Notifications.create().darkStyle().title(Ref.language.getString("window.error")).text(Ref.language.getString("error.no_train_selected")).showError();
        else {
            LocoNet.getInstance().getTrains().remove(t);
            this.updateTrains();
        }
    }

    public void onEditTrain(ActionEvent event) {
        if (this.trains.getSelectionModel() == null || this.trains.getSelectionModel().getSelectedItem() == null) {
            Notifications.create().darkStyle().title(Ref.language.getString("window.error")).text(Ref.language.getString("error.no_train_selected")).showError();
            return;
        }

        String s = this.trains.getSelectionModel().getSelectedItem().getValue();
        Train t = null;
        for (Train train : LocoNet.getInstance().getTrains())
            if (train.equals(s))
                t = train;
        if (t == null)
            Notifications.create().darkStyle().title(Ref.language.getString("window.error")).text(Ref.language.getString("error.no_train_selected")).showError();
        else {
            Train finalT = t;
            Util.runNext(() -> {
                while (GuiEditTrain.train != null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                }
                GuiEditTrain.train = finalT;
                Platform.runLater(() -> Util.openWindow("/assets/layouts/edit_train.fxml",
                        Ref.language.getString("window.edit_train"), GUILoader.getPrimaryStage()));
            });
        }

    }

    public void onSave(ActionEvent event) {
        if (!Files.exists(FileSystems.getDefault().getPath(Ref.HOME_FOLDER), LinkOption.NOFOLLOW_LINKS))
            new File(Ref.HOME_FOLDER).mkdir();

        try {
            GuiMain.getInstance().save(new File(Ref.HOME_FOLDER + "railroad.mtc"));
        } catch (IOException e) {
            Ref.LOGGER.log(Level.SEVERE, "Error due to write railroad data!", e);
        }
    }

    public void onSaveTo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Save", "*.mtc"));
        fileChooser.setTitle(Ref.language.getString("window.choose_picture"));

        File selected = fileChooser.showSaveDialog(GUILoader.getPrimaryStage());

        if (selected == null)
            return;

        if (!Util.fileEndsWith(selected.getPath(), ".mtc"))
            return;

        try {
            GuiMain.getInstance().save(new File(selected.getPath()));
        } catch (IOException e) {
            Ref.LOGGER.log(Level.SEVERE, "Error due to write railroad data!", e);
        }
    }

    public void onLoad(ActionEvent event) {
        try {
            LocoNet.getRailroad().initRailroad();
        } catch (IOException | SAXException e) {
            Ref.LOGGER.log(Level.WARNING, "Loading of Railroad failed", e);
        }
    }

    public void onLoadFrom(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Save", "*.mtc"));
        fileChooser.setTitle(Ref.language.getString("window.choose_picture"));

        File selected = fileChooser.showOpenDialog(GUILoader.getPrimaryStage());

        if (selected == null)
            return;

        if (!selected.exists() || !Util.fileEndsWith(selected.getPath(), ".mtc"))
            return;

        try {
            LocoNet.getRailroad().openRailroad(selected.getPath());
        } catch (IOException | SAXException e) {
            Ref.LOGGER.log(Level.SEVERE, "Error due to write railroad data!", e);
        }
    }

    public void onFullscreen(ActionEvent event) {
        Config.fullScreen = !Config.fullScreen;
        ConfigManager.getInstance().onConfigChanged("fullScreen.text");
    }

    public void onOpenHelp(ActionEvent event) {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/juhu1705/ModellTrainController/wiki"));
        } catch (IOException | URISyntaxException e) {
            Ref.LOGGER.log(Level.SEVERE, "Can not browse link!", e);
        }
    }

    public void onActualPositionEdited(ActionEvent event) {
        if (this.actualPosition.getValue() != null) {
            if (this.actual != null) {
                this.addStation.setDisable(false);
                Sensor s = Util.getSensorByString(this.actualPosition.getValue(), Sensor.getAllSensors());
                if (s == null)
                    return;
                this.actual.setActualPosition(s);
                this.actualSensor.setText(this.actual.getActualPosition() != null ?
                        this.actual.getActualPosition().toString() :
                        Ref.language.getString("button.unset"));
                switch (s.getRotation()) {
                    case NORTH, SOUTH -> {
                        Position p = new Position(s.getPosition().getX(), s.getPosition().getY() - 1);
                        this.up.setDisable(false);
                        this.down.setDisable(false);
                        this.right.setDisable(true);
                        this.left.setDisable(true);
                        this.up.setSelected(true);
                        if (this.actual.getPrevPosition() == null)
                            this.actual.setLastPosition(p);
                    }
                    case EAST, WEST -> {
                        this.up.setDisable(true);
                        this.down.setDisable(true);
                        this.right.setDisable(false);
                        this.left.setDisable(false);
                        this.left.setSelected(true);
                        Position p1 = new Position(s.getPosition().getX() - 1, s.getPosition().getY());
                        if (this.actual.getPrevPosition() == null)
                            this.actual.setLastPosition(p1);
                    }
                }
            }
        }
    }

    public void onClose(ActionEvent event) {
        Util.onClose(event);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GuiMain.instance = this;

        ConfigManager.getInstance().createMenuTree(tree, config);

        this.mode.setItems(FXCollections.observableArrayList(ConfigManager.getInstance().getRegisteredOptions("mode")));

        this.mode.setOnAction(event -> {
            Config.mode = this.mode.getValue();
            ConfigManager.getInstance().onConfigChanged("mode.text");
        });

        this.mode.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String s) {
                if (Ref.language.containsKey("mode.text." + s))
                    return Ref.language.getString("mode.text." + s);
                return s;
            }

            @Override
            public String fromString(String s) {
                for (String string : Ref.language.keySet()) {
                    if (s.equals(Ref.language.getString("mode.text." + string)))
                        return string;
                }
                return s;
            }
        });

        for (int y = 0; y < 100; y++) {
            HBox box = new HBox();
            railroadCells.put(box, new ArrayList<>());
            for (int x = 0; x < 100; x++) {
                ImageView view = new ImageView(RailroadImages.EMPTY_2);
                view.setFitHeight(32);
                view.setFitWidth(32);
                view.setX(32);
                view.setY(32);
                view.setLayoutX(32);
                view.setLayoutY(32);
                view.setSmooth(true);
                view.setPickOnBounds(true);
                box.getChildren().add(view);
                railroadCells.get(box).add(view);
            }
            this.railroadLines.add(box);
            this.railroadSection.getChildren().add(box);

        }

        for (Theme t : Theme.values()) {
            MenuItem theme = new MenuItem(Ref.language.getString("theme.text." + t.name()));
            theme.setOnAction(action -> {
                Config.theme = t.name();
                ConfigManager.getInstance().onConfigChanged("theme.text");
            });
            this.theme.getItems().add(theme);
        }

        for (Language l : Language.values()) {
            MenuItem language = new MenuItem(Ref.language.getString("language.text." + l.name()));
            language.setOnAction(action -> {
                Config.language = l.name();
                ConfigManager.getInstance().onConfigChanged("language.text");
            });
            this.language.getItems().add(language);
        }

        trainsRoot = new TreeItem<>(Ref.language.getString("label.trains"));
        trains.setRoot(this.trainsRoot);
        trains.setShowRoot(false);

        sensorsRoot = new TreeItem<>("");
        sensors.setRoot(this.sensorsRoot);
        sensors.setShowRoot(false);

        switchesRoot = new TreeItem<>("");
        switches.setRoot(this.switchesRoot);
        switches.setShowRoot(false);

        this.updateSensors();
        this.updateSwitches();
        this.updateTrains();

        this.checkOutRailroad();

        actualPosition = new ComboBox();

        this.setMode();

        this.actualPosition.addEventHandler(ActionEvent.ANY, this::onActualPositionEdited);

        this.sensorListed.selectedProperty().addListener((o, oldValue, newValue) -> {
            if (oldValue != newValue) {
                this.onSensorListedChanged();
            }
        });

        actualPosition.setMinWidth(200);


        Label actualPosLabel = new Label(Ref.language.getString("label.actual_position"));

        this.up = new ToggleButton();
        this.down = new ToggleButton();
        this.left = new ToggleButton();
        this.right = new ToggleButton();

        this.up.setToggleGroup(group);
        this.down.setToggleGroup(group);
        this.right.setToggleGroup(group);
        this.left.setToggleGroup(group);

        FontIcon iconUp = new FontIcon("fas-angle-up");
        FontIcon iconDown = new FontIcon("fas-angle-down");
        FontIcon iconLeft = new FontIcon("fas-angle-left");
        FontIcon iconRight = new FontIcon("fas-angle-right");

        this.up.setGraphic(iconUp);
        this.down.setGraphic(iconDown);
        this.left.setGraphic(iconLeft);
        this.right.setGraphic(iconRight);

        this.up.setDisable(true);
        this.down.setDisable(true);
        this.left.setDisable(true);
        this.right.setDisable(true);

        this.up.setOnAction(event -> {
            if (this.actual != null) {
                this.up.setSelected(true);
                this.actual.setLastPosition(new Position(this.actual.getActualPosition().getPosition().getX(),
                        this.actual.getActualPosition().getPosition().getY() + 1));
            }
        });

        this.down.setOnAction(event -> {
            if (this.actual != null) {
                this.down.setSelected(true);
                this.actual.setLastPosition(new Position(this.actual.getActualPosition().getPosition().getX(),
                        this.actual.getActualPosition().getPosition().getY() - 1));
            }
        });

        this.left.setOnAction(event -> {
            if (this.actual != null) {
                this.left.setSelected(true);
                this.actual.setLastPosition(new Position(this.actual.getActualPosition().getPosition().getX() + 1,
                        this.actual.getActualPosition().getPosition().getY()));
            }
        });

        this.right.setOnAction(event -> {
            if (this.actual != null) {
                this.right.setSelected(true);
                this.actual.setLastPosition(new Position(this.actual.getActualPosition().getPosition().getX() - 1,
                        this.actual.getActualPosition().getPosition().getY()));
            }
        });

        HBox rightLeft = new HBox(left, right);
        rightLeft.setSpacing(30);
        rightLeft.setAlignment(Pos.CENTER);

        VBox directions = new VBox(up, rightLeft, down);

        directions.setAlignment(Pos.CENTER);

        Label drivingDirection = new Label(Ref.language.getString("label.drive_direction"));

        VBox box = new VBox(actualPosLabel, actualPosition, drivingDirection, directions);
        box.setMinWidth(200);
        box.setSpacing(10);
        box.setPadding(new Insets(10));

        PopOver popOver = new PopOver(box);

        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

        this.actualSensor.setOnAction(event -> {
            if (this.actual != null)
                popOver.setTitle(this.actual.getName());
            popOver.show(this.actualSensor);
        });

        allSensors.setPadding(new Insets(10));
        allSensors.setMinWidth(200);
        allSensors.setSpacing(10);

        ScrollPane pane = new ScrollPane(allSensors);
        pane.setFitToWidth(true);
        pane.setFitToHeight(true);
        pane.setVmax(200);

        PopOver popOver1 = new PopOver(pane);
        popOver1.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        this.addStation.setOnAction(event -> popOver1.show(addStation));

        FontIcon icon = new FontIcon("fas-plus");
        icon.setIconColor(Paint.valueOf("white"));
        this.addStation.setGraphic(icon);
        this.addStation.setGraphicTextGap(10);

        this.temporary.setPrefWidth(0);
        this.temporary.setPrefHeight(0);


        if (!LocoNet.getInstance().getTrains().isEmpty())
            this.setTrain(LocoNet.getInstance().getTrains().get(0));
        else {
            actualPosition.setDisable(true);
            actualSensor.setDisable(true);
        }
    }

    public void setMode() {
        this.mode.setValue(Config.mode);
        switch (Config.mode) {
            case Config.MODE_MANUAL:
                this.manualControls.setMinHeight(350);
                this.manualControls.setPrefHeight(350);
                this.manualControls.setVisible(true);
                this.automaticControls.setVisible(false);
                this.controls.setVisible(true);
                break;
            case Config.MODE_PLAN:
                this.manualControls.setMinHeight(0);
                this.manualControls.setPrefHeight(0);
                this.manualControls.setVisible(false);
                this.automaticControls.setVisible(true);
                this.controls.setVisible(true);
                Platform.runLater(this::updatePlanMode);
                break;
            case Config.MODE_RANDOM:
                this.manualControls.setVisible(false);
                this.automaticControls.setVisible(false);
                this.controls.setVisible(false);
                break;
        }
    }

    public void updatePlanMode() {
        ArrayList<String> sensors = new ArrayList<>();
        for (Sensor s : Sensor.getAllSensors()) {
            sensors.add(s.toString());
        }
        if (sensors.isEmpty()) {
            return;
        }

        this.actualPosition.setItems(FXCollections.observableArrayList(sensors));

        this.allSensors.getChildren().clear();


        this.temporary.setPrefWidth(27.0);
        Label l = new Label(Ref.language.getString("label.temporary"));
        l.setAlignment(Pos.CENTER);
        l.setPrefHeight(18);

        HBox box = new HBox(this.temporary, l);

        box.setAlignment(Pos.CENTER_LEFT);
        box.setSpacing(20);

        this.allSensors.getChildren().addAll(box);

        for (Sensor s : Sensor.getAllSensors()) {
            if (s.shouldBeListed()) {
                Button b = new Button(s.toString());
                b.setMinWidth(200);
                b.setOnAction(event -> {
                    if (this.actual != null) {
                        this.actual.getTrainStationManager().addStation(s, this.temporary.isSelected());
                    }
                });
                this.allSensors.getChildren().add(b);
            }
        }
    }

    public void updateTrains() {
        if (trainsRoot == null || trainsRoot.getChildren() == null)
            return;
        if (!trainsRoot.getChildren().isEmpty())
            trainsRoot.getChildren().clear();
        for (Train t : LocoNet.getInstance().getTrains()) {
            TreeItem<String> train = new TreeItem<>(t.getName());
            trainsRoot.getChildren().add(train);
        }
    }

    public void updateSensors() {
        sensorsRoot.getChildren().clear();
        for (Sensor s : Sensor.getAllSensors()) {
            TreeItem<String> train = new TreeItem<>("Sensor: " + s.getAddress() + " [" + s.getPosition().getX() + "] [" + s.getPosition().getY() + "]");
            sensorsRoot.getChildren().add(train);
        }
        if (Config.mode.equals(Config.MODE_PLAN))
            Platform.runLater(this::updatePlanMode);
    }

    public void updateSwitches() {
        switchesRoot.getChildren().clear();
        for (Switch s : Switch.getAllSwitches()) {
            TreeItem<String> train = new TreeItem<>("Switch: " + s.address());
            switchesRoot.getChildren().add(train);


        }
    }


    public void setImage(int x, int y, Image i) {
        HBox box1 = this.railroadLines.get(y);
        ArrayList<ImageView> cells = this.railroadCells.get(box1);
        cells.get(x).setImage(i);
    }

    public void save(File output) throws IOException {
        if (output == null)
            throw new IOException("No file to write to!");

        if (LocoNet.getRailroad().getRailroad() == null)
            return;

        final AbstractRailroadElement[][] railroadElements = LocoNet.getRailroad().getRailroad();

        FileWriter fw;
        BufferedWriter bw;

        fw = new FileWriter(output);
        bw = new BufferedWriter(fw);

        bw.append("<railroad>");
        bw.newLine();

        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                if (railroadElements[x][y] != null && railroadElements[x][y].getImage() != null) {
                    railroadElements[x][y].saveTo(bw);
                }
            }
        }

        for (Train t : LocoNet.getInstance().getTrains())
            t.saveTo(bw);

        bw.append("</railroad>");
        bw.newLine();

        bw.close();
    }

    public void updateTrainStationManager() {
        if (this.actual == null)
            return;

        trainStations.getChildren().clear();
        this.actual.updateTrainStationGuis(trainStations);
    }
}
