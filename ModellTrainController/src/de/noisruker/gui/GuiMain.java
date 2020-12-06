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
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiMain implements Initializable {

    private static GuiMain instance = null;

    public static GuiMain getInstance() {
        return instance;
    }

    @FXML
    public TreeView<String> tree, trains;

    public TreeItem<String> trainsRoot;

    @FXML
    public Menu theme, language;

    @FXML
    public VBox config;

    public void onAddTrains(ActionEvent event) {
        Stage s = Util.openWindow("/assets/layouts/add_train.fxml", Ref.language.getString("window.add_train"), GUILoader.getPrimaryStage());
        if(s != null)
            s.setResizable(true);
    }

    public void onStartTrainControl(ActionEvent event) {
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
                Platform.runLater(() -> Util.openWindow("/assets/layouts/control_train.fxml", finalT.getName(), null).setResizable(true));
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
            language.setOnAction(actio -> {
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
}
