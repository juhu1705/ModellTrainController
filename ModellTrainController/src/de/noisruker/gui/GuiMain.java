package de.noisruker.gui;

import de.noisruker.config.ConfigManager;
import de.noisruker.loconet.LocoNet;
import de.noisruker.loconet.LocoNetMessageReceiver;
import de.noisruker.loconet.messages.TrainSlotMessage;
import de.noisruker.main.GUILoader;
import de.noisruker.railroad.Train;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    public VBox config;

    public void onAddTrains(ActionEvent event) {
        Stage s = Util.openWindow("/assets/layouts/add_train.fxml", Ref.language.getString("window.add_train"), GUILoader.getPrimaryStage());
        if(s != null)
            s.setResizable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GuiMain.instance = this;

        ConfigManager.getInstance().createMenuTree(tree, config);

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
