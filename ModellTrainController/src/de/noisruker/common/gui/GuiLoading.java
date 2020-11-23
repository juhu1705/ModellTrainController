package de.noisruker.common.gui;

import de.noisruker.common.messages.SwitchMessage;
import de.noisruker.main.GUILoader;
import de.noisruker.server.loconet.LocoNet;
import de.noisruker.server.loconet.LocoNetConnection;
import de.noisruker.server.loconet.messages.LocoNetMessage;
import de.noisruker.server.loconet.messages.MessageType;
import de.noisruker.util.Config;
import de.noisruker.util.Ref;
import de.noisruker.util.Theme;
import de.noisruker.util.Util;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import jssc.SerialPortException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GuiLoading implements Initializable {

    @FXML
    public ProgressIndicator progress;

    @FXML
    public Label text;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progress.progressProperty().bind(Progress.getInstance().progressProperty());

        Progress.getInstance().bindLabel(text);
    }

    public static void checkForUpdates() throws InterruptedException {
        new Thread(() -> {
            try {
                Progress.getInstance().setProgressDescription("Checking for Updates");
                Progress.getInstance().setProgress(0);

                Thread.sleep(1000);

                Progress.getInstance().setProgressDescription("Connect to Server");
                Progress.getInstance().setProgress(0.25);

                Thread.sleep(1000);

                Progress.getInstance().setProgressDescription("Searching for updates");
                Progress.getInstance().setProgress(0.5);

                Thread.sleep(1000);

                Progress.getInstance().setProgressDescription("Waiting");
                Progress.getInstance().setProgress(0.75);

                Thread.sleep(1000);

                Progress.getInstance().setProgressDescription("Finished");
                Progress.getInstance().setProgress(1);

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Ref.LOGGER.info("Finished");
            Platform.runLater(() -> {
                Ref.LOGGER.info("Starting Init Page");
                Util.updateWindow(GUILoader.getPrimaryStage(), "/assets/layouts/InitSettings.fxml", Theme.LIGHT);
            });
        }).start();
    }

    public static void startLocoNet() {
        Progress.getInstance().setProgressDescription("Starting LocoNet Connection");
        Progress.getInstance().setProgress(-1);
        try {
            LocoNet.getInstance().start(Config.port);
        } catch (SerialPortException e) {
            Progress.getInstance().setProgressDescription("LocoNet connection failed! Restart and try again!");
            return;
        }

        Progress.getInstance().setProgressDescription("Checking Connection");

        Progress.getInstance().setProgress(0);

        for(int i = 0; i < 5; i++) {
            try {
                new SwitchMessage((byte) 0, true).send();
            } catch (IOException e) {
                Progress.getInstance().setProgressDescription("LocoNet connection closed! Restart and try again!");
                return;
            }
            Progress.getInstance().setProgress(((double)i + 1) / 5);
        }

        Progress.getInstance().setProgressDescription("Connection checked");
        Progress.getInstance().setProgress(-1);

        if(Config.addTrainsFirst) {
            Progress.getInstance().setProgressDescription("Searching Trains");
            Progress.getInstance().setProgress(0);
            for(int i = 1; i <= 50; i++) {
                Progress.getInstance().setProgress(((double)i) / 50);
                try {
                    new LocoNetMessage(MessageType.OPC_LOCO_ADR, (byte) 0, (byte) i).send();
                } catch (SerialPortException | LocoNetConnection.PortNotOpenException e) {
                    Progress.getInstance().setProgressDescription("LocoNet connection closed! Restart and try again!");
                    return;
                }
            }
        }

        Progress.getInstance().setProgress(1);
        Progress.getInstance().setProgressDescription("Finished");
    }
}
