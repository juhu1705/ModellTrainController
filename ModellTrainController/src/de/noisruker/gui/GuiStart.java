package de.noisruker.gui;

import de.noisruker.config.ConfigManager;
import de.noisruker.main.GUILoader;
import de.noisruker.util.Config;
import de.noisruker.util.Theme;
import de.noisruker.util.Util;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jssc.SerialPortList;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class GuiStart implements Initializable {

    public static Stage stage = null;

    @FXML
    public CheckBox dontShow;


    @FXML
    public ComboBox<String> port;

    public void start(ActionEvent event) {
        Config.startImmediately = dontShow.isSelected();
        Config.port = port.getValue();
        ConfigManager.getInstance().onConfigChanged();

        if(stage == null)
            Util.openWindow("/assets/layouts/loading.fxml", "Loading", GUILoader.getPrimaryStage());
        else
            Util.updateWindow(stage, "/assets/layouts/loading.fxml");

        GuiLoading.startLocoNet();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<String> ports = new ArrayList<>(Arrays.asList(SerialPortList.getPortNames()));

        if(!ports.isEmpty()) {
            port.setItems(FXCollections.observableArrayList(ports));

            if (Config.port.equals("") || !ports.contains(Config.port))
                port.setValue(ports.get(0));
            else
                port.setValue(Config.port);
        }

        if(Config.startImmediately)
            dontShow.setSelected(true);
    }
}
