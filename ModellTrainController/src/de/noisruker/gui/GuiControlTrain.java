package de.noisruker.gui;

import de.noisruker.railroad.Train;
import de.noisruker.util.Config;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class GuiControlTrain implements Initializable {

    public static Train toAdd = null;
    private Train t;

    @FXML
    public Button max, normal, min;

    @FXML
    public Label header, labelSpeed;

    @FXML
    public Slider speed, direction;

    @FXML
    public ImageView picture;

    @FXML
    public HBox more;

    @FXML
    public ListView<String> standardValues;

    private final Train.TrainSpeedChangeListener changeListener = (train, newSpeed) -> Platform.runLater(() -> this.speed.setValue(newSpeed - 1));

    public void onNormalSpeed(ActionEvent event) {
        if (Config.mode.equals(Config.MODE_MANUAL))
            t.applyNormalSpeed();
    }

    public void onMinSpeed(ActionEvent event) {
        if (Config.mode.equals(Config.MODE_MANUAL))
            t.applyBreakSpeed();
    }

    public void onMaxSpeed(ActionEvent event) {
        if (Config.mode.equals(Config.MODE_MANUAL))
            t.applyMaxSpeed();
    }

    public void onStop(ActionEvent event) {
        if (Config.mode.equals(Config.MODE_MANUAL))
            t.setSpeed((byte) 0);
    }

    public void onStopImmediately(ActionEvent event) {
        Util.runNext(t::stopTrainImmediately);
    }

    public void showMore(ActionEvent event) {
        if (!more.isVisible()) {
            more.setVisible(true);
            Window window = ((Hyperlink) event.getSource()).getScene().getWindow();
            window.setHeight(window.getHeight() + 200);
        } else {
            more.setVisible(false);
            Window window = ((Hyperlink) event.getSource()).getScene().getWindow();
            window.setHeight(window.getHeight() - 200);
        }
    }

    public void onChoosePicture(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Picture", "*.png", "*.jpg"));
        fileChooser.setTitle(Ref.language.getString("window.choose_picture"));
        if (t.getPicturePath() != null && !t.getPicturePath().isBlank()) {
            File file = new File(t.getPicturePath());

            if (file.exists() && new File(file.getParent()).exists())
                fileChooser.setInitialDirectory(new File(file.getParent()));
        }

        File selected = fileChooser.showOpenDialog(((Hyperlink) event.getSource()).getScene().getWindow());

        if (selected == null)
            return;

        if (selected.exists() && Util.fileEndsWith(selected.getPath(), ".png", ".jpg", ".JPG"))
            t.setPicturePath(selected.getPath());

        this.loadPicture();
    }

    public void onPictureDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles())
            dragboard.getFiles().forEach(file -> {
                if (Util.fileEndsWith(file.getPath(), ".png", ".jpg", ".pdf", ".JPG"))
                    t.setPicturePath(file.getPath());
            });
    }

    public void onDragOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
    }

    public void onEdit(ActionEvent event) {
        Util.runNext(() -> {
            while (GuiEditTrain.train != null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
            GuiEditTrain.train = this.t;
            Platform.runLater(() ->
                    Util.openWindow("/assets/layouts/edit_train.fxml",
                            Ref.language.getString("window.edit_train"), null));
        });
    }

    public void loadPicture() {
        if (t.getPicturePath() != null && !t.getPicturePath().isBlank())
            this.picture.setImage(new Image(t.getPicturePath()));
        else
            this.picture.setImage(new Image(Ref.getFileString("/assets/textures/images/no_picture.png")));
    }

    public void onClose(WindowEvent event) {
        t.removeSpeedChangeListener(changeListener);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (toAdd == null) {
            this.header.setText(Ref.language.getString("label.no_train_found"));
            return;
        }
        this.t = toAdd;

        this.header.setText(t.getName());

        if (!Config.mode.equals(Config.MODE_MANUAL)) {
            speed.setDisable(true);
            max.setDisable(true);
            normal.setDisable(true);
            min.setDisable(true);
        }

        this.standardValues.setEditable(false);
        this.standardValues.setItems(FXCollections.observableArrayList(Ref.language.getString("button.max_speed") + ": " + t.getMaxSpeed(), Ref.language.getString("button.normal_speed") + ": " + t.getNormalSpeed(), Ref.language.getString("button.min_speed") + ": " + t.getMinSpeed()));

        t.registerSpeedChangeListener(changeListener);

        this.speed.valueProperty().addListener(
                (observableValue, oldVal, newVal) -> {
                    this.labelSpeed.setText(newVal.intValue() + "");

                    if (Config.mode.equals(Config.MODE_MANUAL) && oldVal.byteValue() != newVal.byteValue()) {
                        t.setSpeed(newVal.byteValue());
                    }
                });

        this.speed.setMax(t.getMaxSpeed());
        this.speed.setMin(0);
        this.speed.setValue(t.getSpeed());

        this.loadPicture();
        toAdd = null;

        direction.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            if (newVal.doubleValue() % 1 != 0) {
                direction.setValue(newVal.intValue());
            }
            if (newVal.intValue() == 0)
                t.stopTrain();
            else
                t.setDirection(newVal.intValue() == 1);
        });
        direction.setValue(1);
    }
}
