package de.noisruker.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

public class GuiEditSignal implements Initializable {

    private static byte signalAddress = -1;
    private static int posX = -1, posY = -1;

    public static ArrayList<HBox> railroadLines = new ArrayList<>();
    public static HashMap<HBox, ArrayList<ImageView>> railroadCells = new HashMap<>();

    public static void setSignalToEdit(int posX, int posY, ArrayList<HBox> railroadLines, HashMap<HBox, ArrayList<ImageView>> railroadCells) {
        GuiEditSignal.posX = posX;
        GuiEditSignal.posY = posY;
        GuiEditSignal.railroadLines = railroadLines;
        GuiEditSignal.railroadCells = railroadCells;
    }

    public static byte getSignalAddress() {
        return signalAddress;
    }

    public static boolean isInUse() {
        return posX != -1 && posY != -1;
    }

    public static void reset() {
        signalAddress = -1;
        posX = -1;
        posY = -1;
    }

    @FXML
    public VBox railroadSection;

    @FXML
    public Spinner<Integer> address;

    public void onFinished(ActionEvent event) {
        signalAddress = (byte) (address.getValue().byteValue() - 1);

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

        address.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 125, 1));

        Image i = getHoverForm(railroadCells.get(railroadLines.get(posY)).get(posX).getImage());

        railroadCells.get(railroadLines.get(posY)).get(posX).setImage(i);
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
        if (image.equals(RailroadImages.SIGNAL_HORIZONTAL))
            return RailroadImages.SIGNAL_HORIZONTAL_HOVER;
        else if (image.equals(RailroadImages.SIGNAL_VERTICAL))
            return RailroadImages.SIGNAL_VERTICAL_HOVER;
        return image;
    }
}
