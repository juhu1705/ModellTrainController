package de.noisruker.gui;

import de.noisruker.config.ConfigManager;
import de.noisruker.gui.progress.Progress;
import de.noisruker.loconet.LocoNetConnection;
import de.noisruker.loconet.LocoNetMessageSender;
import de.noisruker.loconet.messages.SwitchMessage;
import de.noisruker.main.GUILoader;
import de.noisruker.loconet.LocoNet;
import de.noisruker.railroad.Train;
import de.noisruker.railroad.elements.Signal;
import de.noisruker.railroad.elements.Switch;
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
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;

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
                Progress.getInstance().setProgressDescription(Ref.language.getString("info.connecting_to_update_server"));
                Progress.getInstance().setProgress(0.25);

                Progress.getInstance().setProgressDescription(Ref.language.getString("info.update_check"));
                Progress.getInstance().setProgress(0.5);

                Thread.sleep(500);

                Progress.getInstance().setProgressDescription(Ref.language.getString("info.waiting"));
                Progress.getInstance().setProgress(0.75);



                Progress.getInstance().setProgressDescription(Ref.language.getString("info.finished"));
                Progress.getInstance().setProgress(1);

                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                if(Config.startImmediately && Arrays.asList(SerialPortList.getPortNames()).contains(Config.port)) {
                    GuiLoading.startLocoNet();
                } else {
                    Util.updateWindow(GUILoader.getPrimaryStage(), "/assets/layouts/init_settings.fxml");
                    Platform.runLater(() -> Util.updateWindow(GUILoader.getPrimaryStage(), "/assets/layouts/main.fxml").setResizable(true));
                }
            });
        }).start();
    }

    public static void startLocoNet() {
        new Thread(() -> {
            Progress.getInstance().setProgressDescription(Ref.language.getString("info.connect_to_loconet"));
            Progress.getInstance().setProgress(-1);
            try {
                LocoNet.getInstance().start(Config.port);
            } catch (SerialPortException e) {
                Progress.getInstance().setProgressDescription(Ref.language.getString("info.connection_failed"));
                addBackButton();
                //Platform.runLater(() -> Util.updateWindow(GUILoader.getPrimaryStage(), "/assets/layouts/main.fxml").setResizable(true));
                return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) { }

            Progress.getInstance().setProgressDescription(Ref.language.getString("info.check_connection"));

            Progress.getInstance().setProgress(0);
            try {
                for (byte i = 1; i < 6; i++) {
                    LocoNet.checkConnection = i;

                    Train.addTrain(i);

                    int timeLeft = 10;

                    while(!LocoNet.connectionChecked) {
                        if(timeLeft == 0){
                            Progress.getInstance().setProgressDescription(Ref.language.getString("info.connection_failed"));
                            addBackButton();
                            return;
                        }

                        Thread.sleep(500);
                        timeLeft--;
                    }
                    LocoNet.connectionChecked = false;
                    Progress.getInstance().setProgress((double) i / 5d);
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Progress.getInstance().setProgressDescription(Ref.language.getString("info.connection_failed"));
                addBackButton();
                //Platform.runLater(() -> Util.updateWindow(GUILoader.getPrimaryStage(), "/assets/layouts/main.fxml").setResizable(true));
                return;
            }

            Progress.getInstance().setProgressDescription(Ref.language.getString("info.connection_complete"));
            Progress.getInstance().setProgress(-1);

            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) { }

            Progress.getInstance().setProgressDescription(Ref.language.getString("info.load_railroad"));

            try {
                LocoNet.getRailroad().initRailroad();
            } catch (IOException | SAXException e) {
                Ref.LOGGER.log(Level.WARNING, "Loading of Railroad failed", e);
            }

            Progress.getInstance().setProgressDescription(Ref.language.getString("info.init_switches"));
            Progress.getInstance().setProgress(0);

            for(int i = 0; i < Switch.getAllSwitches().size(); i++) {
                Switch.getAllSwitches().get(i).setAndUpdateState(true);
                Progress.getInstance().setProgress(((double)i) / ((double)Switch.getAllSwitches().size()));
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) { }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) { }

            Progress.getInstance().setProgressDescription(Ref.language.getString("info.init_signals"));
            Progress.getInstance().setProgress(0);

            for(int i = 0; i < Signal.getAllSignals().size(); i++) {
                Signal.getAllSignals().get(i).setAndUpdateState(false);
                Progress.getInstance().setProgress(((double)i) / ((double)Signal.getAllSignals().size()));
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) { }
            }

            LocoNet.getRailroad().init();

            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) { }

            Progress.getInstance().setProgress(0);

            if(Config.reportAddress > 0)
                new SwitchMessage((byte) (Config.reportAddress - 1), true).toLocoNetMessage().send();

            Progress.getInstance().setProgress(-1);
            Progress.getInstance().setProgressDescription(Ref.language.getString("info.start_window"));

            while (!LocoNetMessageSender.getInstance().areAllMessagesSend()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) { }
            }

            Platform.runLater(() -> {
                Util.updateWindow(GUILoader.getPrimaryStage(), "/assets/layouts/main.fxml").setResizable(true);
                GUILoader.getPrimaryStage().setMaximized(true);
                if(Config.fullScreen)
                    GUILoader.getPrimaryStage().setFullScreen(true);
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
