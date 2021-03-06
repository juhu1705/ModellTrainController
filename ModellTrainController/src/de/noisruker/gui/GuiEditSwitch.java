package de.noisruker.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class GuiEditSwitch implements Initializable {

    private static byte switchAddress = -1;
    private static boolean direction = true;
    private static int posX = -1, posY = -1;

    public static ArrayList<HBox> railroadLines = new ArrayList<>();
    public static HashMap<HBox, ArrayList<ImageView>> railroadCells = new HashMap<>();

    public static void setSwitchToEdit(int posX, int posY, ArrayList<HBox> railroadLines, HashMap<HBox, ArrayList<ImageView>> railroadCells) {
        GuiEditSwitch.posX = posX;
        GuiEditSwitch.posY = posY;
        GuiEditSwitch.railroadLines = railroadLines;
        GuiEditSwitch.railroadCells = railroadCells;
    }

    public static byte getSwitchAddress() {
        return switchAddress;
    }

    public static boolean getDirection() {
        return direction;
    }

    public static boolean isInUse() {
        return posX != -1 && posY != -1;
    }

    public static void reset() {
        switchAddress = -1;
        direction = true;
        posX = -1;
        posY = -1;
    }

    @FXML
    public VBox railroadSection;

    @FXML
    public Spinner<Integer> address;

    @FXML
    public RadioButton directionOn, directionOff;

    @FXML
    public ImageView pictureOn, pictureOff;

    public void onFinished(ActionEvent event) {
        direction = directionOn.isSelected();
        switchAddress = (byte) (address.getValue().byteValue() - (byte) 1);

        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (HBox box : railroadLines) {
            railroadSection.getChildren().add(box);
            for (ImageView i : railroadCells.get(box)) {
                i.setOnMouseEntered(mouseEvent -> {
                });
                i.setOnMouseClicked(mouseEvent -> {
                });
                i.setOnMouseExited(mouseEvent -> {
                });
                i.setImage(getNormalForm(i.getImage()));
            }
        }

        Image i = railroadCells.get(railroadLines.get(posY)).get(posX).getImage();

        pictureOn.setImage(this.getPictureOn(i));
        pictureOff.setImage(this.getPictureOff(i));

        address.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 125, 1));

        i = getHoverForm(railroadCells.get(railroadLines.get(posY)).get(posX).getImage());

        railroadCells.get(railroadLines.get(posY)).get(posX).setImage(i);
    }

    private Image getPictureOn(Image i) {
        if (i.equals(RailroadImages.SWITCH_EAST_1) || i.equals(RailroadImages.SWITCH_NORTH_1) ||
                i.equals(RailroadImages.SWITCH_WEST_1) || i.equals(RailroadImages.SWITCH_SOUTH_1) ||
                i.equals(RailroadImages.SWITCH_EAST_2) || i.equals(RailroadImages.SWITCH_NORTH_2) ||
                i.equals(RailroadImages.SWITCH_WEST_2) || i.equals(RailroadImages.SWITCH_SOUTH_2))
            return RailroadImages.STRAIGHT_VERTICAL;
        else if (i.equals(RailroadImages.SWITCH_EAST_3) || i.equals(RailroadImages.SWITCH_NORTH_3) ||
                i.equals(RailroadImages.SWITCH_WEST_3) || i.equals(RailroadImages.SWITCH_SOUTH_3))
            return RailroadImages.CURVE_SOUTH_WEST;
        return RailroadImages.EMPTY;
    }

    private Image getPictureOff(Image i) {
        if (i.equals(RailroadImages.SWITCH_EAST_1) || i.equals(RailroadImages.SWITCH_NORTH_1) ||
                i.equals(RailroadImages.SWITCH_WEST_1) || i.equals(RailroadImages.SWITCH_SOUTH_1))
            return RailroadImages.CURVE_SOUTH_WEST;
        else if (i.equals(RailroadImages.SWITCH_EAST_3) || i.equals(RailroadImages.SWITCH_NORTH_3) ||
                i.equals(RailroadImages.SWITCH_WEST_3) || i.equals(RailroadImages.SWITCH_SOUTH_3) ||
                i.equals(RailroadImages.SWITCH_EAST_2) || i.equals(RailroadImages.SWITCH_NORTH_2) ||
                i.equals(RailroadImages.SWITCH_WEST_2) || i.equals(RailroadImages.SWITCH_SOUTH_2))
            return RailroadImages.CURVE_SOUTH_EAST;
        return RailroadImages.EMPTY;
    }

    private Image getNormalForm(Image image) {
        if (image.equals(RailroadImages.SWITCH_EAST_1_HOVER))
            return RailroadImages.SWITCH_EAST_1;
        else if (image.equals(RailroadImages.SWITCH_EAST_2_HOVER))
            return RailroadImages.SWITCH_EAST_2;
        else if (image.equals(RailroadImages.SWITCH_EAST_3_HOVER))
            return RailroadImages.SWITCH_EAST_3;
        else if (image.equals(RailroadImages.SWITCH_NORTH_1_HOVER))
            return RailroadImages.SWITCH_NORTH_1;
        else if (image.equals(RailroadImages.SWITCH_NORTH_2_HOVER))
            return RailroadImages.SWITCH_NORTH_2;
        else if (image.equals(RailroadImages.SWITCH_NORTH_3_HOVER))
            return RailroadImages.SWITCH_NORTH_3;
        else if (image.equals(RailroadImages.SWITCH_SOUTH_1_HOVER))
            return RailroadImages.SWITCH_SOUTH_1;
        else if (image.equals(RailroadImages.SWITCH_SOUTH_2_HOVER))
            return RailroadImages.SWITCH_SOUTH_2;
        else if (image.equals(RailroadImages.SWITCH_SOUTH_3_HOVER))
            return RailroadImages.SWITCH_SOUTH_3;
        else if (image.equals(RailroadImages.SWITCH_WEST_1_HOVER))
            return RailroadImages.SWITCH_WEST_1;
        else if (image.equals(RailroadImages.SWITCH_WEST_2_HOVER))
            return RailroadImages.SWITCH_WEST_2;
        else if (image.equals(RailroadImages.SWITCH_WEST_3_HOVER))
            return RailroadImages.SWITCH_WEST_3;
        else if (image.equals(RailroadImages.STRAIGHT_SENSOR_HORIZONTAL_HOVER))
            return RailroadImages.STRAIGHT_SENSOR_HORIZONTAL;
        else if (image.equals(RailroadImages.STRAIGHT_SENSOR_VERTICAL_HOVER))
            return RailroadImages.STRAIGHT_SENSOR_VERTICAL;
        else if (image.equals(RailroadImages.SIGNAL_VERTICAL_HOVER))
            return RailroadImages.SIGNAL_VERTICAL;
        else if (image.equals(RailroadImages.SIGNAL_HORIZONTAL_HOVER))
            return RailroadImages.SIGNAL_HORIZONTAL;
        else if (RailroadImages.HOVER_IMAGES.contains(image))
            return RailroadImages.EMPTY_2;
        else if (image.equals(RailroadImages.EMPTY))
            return RailroadImages.EMPTY_2;

        return image;
    }

    private Image getHoverForm(Image image) {
        if (image.equals(RailroadImages.SWITCH_EAST_1))
            return RailroadImages.SWITCH_EAST_1_HOVER;
        else if (image.equals(RailroadImages.SWITCH_EAST_2))
            return RailroadImages.SWITCH_EAST_2_HOVER;
        else if (image.equals(RailroadImages.SWITCH_EAST_3))
            return RailroadImages.SWITCH_EAST_3_HOVER;
        else if (image.equals(RailroadImages.SWITCH_NORTH_1))
            return RailroadImages.SWITCH_NORTH_1_HOVER;
        else if (image.equals(RailroadImages.SWITCH_NORTH_2))
            return RailroadImages.SWITCH_NORTH_2_HOVER;
        else if (image.equals(RailroadImages.SWITCH_NORTH_3))
            return RailroadImages.SWITCH_NORTH_3_HOVER;
        else if (image.equals(RailroadImages.SWITCH_SOUTH_1))
            return RailroadImages.SWITCH_SOUTH_1_HOVER;
        else if (image.equals(RailroadImages.SWITCH_SOUTH_2))
            return RailroadImages.SWITCH_SOUTH_2_HOVER;
        else if (image.equals(RailroadImages.SWITCH_SOUTH_3))
            return RailroadImages.SWITCH_SOUTH_3_HOVER;
        else if (image.equals(RailroadImages.SWITCH_WEST_1))
            return RailroadImages.SWITCH_WEST_1_HOVER;
        else if (image.equals(RailroadImages.SWITCH_WEST_2))
            return RailroadImages.SWITCH_WEST_2_HOVER;
        else if (image.equals(RailroadImages.SWITCH_WEST_3))
            return RailroadImages.SWITCH_WEST_3_HOVER;
        return image;
    }
}
