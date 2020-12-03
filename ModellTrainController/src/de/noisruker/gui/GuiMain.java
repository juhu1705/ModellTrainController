package de.noisruker.gui;

import de.noisruker.config.ConfigManager;
import de.noisruker.main.GUILoader;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiMain implements Initializable {

    @FXML
    public TreeView<String> tree;

    @FXML
    public VBox config;

    public void onAddTrains(ActionEvent event) {
        Stage s = Util.openWindow("/assets/layout/add_train.fxml", Ref.language.getString("window.add_train"), GUILoader.getPrimaryStage());
        if(s != null)
            s.setResizable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ConfigManager.getInstance().createMenuTree(tree, config);
    }
}
