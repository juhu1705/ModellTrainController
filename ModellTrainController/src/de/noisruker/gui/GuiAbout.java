package de.noisruker.gui;

import de.noisruker.util.Ref;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiAbout implements Initializable {

    @FXML
    public Label version;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        version.setText(Ref.language.getString("label.version") + ": " + Ref.VERSION);
    }
}
