package de.noisruker.gui;

import de.noisruker.config.ConfigManager;
import de.noisruker.gui.progress.Progress;
import de.noisruker.loconet.messages.SwitchMessage;
import de.noisruker.main.GUILoader;
import de.noisruker.loconet.LocoNet;
import de.noisruker.util.Config;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class GuiLoading implements Initializable {

    public static AnchorPane s_window = null;

    public static AnchorPane getWindow() {
        return s_window;
    }

    @FXML
    public ProgressBar progress;

    @FXML
    public Label text;

    @FXML
    public AnchorPane window;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progress.progressProperty().bind(Progress.getInstance().progressProperty());

        Progress.getInstance().bindLabel(text);

        s_window = window;
    }

    public static void checkForUpdates() throws InterruptedException {
        new Thread(() -> {
            try {
                Progress.getInstance().setProgressDescription("Checking for Updates");
                Progress.getInstance().setProgress(0);



                Progress.getInstance().setProgressDescription("Connect to Server");
                Progress.getInstance().setProgress(0.25);



                Progress.getInstance().setProgressDescription("Searching for updates");
                Progress.getInstance().setProgress(0.5);

                Thread.sleep(1000);

                Progress.getInstance().setProgressDescription("Waiting");
                Progress.getInstance().setProgress(0.75);



                Progress.getInstance().setProgressDescription("Finished");
                Progress.getInstance().setProgress(1);

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Ref.LOGGER.info("Finished");
            Platform.runLater(() -> {
                if(Config.startImmediately && Arrays.asList(SerialPortList.getPortNames()).contains(Config.port)) {
                    GuiLoading.startLocoNet();
                } else {
                    Ref.LOGGER.info("Starting Init Page");
                    Util.updateWindow(GUILoader.getPrimaryStage(), "/assets/layouts/init_settings.fxml");
                }
            });
        }).start();
    }

    public static void startLocoNet() {
        new Thread(() -> {
            Progress.getInstance().setProgressDescription("Starting LocoNet Connection");
            Progress.getInstance().setProgress(-1);
            try {
                LocoNet.getInstance().start(Config.port);
            } catch (SerialPortException e) {
                Progress.getInstance().setProgressDescription("LocoNet connection failed! Restart and try again!");
                addBackButton();
                //Platform.runLater(() -> Util.updateWindow(GUILoader.getPrimaryStage(), "/assets/layouts/main.fxml").setResizable(true));
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) { }

            Progress.getInstance().setProgressDescription("Checking Connection");

            Progress.getInstance().setProgress(0);

            for (int i = 0; i < 5; i++) {
                try {
                    new SwitchMessage((byte) i, true).send();
                } catch (IOException e) {
                    Progress.getInstance().setProgressDescription("LocoNet connection closed! Restart and try again!");
                    addBackButton();
                    return;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) { }
                Progress.getInstance().setProgress(((double) i + 1) / 5);
            }

            Progress.getInstance().setProgressDescription("Connection checked");
            Progress.getInstance().setProgress(-1);


            /*if (Config.addTrainsFirst) {
                Progress.getInstance().setProgressDescription("Searching Trains");
                Progress.getInstance().setProgress(0);
                for (int i = 1; i <= 10; i++) {
                    Progress.getInstance().setProgress(((double) i) / 10);
                    try {
                        new LocoNetMessage(MessageType.OPC_LOCO_ADR, (byte) 0, (byte) i).send();
                    } catch (SerialPortException | LocoNetConnection.PortNotOpenException e) {
                        Progress.getInstance().setProgressDescription("LocoNet connection closed! Restart and try again!");
                        addBackButton();
                        return;
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ignored) {
                    }
                }
            }*/

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) { }

            Progress.getInstance().setProgress(1);
            Progress.getInstance().setProgressDescription("Finished");

            Platform.runLater(() -> {
                Util.updateWindow(GUILoader.getPrimaryStage(), "/assets/layouts/main.fxml").setResizable(true);
                Stage s = Util.openWindow("/assets/layouts/add_train.fxml", Ref.language.getString("window.add_train"), GUILoader.getPrimaryStage());
                if(s == null)
                    return;
                s.setResizable(true);
                s.setOnCloseRequest(e -> GuiAddTrain.actual.close(e));
            });
        }).start();
    }

    public static void addBackButton() {
        Platform.runLater(() -> {
            Button button = new Button();
            button.setText(Ref.language.getString("button.back"));
            button.setOnAction(action -> {
                Util.updateWindow(GUILoader.getPrimaryStage(), "/assets/layouts/init_settings.fxml");
            });

            button.setLayoutX(15);
            button.setLayoutY(250);

            GuiLoading.getWindow().getChildren().add(button);
        });
    }
}
