package de.noisruker.gui;

import de.noisruker.config.ConfigManager;
import de.noisruker.main.GUILoader;
import de.noisruker.util.Config;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jssc.SerialPortList;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class GuiStart implements Initializable {

    public static Stage stage = null;

    @FXML
    public ToggleSwitch startImmediately;

    @FXML
    public ComboBox<String> port;

    @FXML
    public Button next;

    public void start(ActionEvent event) {
        Config.startImmediately = startImmediately.isSelected();
        Config.port = port.getValue();
        ConfigManager.getInstance().onConfigChanged("port.text");
        ConfigManager.getInstance().onConfigChanged("startImmediately.text");

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

            if (Config.port.isBlank() || !ports.contains(Config.port))
                port.setValue(ports.get(0));
            else
                port.setValue(Config.port);
        }

        if(Config.startImmediately)
            startImmediately.setSelected(true);

        ValidationSupport support = new ValidationSupport();

        Validator<String> hasInput = (c, str) -> ValidationResult.fromErrorIf(c, Ref.language.getString("invalid.port"),
                str == null || str.isBlank() || !ports.contains(str));

        support.registerValidator(this.port, true, hasInput);

        next.disableProperty().bind(support.invalidProperty());
    }
}
