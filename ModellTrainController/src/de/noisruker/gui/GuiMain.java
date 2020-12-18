package de.noisruker.gui;

import de.noisruker.config.ConfigManager;
import de.noisruker.loconet.LocoNet;
import de.noisruker.loconet.LocoNetMessageReceiver;
import de.noisruker.loconet.messages.TrainSlotMessage;
import de.noisruker.main.GUILoader;
import de.noisruker.railroad.Train;
import de.noisruker.util.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class GuiMain implements Initializable {

    private static GuiMain instance = null;

    public static GuiMain getInstance() {
        return instance;
    }

    public int start;

    @FXML
    public TreeView<String> tree, trains;

    @FXML
    public VBox railroadSection;

    public ArrayList<HBox> railroadLines = new ArrayList<>();
    public HashMap<HBox, ArrayList<ImageView>> railroadCells = new HashMap<>();

    public TreeItem<String> trainsRoot;

    @FXML
    public Menu theme, language;

    @FXML
    public VBox config;

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

    public void onFullscreen(ActionEvent event) {
        Config.fullScreen = !Config.fullScreen;
        ConfigManager.getInstance().onConfigChanged("fullScreen.text");
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
                ImageView view = new ImageView(RailroadImages.EMPTY);
                view.setFitHeight(32);
                view.setFitWidth(32);
                view.setX(32);
                view.setY(32);
                view.setLayoutX(32);
                view.setLayoutY(32);
                view.setSmooth(true);
                int finalY = y;
                int finalX = x;
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

        LocoNetMessageReceiver.getInstance().registerListener(message -> {
            if(message instanceof TrainSlotMessage) {
                this.updateTrains();
            }
        });
        trainsRoot = new TreeItem<>(Ref.language.getString("label.trains"));
        trains.setRoot(this.trainsRoot);
        trains.setShowRoot(false);
        this.updateTrains();
    }

    public void updateTrains() {
        trainsRoot.getChildren().clear();
        for(Train t: LocoNet.getInstance().getTrains()) {
            TreeItem<String> train = new TreeItem<>(t.getName());
            trainsRoot.getChildren().add(train);
        }
    }

    public void setImage(int x, int y, Image i) {
        HBox box1 = this.railroadLines.get(y);
        ArrayList<ImageView> cells = this.railroadCells.get(box1);
        cells.get(x).setImage(i);
    }
}
