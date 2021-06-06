package de.noisruker.gui;

import de.noisruker.loconet.LocoNet;
import de.noisruker.main.GUILoader;
import de.noisruker.railroad.Position;
import de.noisruker.railroad.RailRotation;
import de.noisruker.railroad.elements.*;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class GuiCreateRailroad implements Initializable {

    private static GuiMain instance = null;

    public static GuiMain getInstance() {
        return instance;
    }

    public int startX = -1, startY = -1, mouseX = 0, mouseY = 0;

    @FXML
    public ToggleButton straight, curve, RSwitch, LSwitch, LRSwitch, delete, sensor, directional, shouldHide, end, signal, crossing;

    @FXML
    public VBox railroadSection;

    public ArrayList<HBox> railroadLines = new ArrayList<>();
    public HashMap<HBox, ArrayList<ImageView>> railroadCells = new HashMap<>();

    public EditMode mode = EditMode.STRAIGHT;

    private enum EditMode {
        STRAIGHT,
        CURVE,
        SWITCH_R,
        SWITCH_L,
        SWITCH_L_R,
        SENSOR,
        DIRECTIONAL,
        END,
        SIGNAL,
        CROSSING,
        DELETE
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

    public Image getImageForModeAndRotation(boolean asHover) {
        switch (mode) {
            case STRAIGHT:
                switch (rotation) {
                    case NORTH:
                    case SOUTH:
                        return asHover ? RailroadImages.STRAIGHT_VERTICAL_HOVER : RailroadImages.STRAIGHT_VERTICAL;
                    case WEST:
                    case EAST:
                        return asHover ? RailroadImages.STRAIGHT_HORIZONTAL_HOVER : RailroadImages.STRAIGHT_HORIZONTAL;
                }
            case SIGNAL:
                switch (rotation) {
                    case NORTH:
                    case SOUTH:
                        return asHover ? RailroadImages.SIGNAL_VERTICAL_HOVER : RailroadImages.SIGNAL_VERTICAL;
                    case WEST:
                    case EAST:
                        return asHover ? RailroadImages.SIGNAL_HORIZONTAL_HOVER : RailroadImages.SIGNAL_HORIZONTAL;
                }
            case DIRECTIONAL:
                switch (rotation) {
                    case NORTH:
                        return asHover ? RailroadImages.STRAIGHT_NORTH_HOVER : RailroadImages.STRAIGHT_NORTH;
                    case SOUTH:
                        return asHover ? RailroadImages.STRAIGHT_SOUTH_HOVER : RailroadImages.STRAIGHT_SOUTH;
                    case WEST:
                        return asHover ? RailroadImages.STRAIGHT_WEST_HOVER : RailroadImages.STRAIGHT_WEST;
                    case EAST:
                        return asHover ? RailroadImages.STRAIGHT_EAST_HOVER : RailroadImages.STRAIGHT_EAST;
                }
            case CURVE:
                switch (rotation) {
                    case NORTH:
                        return asHover ? RailroadImages.CURVE_NORTH_EAST_HOVER : RailroadImages.CURVE_NORTH_EAST;
                    case SOUTH:
                        return asHover ? RailroadImages.CURVE_SOUTH_WEST_HOVER : RailroadImages.CURVE_SOUTH_WEST;
                    case WEST:
                        return asHover ? RailroadImages.CURVE_NORTH_WEST_HOVER : RailroadImages.CURVE_NORTH_WEST;
                    case EAST:
                        return asHover ? RailroadImages.CURVE_SOUTH_EAST_HOVER : RailroadImages.CURVE_SOUTH_EAST;
                }
            case SWITCH_R:
                switch (rotation) {
                    case NORTH:
                        return asHover ? RailroadImages.SWITCH_NORTH_2_HOVER : RailroadImages.SWITCH_NORTH_2;
                    case SOUTH:
                        return asHover ? RailroadImages.SWITCH_SOUTH_2_HOVER : RailroadImages.SWITCH_SOUTH_2;
                    case WEST:
                        return asHover ? RailroadImages.SWITCH_WEST_2_HOVER : RailroadImages.SWITCH_WEST_2;
                    case EAST:
                        return asHover ? RailroadImages.SWITCH_EAST_2_HOVER : RailroadImages.SWITCH_EAST_2;
                }
            case SWITCH_L:
                switch (rotation) {
                    case NORTH:
                        return asHover ? RailroadImages.SWITCH_NORTH_1_HOVER : RailroadImages.SWITCH_NORTH_1;
                    case SOUTH:
                        return asHover ? RailroadImages.SWITCH_SOUTH_1_HOVER : RailroadImages.SWITCH_SOUTH_1;
                    case WEST:
                        return asHover ? RailroadImages.SWITCH_WEST_1_HOVER : RailroadImages.SWITCH_WEST_1;
                    case EAST:
                        return asHover ? RailroadImages.SWITCH_EAST_1_HOVER : RailroadImages.SWITCH_EAST_1;
                }
            case SWITCH_L_R:
                switch (rotation) {
                    case NORTH:
                        return asHover ? RailroadImages.SWITCH_NORTH_3_HOVER : RailroadImages.SWITCH_NORTH_3;
                    case SOUTH:
                        return asHover ? RailroadImages.SWITCH_SOUTH_3_HOVER : RailroadImages.SWITCH_SOUTH_3;
                    case WEST:
                        return asHover ? RailroadImages.SWITCH_WEST_3_HOVER : RailroadImages.SWITCH_WEST_3;
                    case EAST:
                        return asHover ? RailroadImages.SWITCH_EAST_3_HOVER : RailroadImages.SWITCH_EAST_3;
                }
            case SENSOR:
                switch (rotation) {
                    case NORTH:
                    case SOUTH:
                        return asHover ? RailroadImages.STRAIGHT_SENSOR_VERTICAL_HOVER : RailroadImages.STRAIGHT_SENSOR_VERTICAL;
                    case WEST:
                    case EAST:
                        return asHover ? RailroadImages.STRAIGHT_SENSOR_HORIZONTAL_HOVER : RailroadImages.STRAIGHT_SENSOR_HORIZONTAL;
                }
            case END:
                switch (rotation) {
                    case NORTH:
                        return asHover ? RailroadImages.END_NORTH_HOVER : RailroadImages.END_NORTH;
                    case SOUTH:
                        return asHover ? RailroadImages.END_SOUTH_HOVER : RailroadImages.END_SOUTH;
                    case WEST:
                        return asHover ? RailroadImages.END_WEST_HOVER : RailroadImages.END_WEST;
                    case EAST:
                        return asHover ? RailroadImages.END_EAST_HOVER : RailroadImages.END_EAST;
                }
            case CROSSING:
                return asHover ? RailroadImages.CROSSING_HOVER : RailroadImages.CROSSING;
        }

        if(shouldHide == null)
            return RailroadImages.EMPTY_2;
        return shouldHide.isSelected() ? RailroadImages.EMPTY : RailroadImages.EMPTY_2;
    }

    public void onHideChanged(ActionEvent event) {
        this.railroadCells.forEach((key, list) -> {
            list.forEach(image -> {
                if (image.getImage().equals(RailroadImages.EMPTY) || image.getImage().equals(RailroadImages.EMPTY_2))
                    image.setImage(shouldHide.isSelected() ? RailroadImages.EMPTY : RailroadImages.EMPTY_2);
            });
        });
        this.hover_image = this.getImageForModeAndRotation(true);
    }

    public void onSetModeToStraight(ActionEvent event) {
        straight.setSelected(true);
        curve.setSelected(false);
        RSwitch.setSelected(false);
        LSwitch.setSelected(false);
        LRSwitch.setSelected(false);
        delete.setSelected(false);
        sensor.setSelected(false);
        end.setSelected(false);
        directional.setSelected(false);
        signal.setSelected(false);
        crossing.setSelected(false);
        mode = EditMode.STRAIGHT;
        this.hover_image = getImageForModeAndRotation(true);
    }

    public void onSetModeToCrossing(ActionEvent event) {
        straight.setSelected(false);
        curve.setSelected(false);
        RSwitch.setSelected(false);
        LSwitch.setSelected(false);
        LRSwitch.setSelected(false);
        delete.setSelected(false);
        sensor.setSelected(false);
        end.setSelected(false);
        directional.setSelected(false);
        signal.setSelected(false);
        crossing.setSelected(true);
        mode = EditMode.CROSSING;
        this.hover_image = getImageForModeAndRotation(true);
    }

    public void onSetModeToCurve(ActionEvent event) {
        curve.setSelected(true);
        straight.setSelected(false);
        RSwitch.setSelected(false);
        LSwitch.setSelected(false);
        LRSwitch.setSelected(false);
        delete.setSelected(false);
        sensor.setSelected(false);
        end.setSelected(false);
        directional.setSelected(false);
        signal.setSelected(false);
        crossing.setSelected(false);
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
        sensor.setSelected(false);
        end.setSelected(false);
        directional.setSelected(false);
        signal.setSelected(false);
        crossing.setSelected(false);
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
        sensor.setSelected(false);
        end.setSelected(false);
        directional.setSelected(false);
        signal.setSelected(false);
        crossing.setSelected(false);
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
        sensor.setSelected(false);
        end.setSelected(false);
        directional.setSelected(false);
        signal.setSelected(false);
        crossing.setSelected(false);
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
        sensor.setSelected(false);
        end.setSelected(false);
        directional.setSelected(false);
        signal.setSelected(false);
        crossing.setSelected(false);
        mode = EditMode.DELETE;
        this.hover_image = getImageForModeAndRotation(true);
    }

    public void onSetModeToSensor(ActionEvent event) {
        sensor.setSelected(true);
        curve.setSelected(false);
        straight.setSelected(false);
        RSwitch.setSelected(false);
        LSwitch.setSelected(false);
        LRSwitch.setSelected(false);
        delete.setSelected(false);
        end.setSelected(false);
        directional.setSelected(false);
        signal.setSelected(false);
        crossing.setSelected(false);
        mode = EditMode.SENSOR;
        this.hover_image = getImageForModeAndRotation(true);
    }

    public void onSetModeToDirectional(ActionEvent event) {
        sensor.setSelected(false);
        curve.setSelected(false);
        straight.setSelected(false);
        RSwitch.setSelected(false);
        LSwitch.setSelected(false);
        LRSwitch.setSelected(false);
        delete.setSelected(false);
        end.setSelected(false);
        directional.setSelected(true);
        signal.setSelected(false);
        crossing.setSelected(false);
        mode = EditMode.DIRECTIONAL;
        this.hover_image = getImageForModeAndRotation(true);
    }

    public void onSetModeToEnd(ActionEvent event) {
        sensor.setSelected(false);
        curve.setSelected(false);
        straight.setSelected(false);
        RSwitch.setSelected(false);
        LSwitch.setSelected(false);
        LRSwitch.setSelected(false);
        delete.setSelected(false);
        end.setSelected(true);
        directional.setSelected(false);
        signal.setSelected(false);
        crossing.setSelected(false);
        mode = EditMode.END;
        this.hover_image = getImageForModeAndRotation(true);
    }

    public void onSetModeToSignal(ActionEvent event) {
        sensor.setSelected(false);
        curve.setSelected(false);
        straight.setSelected(false);
        RSwitch.setSelected(false);
        LSwitch.setSelected(false);
        LRSwitch.setSelected(false);
        delete.setSelected(false);
        end.setSelected(false);
        directional.setSelected(false);
        crossing.setSelected(false);
        signal.setSelected(true);
        mode = EditMode.SIGNAL;
        this.hover_image = getImageForModeAndRotation(true);
    }

    public void onNext(ActionEvent event) {
        if (!isRailroadValid()) {
            Notifications.create().darkStyle().title(Ref.language.getString("window.error")).text(Ref.language.getString("error.invalid_railroad")).showError();
            return;
        }

        Sensor.getAllSensors().clear();
        Switch.getAllSwitches().clear();
        AbstractRailroadElement.clearElements();

        final AbstractRailroadElement[][] railroadElements = this.getRailroad();
        new Thread(() -> {
            while (openWindows != 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
            LocoNet.getRailroad().applyRailroad(railroadElements);
        }).start();
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }

    public AbstractRailroadElement[][] getRailroad() {
        AbstractRailroadElement[][] railroadElements = new AbstractRailroadElement[100][100];
        Util.prepareNewRailroad();
        for (int y = 0; y < 100; y++) {
            HBox box1 = this.railroadLines.get(y);
            ArrayList<ImageView> cells = this.railroadCells.get(box1);
            for (int x = 0; x < 100; x++) {
                if (cells.get(x).getImage().equals(RailroadImages.STRAIGHT_HORIZONTAL)) {
                    railroadElements[x][y] = new RailroadLine(RailRotation.WEST, new Position(x, y));
                } else if (cells.get(x).getImage().equals(RailroadImages.STRAIGHT_VERTICAL)) {
                    railroadElements[x][y] = new RailroadLine(RailRotation.NORTH, new Position(x, y));
                } else if (cells.get(x).getImage().equals(RailroadImages.CROSSING)) {
                    railroadElements[x][y] = new Crossing(new Position(x, y), RailRotation.NORTH);
                } else if (cells.get(x).getImage().equals(RailroadImages.STRAIGHT_SENSOR_HORIZONTAL)) {
                    this.handleSensor(railroadElements, RailRotation.WEST, x, y);
                } else if (cells.get(x).getImage().equals(RailroadImages.STRAIGHT_SENSOR_VERTICAL)) {
                    this.handleSensor(railroadElements, RailRotation.NORTH, x, y);
                } else if (cells.get(x).getImage().equals(RailroadImages.CURVE_NORTH_EAST)) {
                    railroadElements[x][y] = new RailroadCurve(RailRotation.NORTH, new Position(x, y));
                } else if (cells.get(x).getImage().equals(RailroadImages.CURVE_NORTH_WEST)) {
                    railroadElements[x][y] = new RailroadCurve(RailRotation.WEST, new Position(x, y));
                } else if (cells.get(x).getImage().equals(RailroadImages.CURVE_SOUTH_WEST)) {
                    railroadElements[x][y] = new RailroadCurve(RailRotation.SOUTH, new Position(x, y));
                } else if (cells.get(x).getImage().equals(RailroadImages.CURVE_SOUTH_EAST)) {
                    railroadElements[x][y] = new RailroadCurve(RailRotation.EAST, new Position(x, y));
                } else if (cells.get(x).getImage().equals(RailroadImages.SWITCH_EAST_1)) {
                    this.handleSwitch(railroadElements, Switch.SwitchType.LEFT, RailRotation.EAST, x, y);
                } else if (cells.get(x).getImage().equals(RailroadImages.SWITCH_EAST_2)) {
                    this.handleSwitch(railroadElements, Switch.SwitchType.RIGHT, RailRotation.EAST, x, y);
                } else if (cells.get(x).getImage().equals(RailroadImages.SWITCH_EAST_3)) {
                    this.handleSwitch(railroadElements, Switch.SwitchType.LEFT_RIGHT, RailRotation.EAST, x, y);
                } else if (cells.get(x).getImage().equals(RailroadImages.SWITCH_WEST_1)) {
                    this.handleSwitch(railroadElements, Switch.SwitchType.LEFT, RailRotation.WEST, x, y);
                } else if (cells.get(x).getImage().equals(RailroadImages.SWITCH_WEST_2)) {
                    this.handleSwitch(railroadElements, Switch.SwitchType.RIGHT, RailRotation.WEST, x, y);
                } else if (cells.get(x).getImage().equals(RailroadImages.SWITCH_WEST_3)) {
                    this.handleSwitch(railroadElements, Switch.SwitchType.LEFT_RIGHT, RailRotation.WEST, x, y);
                } else if (cells.get(x).getImage().equals(RailroadImages.SWITCH_NORTH_1)) {
                    this.handleSwitch(railroadElements, Switch.SwitchType.LEFT, RailRotation.NORTH, x, y);
                } else if (cells.get(x).getImage().equals(RailroadImages.SWITCH_NORTH_2)) {
                    this.handleSwitch(railroadElements, Switch.SwitchType.RIGHT, RailRotation.NORTH, x, y);
                } else if (cells.get(x).getImage().equals(RailroadImages.SWITCH_NORTH_3)) {
                    this.handleSwitch(railroadElements, Switch.SwitchType.LEFT_RIGHT, RailRotation.NORTH, x, y);
                } else if (cells.get(x).getImage().equals(RailroadImages.SWITCH_SOUTH_1)) {
                    this.handleSwitch(railroadElements, Switch.SwitchType.LEFT, RailRotation.SOUTH, x, y);
                } else if (cells.get(x).getImage().equals(RailroadImages.SWITCH_SOUTH_2)) {
                    this.handleSwitch(railroadElements, Switch.SwitchType.RIGHT, RailRotation.SOUTH, x, y);
                } else if (cells.get(x).getImage().equals(RailroadImages.SWITCH_SOUTH_3)) {
                    this.handleSwitch(railroadElements, Switch.SwitchType.LEFT_RIGHT, RailRotation.SOUTH, x, y);
                } else if (cells.get(x).getImage().equals(RailroadImages.END_NORTH)) {
                    railroadElements[x][y] = new RailroadEnd(RailRotation.NORTH, new Position(x, y));
                } else if (cells.get(x).getImage().equals(RailroadImages.END_SOUTH)) {
                    railroadElements[x][y] = new RailroadEnd(RailRotation.SOUTH, new Position(x, y));
                } else if (cells.get(x).getImage().equals(RailroadImages.END_EAST)) {
                    railroadElements[x][y] = new RailroadEnd(RailRotation.EAST, new Position(x, y));
                } else if (cells.get(x).getImage().equals(RailroadImages.END_WEST)) {
                    railroadElements[x][y] = new RailroadEnd(RailRotation.WEST, new Position(x, y));
                } else if (cells.get(x).getImage().equals(RailroadImages.STRAIGHT_NORTH)) {
                    railroadElements[x][y] = new RailroadDirectionalLine(RailRotation.NORTH, new Position(x, y));
                } else if (cells.get(x).getImage().equals(RailroadImages.STRAIGHT_SOUTH)) {
                    railroadElements[x][y] = new RailroadDirectionalLine(RailRotation.SOUTH, new Position(x, y));
                } else if (cells.get(x).getImage().equals(RailroadImages.STRAIGHT_EAST)) {
                    railroadElements[x][y] = new RailroadDirectionalLine(RailRotation.EAST, new Position(x, y));
                } else if (cells.get(x).getImage().equals(RailroadImages.STRAIGHT_WEST)) {
                    railroadElements[x][y] = new RailroadDirectionalLine(RailRotation.WEST, new Position(x, y));
                } else if (cells.get(x).getImage().equals(RailroadImages.SIGNAL_HORIZONTAL)) {
                    this.handleSignal(railroadElements, RailRotation.WEST, x, y);
                } else if (cells.get(x).getImage().equals(RailroadImages.SIGNAL_VERTICAL)) {
                    this.handleSignal(railroadElements, RailRotation.NORTH, x, y);
                }
            }
        }
        return railroadElements;
    }

    private int openWindows = 0;

    private void handleSwitch(AbstractRailroadElement[][] railroadElements, Switch.SwitchType type, RailRotation rotation, int x, int y) {
        openWindows++;
        Util.runNext(() -> {
            this.handleAddSwitch(x, y);
            railroadElements[x][y] = new Switch(GuiEditSwitch.getSwitchAddress(), type, rotation, GuiEditSwitch.getDirection(), new Position(x, y));
            GuiEditSwitch.reset();
            openWindows--;
        });
    }

    private void handleAddSwitch(int x, int y) {
        while (GuiEditSwitch.isInUse() || GuiEditSensor.isInUse() || GuiEditSignal.isInUse()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }

        GuiEditSwitch.setSwitchToEdit(x, y, railroadLines, railroadCells);
        this.openInLoopSwitch();

        while (GuiEditSwitch.getSwitchAddress() == -1) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void handleSignal(AbstractRailroadElement[][] railroadElements, RailRotation rotation, int x, int y) {
        openWindows++;
        Util.runNext(() -> {
            this.handleAddSignal(x, y);
            railroadElements[x][y] = new Signal(GuiEditSignal.getSignalAddress(), rotation, new Position(x, y));
            GuiEditSignal.reset();
            openWindows--;
        });
    }

    private void handleAddSignal(int x, int y) {
        while (GuiEditSwitch.isInUse() || GuiEditSensor.isInUse() || GuiEditSignal.isInUse()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }

        GuiEditSignal.setSignalToEdit(x, y, railroadLines, railroadCells);
        this.openInLoopSignal();

        while (GuiEditSignal.getSignalAddress() == -1) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void openInLoopSignal() {
        Platform.runLater(() -> {
            Stage s = Util.openWindow("/assets/layouts/create_signal.fxml", Ref.language.getString("window.railroad"), GUILoader.getPrimaryStage());
            if (s == null)
                return;

            s.setAlwaysOnTop(true);
            s.setOnCloseRequest(windowEvent -> this.openInLoopSignal());
        });
    }

    private void handleSensor(AbstractRailroadElement[][] railroadElements, RailRotation rotation, int x, int y) {
        openWindows++;
        Util.runNext(() -> {
            this.handleAddSensor(x, y);
            railroadElements[x][y] = new Sensor(GuiEditSensor.getSensorAddress(), false, new Position(x, y), rotation);
            GuiEditSensor.reset();
            openWindows--;
        });
    }

    private void handleAddSensor(int x, int y) {
        while (GuiEditSensor.isInUse() || GuiEditSwitch.isInUse() || GuiEditSignal.isInUse()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }

        GuiEditSensor.setSensorToEdit(x, y, railroadLines, railroadCells);
        this.openInLoopSensor();

        while (GuiEditSensor.getSensorAddress() == -1) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void openInLoopSensor() {
        Platform.runLater(() -> {
            Stage s = Util.openWindow("/assets/layouts/create_sensor.fxml", Ref.language.getString("window.railroad"), GUILoader.getPrimaryStage());
            if (s == null)
                return;

            s.setAlwaysOnTop(true);
            s.setOnCloseRequest(windowEvent -> this.openInLoopSensor());
        });
    }

    private void openInLoopSwitch() {
        Platform.runLater(() -> {
            Stage s = Util.openWindow("/assets/layouts/create_switch.fxml", Ref.language.getString("window.railroad"), GUILoader.getPrimaryStage());
            if (s == null)
                return;

            s.setAlwaysOnTop(true);
            s.setOnCloseRequest(windowEvent -> this.openInLoopSwitch());
        });
    }

    public boolean isRailroadValid() {
        for (int y = 0; y < 100; y++) {
            HBox box1 = this.railroadLines.get(y);
            ArrayList<ImageView> cells = this.railroadCells.get(box1);
            for (int x = 0; x < 100; x++) {
                if (!checkIsValid(cells.get(x).getImage(), x, y))
                    return false;
            }
        }
        return true;
    }

    public boolean checkIsValid(Image i, int x, int y) {
        if (RailroadImages.NORTH_IMAGES.contains(i)) {
            if (!isConnectedToSouth(x, y - 1))
                return false;
        }
        if (RailroadImages.EAST_IMAGES.contains(i)) {
            if (!isConnectedToWest(x + 1, y))
                return false;
        }
        if (RailroadImages.SOUTH_IMAGES.contains(i)) {
            if (!isConnectedToNorth(x, y + 1))
                return false;
        }
        if (RailroadImages.WEST_IMAGES.contains(i)) {
            if (!isConnectedToEast(x - 1, y))
                return false;
        }

        return true;
    }

    public boolean isConnectedToNorth(int x, int y) {
        if (x > 100 || x < 0 || y > 100 || y < 0)
            return false;

        HBox box1 = this.railroadLines.get(y);
        ArrayList<ImageView> cells = this.railroadCells.get(box1);
        return RailroadImages.NORTH_IMAGES.contains(cells.get(x).getImage());
    }

    public boolean isConnectedToSouth(int x, int y) {
        if (x > 100 || x < 0 || y > 100 || y < 0)
            return false;

        HBox box1 = this.railroadLines.get(y);
        ArrayList<ImageView> cells = this.railroadCells.get(box1);
        return RailroadImages.SOUTH_IMAGES.contains(cells.get(x).getImage());
    }

    public boolean isConnectedToEast(int x, int y) {
        if (x > 100 || x < 0 || y > 100 || y < 0)
            return false;

        HBox box1 = this.railroadLines.get(y);
        ArrayList<ImageView> cells = this.railroadCells.get(box1);
        return RailroadImages.EAST_IMAGES.contains(cells.get(x).getImage());
    }

    public boolean isConnectedToWest(int x, int y) {
        if (x > 100 || x < 0 || y > 100 || y < 0)
            return false;

        HBox box1 = this.railroadLines.get(y);
        ArrayList<ImageView> cells = this.railroadCells.get(box1);
        return RailroadImages.WEST_IMAGES.contains(cells.get(x).getImage());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (int y = 0; y < 100; y++) {
            HBox box = new HBox();
            railroadCells.put(box, new ArrayList<>());
            for (int x = 0; x < 100; x++) {
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
                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        switch (mode) {
                            case STRAIGHT:
                                if (startX != -1 && startY != -1 && mouseEvent.isShiftDown()) {
                                    switch (rotation) {
                                        case NORTH:
                                        case SOUTH:
                                            this.connectColumn(startY, finalY, startX, false);
                                            break;
                                        case EAST:
                                        case WEST:
                                            this.connectRow(startX, finalX, startY, false);
                                            break;
                                    }
                                    startX = -1;
                                    startY = -1;
                                } else {
                                    this.setImage(finalX, finalY, getImageForModeAndRotation(false));
                                    startX = finalX;
                                    startY = finalY;
                                }
                                break;
                            case CURVE:
                            case SWITCH_R:
                            case SWITCH_L:
                            case SWITCH_L_R:
                            case SENSOR:
                            case END:
                            case DIRECTIONAL:
                            case SIGNAL:
                            case CROSSING:
                            case DELETE:
                                this.setImage(finalX, finalY, getImageForModeAndRotation(false));
                                break;
                        }
                    } else if (mouseEvent.getButton().equals(MouseButton.MIDDLE)) {
                        this.onNextRotation(null);
                    } else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        switch (mode) {
                            case STRAIGHT -> this.onSetModeToDelete(null);
                            case CROSSING -> this.onSetModeToStraight(null);
                            case CURVE -> this.onSetModeToCrossing(null);
                            case SWITCH_R -> this.onSetModeToCurve(null);
                            case SWITCH_L -> this.onSetModeToRSwitch(null);
                            case SWITCH_L_R -> this.onSetModeToLSwitch(null);
                            case DELETE -> this.onSetModeToEnd(null);
                            case SENSOR -> this.onSetModeToLRSwitch(null);
                            case END -> this.onSetModeToDirectional(null);
                            case DIRECTIONAL -> this.onSetModeToSensor(null);
                        }
                    }
                    if (view.getImage().equals(RailroadImages.EMPTY) || view.getImage().equals(RailroadImages.EMPTY_2) || RailroadImages.HOVER_IMAGES.contains(view.getImage()))
                        view.setImage(this.hover_image);
                });

                view.setOnScroll(scrollEvent -> {
                    if (scrollEvent.isShiftDown()) {
                        this.onNextRotation(null);
                        if (RailroadImages.HOVER_IMAGES.contains(view.getImage()))
                            view.setImage(this.hover_image);
                        scrollEvent.consume();
                    }
                });

                view.setOnMouseEntered(mouseEvent -> {
                    mouseX = finalX;
                    mouseY = finalY;

                    if (startX != -1 && startY != -1 && this.straight.isSelected()) {
                        switch (rotation) {
                            case NORTH:
                            case SOUTH:
                                for (int i = 0; i < 100; i++) {
                                    HBox box1 = this.railroadLines.get(i);
                                    ArrayList<ImageView> cells = this.railroadCells.get(box1);

                                    if (RailroadImages.HOVER_IMAGES.contains(cells.get(startX).getImage()))
                                        cells.get(startX).setImage(shouldHide.isSelected() ? RailroadImages.EMPTY : RailroadImages.EMPTY_2);
                                }
                                if (mouseEvent.isShiftDown())
                                    this.connectColumn(startY, finalY, startX, true);
                                break;
                            case EAST:
                            case WEST:
                                HBox box1 = this.railroadLines.get(startY);
                                ArrayList<ImageView> cells = this.railroadCells.get(box1);
                                for (int i = 0; i < 100; i++) {
                                    if (RailroadImages.HOVER_IMAGES.contains(cells.get(i).getImage()))
                                        cells.get(i).setImage(shouldHide.isSelected() ? RailroadImages.EMPTY : RailroadImages.EMPTY_2);
                                }

                                if (mouseEvent.isShiftDown())
                                    this.connectRow(startX, finalX, startY, true);
                                break;
                        }
                    }

                    if (view.getImage().equals(RailroadImages.EMPTY) || view.getImage().equals(RailroadImages.EMPTY_2))
                        view.setImage(this.hover_image);
                });

                view.setOnMouseExited(mouseEvent -> {
                    if (RailroadImages.HOVER_IMAGES.contains(view.getImage()))
                        view.setImage(shouldHide.isSelected() ? RailroadImages.EMPTY : RailroadImages.EMPTY_2);
                });

                view.setPickOnBounds(true);
                box.getChildren().add(view);
                railroadCells.get(box).add(view);
            }
            this.railroadLines.add(box);
            this.railroadSection.getChildren().add(box);

        }
        this.railroadSection.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.SHIFT)) {
                if (startX != -1 && startY != -1) {
                    switch (rotation) {
                        case NORTH:
                        case SOUTH:
                            for (int i = 0; i < 100; i++) {
                                HBox box1 = this.railroadLines.get(i);
                                ArrayList<ImageView> cells = this.railroadCells.get(box1);

                                if (RailroadImages.HOVER_IMAGES.contains(cells.get(startX).getImage()))
                                    cells.get(startX).setImage(shouldHide.isSelected() ? RailroadImages.EMPTY : RailroadImages.EMPTY_2);
                            }

                            this.connectColumn(startY, mouseY, startX, true);
                            break;
                        case EAST:
                        case WEST:
                            HBox box1 = this.railroadLines.get(startY);
                            ArrayList<ImageView> cells = this.railroadCells.get(box1);
                            for (int i = 0; i < 100; i++) {
                                if (RailroadImages.HOVER_IMAGES.contains(cells.get(i).getImage()))
                                    cells.get(i).setImage(shouldHide.isSelected() ? RailroadImages.EMPTY : RailroadImages.EMPTY_2);
                            }

                            this.connectRow(startX, mouseX, startY, true);
                            break;
                    }
                }
            }
        });

        this.railroadSection.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.SHIFT)) {
                if (startX != -1 && startY != -1) {
                    switch (rotation) {
                        case NORTH:
                        case SOUTH:
                            for (int i = 0; i < 100; i++) {
                                HBox box1 = this.railroadLines.get(i);
                                ArrayList<ImageView> cells = this.railroadCells.get(box1);

                                if (RailroadImages.HOVER_IMAGES.contains(cells.get(startX).getImage()))
                                    cells.get(startX).setImage(shouldHide.isSelected() ? RailroadImages.EMPTY : RailroadImages.EMPTY_2);
                            }
                            break;
                        case EAST:
                        case WEST:
                            HBox box1 = this.railroadLines.get(startY);
                            ArrayList<ImageView> cells = this.railroadCells.get(box1);
                            for (int i = 0; i < 100; i++) {
                                if (RailroadImages.HOVER_IMAGES.contains(cells.get(i).getImage()))
                                    cells.get(i).setImage(shouldHide.isSelected() ? RailroadImages.EMPTY : RailroadImages.EMPTY_2);
                            }
                            break;
                    }
                }
            }
        });
    }

    public void calculateConnection(int fromX, int fromY, int toX, int toY, int startDirection) {
        switch (startDirection) {
            case 0:
                if (fromX > toX) {
                    if (fromY < toY) {
                        this.setImage(fromX + 1, fromY, RailroadImages.CURVE_SOUTH_WEST);

                        this.connectColumn(fromY + 1, toY, fromX + 1, false);

                        this.setImage(fromX + 1, toY, RailroadImages.CURVE_NORTH_WEST);

                        this.connectRow(fromX, toX, toY, false);

                        this.setImage(toX, toY, RailroadImages.STRAIGHT_HORIZONTAL);
                    } else if (fromY > toY) {
                        this.setImage(fromX + 1, fromY, RailroadImages.CURVE_NORTH_WEST);

                        this.connectColumn(fromY - 1, toY, fromX + 1, false);

                        this.setImage(fromX + 1, toY, RailroadImages.CURVE_SOUTH_WEST);

                        this.connectRow(fromX, toX, toY, false);

                        this.setImage(toX, toY, RailroadImages.STRAIGHT_HORIZONTAL);
                    } else {
                        this.setImage(fromX + 1, fromY, RailroadImages.CURVE_SOUTH_WEST);

                        this.setImage(fromX + 1, fromY + 1, RailroadImages.CURVE_NORTH_WEST);

                        this.connectRow(fromX, toX, fromY + 1, false);

                        this.setImage(toX, fromY + 1, RailroadImages.CURVE_NORTH_EAST);

                        this.setImage(toX, fromY, RailroadImages.STRAIGHT_VERTICAL);
                    }
                } else if (fromX < toX) {
                    this.connectRow(fromX, toX, fromY, false);

                    HBox box1 = this.railroadLines.get(fromY);
                    ArrayList<ImageView> cells = this.railroadCells.get(box1);
                    if (fromY < toY) {
                        cells.get(toX).setImage(RailroadImages.CURVE_SOUTH_WEST);
                        this.connectColumn(fromY + 1, toY, toX, false);
                        this.setImage(toX, toY, RailroadImages.STRAIGHT_VERTICAL);
                    } else if (fromY > toY) {
                        cells.get(toX).setImage(RailroadImages.CURVE_NORTH_WEST);
                        this.connectColumn(fromY - 1, toY, toX, false);
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

    public void connectColumn(int from, int to, int row, boolean isHover) {
        if (from < to) {
            for (int i = from; i <= to; i++) {
                HBox box1 = this.railroadLines.get(i);
                ArrayList<ImageView> cells = this.railroadCells.get(box1);
                if (isHover) {
                    if (cells.get(row).getImage().equals(RailroadImages.EMPTY) || cells.get(row).getImage().equals(RailroadImages.EMPTY_2))
                        cells.get(row).setImage(RailroadImages.STRAIGHT_VERTICAL_HOVER);
                } else
                    cells.get(row).setImage(RailroadImages.STRAIGHT_VERTICAL);
            }
        } else if (from > to) {
            for (int i = from; i >= to; i--) {
                HBox box1 = this.railroadLines.get(i);
                ArrayList<ImageView> cells = this.railroadCells.get(box1);
                if (isHover) {
                    if (cells.get(row).getImage().equals(RailroadImages.EMPTY) || cells.get(row).getImage().equals(RailroadImages.EMPTY_2))
                        cells.get(row).setImage(RailroadImages.STRAIGHT_VERTICAL_HOVER);
                } else
                    cells.get(row).setImage(RailroadImages.STRAIGHT_VERTICAL);
            }
        }
    }

    public void connectRow(int from, int to, int column, boolean isHover) {
        HBox box1 = this.railroadLines.get(column);
        ArrayList<ImageView> cells = this.railroadCells.get(box1);
        if (from < to) {
            for (int i = from; i <= to; i++) {
                if (isHover) {
                    if (cells.get(i).getImage().equals(RailroadImages.EMPTY) || cells.get(i).getImage().equals(RailroadImages.EMPTY_2))
                        cells.get(i).setImage(RailroadImages.STRAIGHT_HORIZONTAL_HOVER);
                } else
                    cells.get(i).setImage(RailroadImages.STRAIGHT_HORIZONTAL);
            }
        } else if (from > to) {
            for (int i = from; i >= to; i--) {
                if (isHover) {
                    if (cells.get(i).getImage().equals(RailroadImages.EMPTY) || cells.get(i).getImage().equals(RailroadImages.EMPTY_2))
                        cells.get(i).setImage(RailroadImages.STRAIGHT_HORIZONTAL_HOVER);
                } else
                    cells.get(i).setImage(RailroadImages.STRAIGHT_HORIZONTAL);
            }
        }
    }

}
