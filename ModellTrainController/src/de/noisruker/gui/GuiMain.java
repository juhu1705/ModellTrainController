package de.noisruker.gui;

import de.noisruker.config.ConfigManager;
import de.noisruker.loconet.LocoNet;
import de.noisruker.loconet.LocoNetMessageReceiver;
import de.noisruker.loconet.messages.TrainSlotMessage;
import de.noisruker.main.GUILoader;
import de.noisruker.railroad.Train;
import de.noisruker.util.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class GuiMain implements Initializable {

    private static GuiMain instance = null;

    public static GuiMain getInstance() {
        return instance;
    }

    public int start;

    @FXML
    public TreeView<String> tree, trains;

    @FXML
    public ToggleButton straight, curve, RSwitch, LSwitch, LRSwitch, delete, cut, shouldHide;

    @FXML
    public VBox railroadSection;

    public ArrayList<HBox> railroadLines = new ArrayList<>();
    public HashMap<HBox, ArrayList<ImageView>> railroadCells = new HashMap<>();

    public TreeItem<String> trainsRoot;

    @FXML
    public Menu theme, language;

    @FXML
    public VBox config;

    public EditMode mode = EditMode.STRAIGHT;

    private enum EditMode {
        STRAIGHT,
        CURVE,
        SWITCH_R,
        SWITCH_L,
        SWITCH_L_R,
        DELETE,
        CUT
    }

    public Rotation rotation = Rotation.NORTH;

    private enum Rotation {
        NORTH,
        WEST,
        EAST,
        SOUTH
    }

    public Image hover_image = getImageForModeAndRotation(true);

    public void onNextRotation(ActionEvent event) {
        switch (rotation) {
            case NORTH:
                rotation = Rotation.EAST;
                break;
            case WEST:
                rotation = Rotation.NORTH;
                break;
            case EAST:
                rotation = Rotation.SOUTH;
                break;
            case SOUTH:
                rotation = Rotation.WEST;
                break;
        }
        this.hover_image = getImageForModeAndRotation(true);
    }

    public void onPreviousRotation(ActionEvent event) {
        switch (rotation) {
            case NORTH:
                rotation = Rotation.WEST;
                break;
            case WEST:
                rotation = Rotation.SOUTH;
                break;
            case EAST:
                rotation = Rotation.NORTH;
                break;
            case SOUTH:
                rotation = Rotation.EAST;
                break;
        }
        this.hover_image = getImageForModeAndRotation(true);
    }

    public Image getImageForModeAndRotation(boolean AsHover) {
        switch (mode) {
            case STRAIGHT:
                switch (rotation) {
                    case NORTH:
                    case SOUTH:
                        return AsHover ? RailroadImages.STRAIGHT_VERTICAL_HOVER : RailroadImages.STRAIGHT_VERTICAL;
                    case WEST:
                    case EAST:
                        return AsHover ? RailroadImages.STRAIGHT_HORIZONTAL_HOVER : RailroadImages.STRAIGHT_HORIZONTAL;
                }
            case CURVE:
                switch (rotation) {
                    case NORTH:
                        return AsHover ? RailroadImages.CURVE_NORTH_EAST_HOVER : RailroadImages.CURVE_NORTH_EAST;
                    case SOUTH:
                        return AsHover ? RailroadImages.CURVE_SOUTH_WEST_HOVER : RailroadImages.CURVE_SOUTH_WEST;
                    case WEST:
                        return AsHover ? RailroadImages.CURVE_NORTH_WEST_HOVER : RailroadImages.CURVE_NORTH_WEST;
                    case EAST:
                        return AsHover ? RailroadImages.CURVE_SOUTH_EAST_HOVER : RailroadImages.CURVE_SOUTH_EAST;
                }
            case SWITCH_R:
                switch (rotation) {
                    case NORTH:
                        return AsHover ? RailroadImages.SWITCH_NORTH_2_HOVER : RailroadImages.SWITCH_NORTH_2;
                    case SOUTH:
                        return AsHover ? RailroadImages.SWITCH_SOUTH_2_HOVER : RailroadImages.SWITCH_SOUTH_2;
                    case WEST:
                        return AsHover ? RailroadImages.SWITCH_WEST_2_HOVER : RailroadImages.SWITCH_WEST_2;
                    case EAST:
                        return AsHover ? RailroadImages.SWITCH_EAST_2_HOVER : RailroadImages.SWITCH_EAST_2;
                }
            case SWITCH_L:
                switch (rotation) {
                    case NORTH:
                        return AsHover ? RailroadImages.SWITCH_NORTH_1_HOVER : RailroadImages.SWITCH_NORTH_1;
                    case SOUTH:
                        return AsHover ? RailroadImages.SWITCH_SOUTH_1_HOVER : RailroadImages.SWITCH_SOUTH_1;
                    case WEST:
                        return AsHover ? RailroadImages.SWITCH_WEST_1_HOVER : RailroadImages.SWITCH_WEST_1;
                    case EAST:
                        return AsHover ? RailroadImages.SWITCH_EAST_1_HOVER : RailroadImages.SWITCH_EAST_1;
                }
            case SWITCH_L_R:
                switch (rotation) {
                    case NORTH:
                        return AsHover ? RailroadImages.SWITCH_NORTH_3_HOVER : RailroadImages.SWITCH_NORTH_3;
                    case SOUTH:
                        return AsHover ? RailroadImages.SWITCH_SOUTH_3_HOVER : RailroadImages.SWITCH_SOUTH_3;
                    case WEST:
                        return AsHover ? RailroadImages.SWITCH_WEST_3_HOVER : RailroadImages.SWITCH_WEST_3;
                    case EAST:
                        return AsHover ? RailroadImages.SWITCH_EAST_3_HOVER : RailroadImages.SWITCH_EAST_3;
                }
            case CUT:
                switch (rotation) {
                    case NORTH:
                    case SOUTH:
                        return AsHover ? RailroadImages.STRAIGHT_VERTICAL_HOVER : RailroadImages.STRAIGHT_CUT_VERTICAL;
                    case WEST:
                    case EAST:
                        return AsHover ? RailroadImages.STRAIGHT_HORIZONTAL_HOVER : RailroadImages.STRAIGHT_CUT_HORIZONTAL;
                }
        }

        return shouldHide.isSelected() ? RailroadImages.EMPTY : RailroadImages.EMPTY_2;
    }

    public void onHideChanged(ActionEvent event) {
        this.railroadCells.forEach((key, list) -> {
            list.forEach(image -> {
                if(image.getImage().equals(RailroadImages.EMPTY) || image.getImage().equals(RailroadImages.EMPTY_2))
                    image.setImage(shouldHide.isSelected() ? RailroadImages.EMPTY : RailroadImages.EMPTY_2);
            });
        });
    }

    public void onSetModeToStraight(ActionEvent event) {
        straight.setSelected(true);
        curve.setSelected(false);
        RSwitch.setSelected(false);
        LSwitch.setSelected(false);
        LRSwitch.setSelected(false);
        delete.setSelected(false);
        cut.setSelected(false);
        mode = EditMode.STRAIGHT;
        this.hover_image = getImageForModeAndRotation(true);
    }

    public void onSetModeToCurve(ActionEvent event) {
        curve.setSelected(true);
        straight.setSelected(false);
        RSwitch.setSelected(false);
        LSwitch.setSelected(false);
        LRSwitch.setSelected(false);
        delete.setSelected(false);
        cut.setSelected(false);
        mode = EditMode.CURVE;
        this.hover_image = getImageForModeAndRotation(true);
    }

    public void onSetModeToRSwitch(ActionEvent event) {
        RSwitch.setSelected(true);
        curve.setSelected(false);
        straight.setSelected(false);
        LSwitch.setSelected(false);
        LRSwitch.setSelected(false);
        delete.setSelected(false);
        cut.setSelected(false);
        mode = EditMode.SWITCH_R;
        this.hover_image = getImageForModeAndRotation(true);
    }

    public void onSetModeToLSwitch(ActionEvent event) {
        LSwitch.setSelected(true);
        curve.setSelected(false);
        straight.setSelected(false);
        RSwitch.setSelected(false);
        LRSwitch.setSelected(false);
        delete.setSelected(false);
        cut.setSelected(false);
        mode = EditMode.SWITCH_L;
        this.hover_image = getImageForModeAndRotation(true);
    }

    public void onSetModeToLRSwitch(ActionEvent event) {
        LRSwitch.setSelected(true);
        curve.setSelected(false);
        straight.setSelected(false);
        RSwitch.setSelected(false);
        LSwitch.setSelected(false);
        delete.setSelected(false);
        cut.setSelected(false);
        mode = EditMode.SWITCH_L_R;
        this.hover_image = getImageForModeAndRotation(true);
    }

    public void onSetModeToDelete(ActionEvent event) {
        delete.setSelected(true);
        curve.setSelected(false);
        straight.setSelected(false);
        RSwitch.setSelected(false);
        LSwitch.setSelected(false);
        LRSwitch.setSelected(false);
        cut.setSelected(false);
        mode = EditMode.DELETE;
        this.hover_image = getImageForModeAndRotation(true);
    }

    public void onSetModeToCut(ActionEvent event) {
        cut.setSelected(true);
        curve.setSelected(false);
        straight.setSelected(false);
        RSwitch.setSelected(false);
        LSwitch.setSelected(false);
        LRSwitch.setSelected(false);
        delete.setSelected(false);
        mode = EditMode.CUT;
        this.hover_image = getImageForModeAndRotation(true);
    }

    public void onAddTrains(ActionEvent event) {
        Stage s = Util.openWindow("/assets/layouts/add_train.fxml", Ref.language.getString("window.add_train"), GUILoader.getPrimaryStage());
        if(s != null)
            s.setResizable(true);
    }

    public void onStartTrainControl(ActionEvent event) {
        if(this.trains.getSelectionModel() == null || this.trains.getSelectionModel().getSelectedItem() == null) {
            Notifications.create().darkStyle().title(Ref.language.getString("window.error")).text(Ref.language.getString("error.no_train_selected")).showError();
            return;
        }

        String s = this.trains.getSelectionModel().getSelectedItem().getValue();
        Train t = null;
        for(Train train: LocoNet.getInstance().getTrains())
            if(train.equals(s))
                t = train;
        if(t == null)
            Notifications.create().darkStyle().title(Ref.language.getString("window.error")).text(Ref.language.getString("error.no_train_selected")).showError();
        else {
            final Train finalT = t;
            Util.runNext(() -> {
                while(GuiControlTrain.toAdd != null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored) { }
                }
                GuiControlTrain.toAdd = finalT;
                Platform.runLater(() -> Util.openWindow("/assets/layouts/control_train.fxml", finalT.getName(), null));
            });
        }
    }

    public void onFullscreen(ActionEvent event) {
        Config.fullScreen = !Config.fullScreen;
        ConfigManager.getInstance().onConfigChanged("fullScreen.text");
    }

    public void onClose(ActionEvent event) {
        Util.onClose(event);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GuiMain.instance = this;

        ConfigManager.getInstance().createMenuTree(tree, config);

        for(int y = 0; y < 100; y++) {
            HBox box = new HBox();
            railroadCells.put(box, new ArrayList<>());
            for(int x = 0; x < 100; x++) {
                ImageView view = new ImageView(shouldHide.isSelected() ? RailroadImages.EMPTY : RailroadImages.EMPTY_2);
                view.setFitHeight(32);
                view.setFitWidth(32);
                view.setX(32);
                view.setY(32);
                view.setLayoutX(32);
                view.setLayoutY(32);
                view.setSmooth(true);
                int finalY = y;
                int finalX = x;
                view.setOnMouseClicked(mouseEvent -> {
                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        switch (mode) {
                            case STRAIGHT:
                                this.setImage(finalX, finalY, getImageForModeAndRotation(false));
                                start = finalX;
                                break;
                            case CURVE:
                            case SWITCH_R:
                            case SWITCH_L:
                            case SWITCH_L_R:
                            case DELETE:
                                this.setImage(finalX, finalY, getImageForModeAndRotation(false));
                                break;
                            case CUT:
                                if(RailroadImages.HOVER_IMAGES.contains(view.getImage()) ||
                                        view.getImage().equals(RailroadImages.STRAIGHT_HORIZONTAL) ||
                                        view.getImage().equals(RailroadImages.STRAIGHT_VERTICAL)) {
                                    this.setImage(finalX, finalY, getImageForModeAndRotation(false));
                                }
                                break;
                        }
                    } else if(mouseEvent.getButton().equals(MouseButton.MIDDLE)) {
                        this.onNextRotation(null);
                    } else if(mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        switch (mode) {
                            case STRAIGHT:
                                this.onSetModeToDelete(null);
                                break;
                            case CURVE:
                                this.onSetModeToStraight(null);
                                break;
                            case SWITCH_R:
                                this.onSetModeToCurve(null);
                                break;
                            case SWITCH_L:
                                this.onSetModeToRSwitch(null);
                                break;
                            case SWITCH_L_R:
                                this.onSetModeToLSwitch(null);
                                break;
                            case DELETE:
                                this.onSetModeToCut(null);
                                break;
                            case CUT:
                                this.onSetModeToLRSwitch(null);
                                break;
                        }
                    }
                    if(view.getImage().equals(RailroadImages.EMPTY) || view.getImage().equals(RailroadImages.EMPTY_2) || RailroadImages.HOVER_IMAGES.contains(view.getImage()))
                        view.setImage(this.hover_image);
                });
                view.setOnMouseEntered(mouseEvent -> {
                    if(view.getImage().equals(RailroadImages.EMPTY) || view.getImage().equals(RailroadImages.EMPTY_2))
                        view.setImage(this.hover_image);
                    if(this.mode.equals(EditMode.DELETE) || this.mode.equals(EditMode.CUT))
                        view.setCursor(Cursor.HAND);
                    else
                        view.setCursor(Cursor.DEFAULT);
                });
                view.setOnMouseExited(mouseEvent -> {
                    if(RailroadImages.HOVER_IMAGES.contains(view.getImage()))
                        view.setImage(shouldHide.isSelected() ? RailroadImages.EMPTY : RailroadImages.EMPTY_2);
                });

                view.setPickOnBounds(true);
                box.getChildren().add(view);
                railroadCells.get(box).add(view);
            }
            this.railroadLines.add(box);
            this.railroadSection.getChildren().add(box);

        }

        for(Theme t: Theme.values()) {
            MenuItem theme = new MenuItem(Ref.language.getString("theme.text." + t.name()));
            theme.setOnAction(action -> {
                Config.theme = t.name();
                ConfigManager.getInstance().onConfigChanged("theme.text");
            });
            this.theme.getItems().add(theme);
        }

        for(Language l: Language.values()) {
            MenuItem language = new MenuItem(Ref.language.getString("language.text." + l.name()));
            language.setOnAction(action -> {
                Config.language = l.name();
                ConfigManager.getInstance().onConfigChanged("language.text");
            });
            this.language.getItems().add(language);
        }

        LocoNetMessageReceiver.getInstance().registerListener(message -> {
            if(message instanceof TrainSlotMessage) {
                this.updateTrains();
            }
        });
        trainsRoot = new TreeItem<>(Ref.language.getString("label.trains"));
        trains.setRoot(this.trainsRoot);
        trains.setShowRoot(false);
        this.updateTrains();
    }

    public void updateTrains() {
        trainsRoot.getChildren().clear();
        for(Train t: LocoNet.getInstance().getTrains()) {
            TreeItem<String> train = new TreeItem<>(t.getName());
            trainsRoot.getChildren().add(train);
        }
    }

    public void calculateConnection(int fromX, int fromY, int toX, int toY, int startDirection) {
        switch(startDirection) {
            case 0:
                if(fromX > toX) {
                    if(fromY < toY) {
                        this.setImage(fromX + 1, fromY, RailroadImages.CURVE_SOUTH_WEST);

                        this.connectColumn(fromY + 1, toY, fromX + 1);

                        this.setImage(fromX + 1, toY, RailroadImages.CURVE_NORTH_WEST);

                        this.connectRow(fromX, toX, toY);

                        this.setImage(toX, toY, RailroadImages.STRAIGHT_HORIZONTAL);
                    } else if(fromY > toY) {
                        this.setImage(fromX + 1, fromY, RailroadImages.CURVE_NORTH_WEST);

                        this.connectColumn(fromY - 1, toY, fromX + 1);

                        this.setImage(fromX + 1, toY, RailroadImages.CURVE_SOUTH_WEST);

                        this.connectRow(fromX, toX, toY);

                        this.setImage(toX, toY, RailroadImages.STRAIGHT_HORIZONTAL);
                    } else {
                        this.setImage(fromX + 1, fromY, RailroadImages.CURVE_SOUTH_WEST);

                        this.setImage(fromX + 1, fromY + 1, RailroadImages.CURVE_NORTH_WEST);

                        this.connectRow(fromX, toX, fromY + 1);

                        this.setImage(toX, fromY + 1, RailroadImages.CURVE_NORTH_EAST);

                        this.setImage(toX, fromY, RailroadImages.STRAIGHT_VERTICAL);
                    }
                } else if(fromX < toX) {
                    this.connectRow(fromX, toX, fromY);

                    HBox box1 = this.railroadLines.get(fromY);
                    ArrayList<ImageView> cells = this.railroadCells.get(box1);
                    if(fromY < toY) {
                        cells.get(toX).setImage(RailroadImages.CURVE_SOUTH_WEST);
                        this.connectColumn(fromY + 1, toY, toX);
                        this.setImage(toX, toY, RailroadImages.STRAIGHT_VERTICAL);
                    } else if(fromY > toY) {
                        cells.get(toX).setImage(RailroadImages.CURVE_NORTH_WEST);
                        this.connectColumn(fromY - 1, toY, toX);
                        this.setImage(toX, toY, RailroadImages.STRAIGHT_VERTICAL);
                    } else {
                        cells.get(toX).setImage(RailroadImages.STRAIGHT_HORIZONTAL);
                    }
                } else {

                }
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }
    }

    public void setImage(int x, int y, Image i) {
        HBox box1 = this.railroadLines.get(y);
        ArrayList<ImageView> cells = this.railroadCells.get(box1);
        cells.get(x).setImage(i);
    }

    public void connectColumn(int from, int to, int row) {
        if(from < to) {
            for (int i = from; i < to; i++) {
                HBox box1 = this.railroadLines.get(i);
                ArrayList<ImageView> cells = this.railroadCells.get(box1);
                cells.get(row).setImage(RailroadImages.STRAIGHT_VERTICAL);
            }
        } else if(from > to) {
            for (int i = from; i > to; i--) {
                HBox box1 = this.railroadLines.get(i);
                ArrayList<ImageView> cells = this.railroadCells.get(box1);
                cells.get(row).setImage(RailroadImages.STRAIGHT_VERTICAL);
            }
        }
    }

    public void connectRow(int from, int to, int column) {
        HBox box1 = this.railroadLines.get(column);
        ArrayList<ImageView> cells = this.railroadCells.get(box1);
        if(from < to) {
            for (int i = from; i < to; i++) {
                cells.get(i).setImage(RailroadImages.STRAIGHT_HORIZONTAL);
            }
        } else if(from > to) {
            for (int i = from; i > to; i--) {
                cells.get(i).setImage(RailroadImages.STRAIGHT_HORIZONTAL);
            }
        }
    }
}
