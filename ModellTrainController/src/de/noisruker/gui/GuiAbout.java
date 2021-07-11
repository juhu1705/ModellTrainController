package de.noisruker.gui;

import de.noisruker.main.GUILoader;
import de.noisruker.util.Ref;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.controlsfx.control.Notifications;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class GuiAbout implements Initializable {

    @FXML
    public Label version;

    public void onPgPKey(ActionEvent event) {
        try {
            if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
                Desktop.getDesktop().browse(new URI("https://keys.openpgp.org/search?q=fabius.mettner%40gmx.de"));
            else {
                Ref.LOGGER.log(Level.SEVERE, "Can not browse link! Here the link to the pgp key: https://keys.openpgp.org/search?q=fabius.mettner%40gmx.de");
                Notifications.create().darkStyle().title("Can not browse link!").text("Here the link to the pgp key: https://keys.openpgp.org/search?q=fabius.mettner%40gmx.de").owner(GUILoader.getPrimaryStage()).showConfirm();
            }
        } catch (IOException | URISyntaxException | UnsupportedOperationException e) {
            Ref.LOGGER.log(Level.SEVERE, "Can not browse link! Here the link to the pgp key: https://keys.openpgp.org/search?q=fabius.mettner%40gmx.de");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        version.setText(Ref.language.getString("label.version") + ": " + Ref.VERSION);
    }
}
