package de.noisruker.gui;

import de.noisruker.loconet.LocoNet;
import de.noisruker.railroad.Train;
import de.noisruker.util.Ref;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class GuiEditTrain implements Initializable {

    public static Train train = null;

    @FXML
    public Label header, labelMinSpeed, labelNormalSpeed, labelMaxSpeed, error;

    @FXML
    public Slider minSpeed, normalSpeed, maxSpeed;

    @FXML
    public CheckBox standardDirection;

    public void onSet(ActionEvent e) {
        error.setText("");

        if(this.minSpeed.getValue() > this.maxSpeed.getValue())
            error.setText(Ref.language.getString("label.error.min_speed"));

        if(this.normalSpeed.getValue() < this.minSpeed.getValue())
            error.setText(Ref.language.getString("label.error.normal_speed"));

        if(this.maxSpeed.getValue() < this.normalSpeed.getValue())
            error.setText(Ref.language.getString("label.error.max_speed"));

        if(!error.getText().isBlank())

        if(GuiEditTrain.train != null) {
            for(Train t: LocoNet.getInstance().getTrains())
                if(t.getAddress() == GuiEditTrain.train.getAddress())
                    t.setParameters((byte) this.maxSpeed.getValue(), (byte) this.normalSpeed.getValue(), (byte) this.minSpeed.getValue(), this.standardDirection.isSelected());
        }

        GuiEditTrain.train = null;
        ((Stage)((Button) e.getSource()).getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(GuiEditTrain.train == null) {
            this.header.setText(Ref.language.getString("label.no_train_found"));
            return;
        }

        this.header.setText(Ref.language.getString("label.train") + " " + GuiEditTrain.train.getAddress());

        this.minSpeed.valueProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) -> {
            this.labelMinSpeed.setText(Integer.toString(newVal.intValue()));
            this.maxSpeed.setMin(newVal.intValue());
            this.normalSpeed.setMin(newVal.intValue());
        });

        this.normalSpeed.valueProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) -> {
            this.labelNormalSpeed.setText(Integer.toString(newVal.intValue()));
        });

        this.maxSpeed.valueProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) -> {
            this.labelMaxSpeed.setText(Integer.toString(newVal.intValue()));
            this.minSpeed.setMax(newVal.intValue());
            this.normalSpeed.setMax(newVal.intValue());
        });

        this.minSpeed.setValue(GuiEditTrain.train.getMinSpeed());
        this.normalSpeed.setValue(GuiEditTrain.train.getNormalSpeed());
        this.maxSpeed.setValue(GuiEditTrain.train.getMaxSpeed());

        this.standardDirection.setSelected(GuiEditTrain.train.standardForward());
    }
}
