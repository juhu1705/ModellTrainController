package de.noisruker.gui;

import de.noisruker.config.ConfigManager;
import de.noisruker.config.FieldHandler;
import de.noisruker.loconet.LocoNet;
import de.noisruker.loconet.LocoNetMessageReceiver;
import de.noisruker.loconet.messages.TrainSlotMessage;
import de.noisruker.main.GUILoader;
import de.noisruker.railroad.AbstractRailroadElement;
import de.noisruker.railroad.RailroadReader;
import de.noisruker.railroad.Train;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.railroad.elements.Signal;
import de.noisruker.railroad.elements.Switch;
import de.noisruker.util.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.awt.*;
import java.io.*;
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
    public VBox railroadSection;

    public ArrayList<HBox> railroadLines = new ArrayList<>();
    public HashMap<HBox, ArrayList<ImageView>> railroadCells = new HashMap<>();

    public TreeItem<String> trainsRoot, switchesRoot, sensorsRoot;

    @FXML
    public Menu theme, language;

    @FXML
    public VBox config;

    private Train actual;

    @FXML
    public Label trainName;

    public void checkOutRailroad() {
        Platform.runLater(() -> {
            this.updateRailroad();
        });
    }

    private void updateRailroad() {
        if(LocoNet.getRailroad().getRailroad() == null)
            return;

        final AbstractRailroadElement[][] railroadElements = LocoNet.getRailroad().getRailroad();

        for(int y = 0; y < 100; y++) {
            HBox box = this.railroadLines.get(y);
            for(int x = 0; x < 100; x++) {
                if(railroadElements[x][y] != null && railroadElements[x][y].getImage() != null) {
                    this.railroadCells.get(box).get(x).setImage(railroadElements[x][y].getImage());
                    if(railroadElements[x][y] instanceof Switch) {
                        int finalX = x;
                        int finalY = y;
                        this.railroadCells.get(box).get(x).setOnMouseClicked(event -> {
                            Switch s = (Switch) railroadElements[finalX][finalY];
                            s.changeDirection();
                            this.railroadCells.get(box).get(finalX).setImage(railroadElements[finalX][finalY].getImage());
                        });
                    } else if(railroadElements[x][y] instanceof Signal) {
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
    }

    public void onNormalSpeed(ActionEvent event) {
        if(Config.mode.equals(Config.MODE_MANUAL) && actual != null)
            actual.applyNormalSpeed();
    }

    public void onMinSpeed(ActionEvent event) {
        if(Config.mode.equals(Config.MODE_MANUAL) && actual != null)
            actual.applyBreakSpeed();
    }

    public void onMaxSpeed(ActionEvent event) {
        if(Config.mode.equals(Config.MODE_MANUAL) && actual != null)
            actual.applyMaxSpeed();
    }

    public void onStop(ActionEvent event) {
        if(Config.mode.equals(Config.MODE_MANUAL) && actual != null)
            actual.setSpeed((byte) 0);
    }

    public void onStopImmediately(ActionEvent event) {
        if(actual != null)
            Util.runNext(actual::stopTrainImmediately);
    }

    public void onTrainSelectionChanged(MouseEvent event) {
        if(this.trains.getSelectionModel().getSelectedItem() == null)
            return;
        String name = this.trains.getSelectionModel().getSelectedItem().getValue();
        for(Train train: LocoNet.getInstance().getTrains())
            if(train.equals(name)) {
                this.actual = train;
                trainName.setText(name);
                Util.runNext(() -> train.setDirection(true));
            }
    }

    public void onCreateRailroad(ActionEvent event) {
        Stage s = Util.openWindow("/assets/layouts/edit_railroad.fxml", Ref.language.getString("window.railroad"), GUILoader.getPrimaryStage());
        if(s != null) {
            s.setResizable(true);
        }
    }

    public void onAddTrains(ActionEvent event) {
        Stage s = Util.openWindow("/assets/layouts/add_train.fxml", Ref.language.getString("window.add_train"), GUILoader.getPrimaryStage());
        if(s != null)
            s.setResizable(true);
    }

    public void onStartTrainControl(ActionEvent event) {
        if(this.trains.getSelectionModel() == null || this.trains.getSelectionModel().getSelectedItem() == null) {
            Notifications.create().darkStyle().title(Ref.language.getString("window.error")).text(Ref.language.getString("error.no_train_selected")).showError();
            return;
        }

        String s = this.trains.getSelectionModel().getSelectedItem().getValue();
        Train t = null;
        for(Train train: LocoNet.getInstance().getTrains())
            if(train.equals(s))
                t = train;
        if(t == null)
            Notifications.create().darkStyle().title(Ref.language.getString("window.error")).text(Ref.language.getString("error.no_train_selected")).showError();
        else {
            final Train finalT = t;
            Util.runNext(() -> {
                while(GuiControlTrain.toAdd != null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored) { }
                }
                GuiControlTrain.toAdd = finalT;
                Platform.runLater(() -> Util.openWindow("/assets/layouts/control_train.fxml", finalT.getName(), null));
            });
        }
    }

    public void onDeleteTrain(ActionEvent event) {
        if(this.trains.getSelectionModel() == null || this.trains.getSelectionModel().getSelectedItem() == null) {
            Notifications.create().darkStyle().title(Ref.language.getString("window.error")).text(Ref.language.getString("error.no_train_selected")).showError();
            return;
        }

        String s = this.trains.getSelectionModel().getSelectedItem().getValue();
        Train t = null;
        for(Train train: LocoNet.getInstance().getTrains())
            if(train.equals(s))
                t = train;
        if(t == null)
            Notifications.create().darkStyle().title(Ref.language.getString("window.error")).text(Ref.language.getString("error.no_train_selected")).showError();
        else {
            LocoNet.getInstance().getTrains().remove(t);
            this.updateTrains();
        }
    }

    public void onEditTrain(ActionEvent event) {
        if(this.trains.getSelectionModel() == null || this.trains.getSelectionModel().getSelectedItem() == null) {
            Notifications.create().darkStyle().title(Ref.language.getString("window.error")).text(Ref.language.getString("error.no_train_selected")).showError();
            return;
        }

        String s = this.trains.getSelectionModel().getSelectedItem().getValue();
        Train t = null;
        for(Train train: LocoNet.getInstance().getTrains())
            if(train.equals(s))
                t = train;
        if(t == null)
            Notifications.create().darkStyle().title(Ref.language.getString("window.error")).text(Ref.language.getString("error.no_train_selected")).showError();
        else {
            Train finalT = t;
            Util.runNext(() -> {
                while (GuiEditTrain.train != null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) { }
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

        if(selected == null)
            return;

        if(!Util.fileEndsWith(selected.getPath(), ".mtc"))
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

        if(selected == null)
            return;

        if(!selected.exists() || !Util.fileEndsWith(selected.getPath(), ".mtc"))
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

    public void onClose(ActionEvent event) {
        Util.onClose(event);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GuiMain.instance = this;

        ConfigManager.getInstance().createMenuTree(tree, config);

        for(int y = 0; y < 100; y++) {
            HBox box = new HBox();
            railroadCells.put(box, new ArrayList<>());
            for(int x = 0; x < 100; x++) {
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

        for(Theme t: Theme.values()) {
            MenuItem theme = new MenuItem(Ref.language.getString("theme.text." + t.name()));
            theme.setOnAction(action -> {
                Config.theme = t.name();
                ConfigManager.getInstance().onConfigChanged("theme.text");
            });
            this.theme.getItems().add(theme);
        }

        for(Language l: Language.values()) {
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
    }



    public void updateTrains() {
        if(trainsRoot == null || trainsRoot.getChildren() == null)
            return;
        if(!trainsRoot.getChildren().isEmpty())
            trainsRoot.getChildren().clear();
        for(Train t: LocoNet.getInstance().getTrains()) {
            TreeItem<String> train = new TreeItem<>(t.getName());
            trainsRoot.getChildren().add(train);
        }
    }

    public void updateSensors() {
        sensorsRoot.getChildren().clear();
        for(Sensor s: Sensor.getAllSensors()) {
            TreeItem<String> train = new TreeItem<>("Sensor: " + s.getAddress());
            sensorsRoot.getChildren().add(train);
        }
    }

    public void updateSwitches() {
        switchesRoot.getChildren().clear();
        for(Switch s: Switch.getAllSwitches()) {
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

        if(LocoNet.getRailroad().getRailroad() == null)
            return;

        final AbstractRailroadElement[][] railroadElements = LocoNet.getRailroad().getRailroad();

        FileWriter fw;
        BufferedWriter bw;

        fw = new FileWriter(output);
        bw = new BufferedWriter(fw);

        bw.append("<railroad>");
        bw.newLine();

        for(int y = 0; y < 100; y++) {
            for(int x = 0; x < 100; x++) {
                if(railroadElements[x][y] != null && railroadElements[x][y].getImage() != null) {
                    railroadElements[x][y].saveTo(bw);
                }
            }
        }

        for(Train t: LocoNet.getInstance().getTrains())
            t.saveTo(bw);

        bw.append("</railroad>");
        bw.newLine();

        bw.close();
    }
}
