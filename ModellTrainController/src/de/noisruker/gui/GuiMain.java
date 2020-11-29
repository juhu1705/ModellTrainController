package de.noisruker.gui;

import de.noisruker.config.ConfigManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiMain implements Initializable {

    @FXML
    public TreeView<String> tree;

    @FXML
    public VBox config;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ConfigManager.getInstance().createMenuTree(tree, config);
    }
}
