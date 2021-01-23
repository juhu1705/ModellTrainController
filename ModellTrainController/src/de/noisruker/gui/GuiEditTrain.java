package de.noisruker.gui;

import de.noisruker.loconet.LocoNet;
import de.noisruker.railroad.Train;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiEditTrain implements Initializable {

    public static Train train = null;
    private Train t;

    @FXML
    public Label header, labelMinSpeed, labelNormalSpeed, labelMaxSpeed, error;

    @FXML
    public TextField name;

    @FXML
    public Slider minSpeed, normalSpeed, maxSpeed;

    @FXML
    public ToggleSwitch standardDirection;

    @FXML
    public Button set;

    public void onSet(ActionEvent e) {
        error.setText("");

        if (this.minSpeed.getValue() > this.maxSpeed.getValue())
            error.setText(Ref.language.getString("label.error.min_speed"));

        if (this.normalSpeed.getValue() < this.minSpeed.getValue())
            error.setText(Ref.language.getString("label.error.normal_speed"));

        if (this.maxSpeed.getValue() < this.normalSpeed.getValue())
            error.setText(Ref.language.getString("label.error.max_speed"));

        if (error.getText().isBlank())
            if (this.t != null)
                for (Train t : LocoNet.getInstance().getTrains()) {
                    Ref.LOGGER.info(t.toString());
                    if (t.getAddress() == this.t.getAddress())
                        t.setParameters(name.getText(), (byte) this.maxSpeed.getValue(), (byte) this.normalSpeed.getValue(), (byte) this.minSpeed.getValue(), this.standardDirection.isSelected());
                }
        this.t = null;

        GuiMain.getInstance().updateTrains();

        ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (GuiEditTrain.train == null) {
            this.header.setText(Ref.language.getString("label.no_train_found"));
            return;
        }
        this.t = train;

        this.header.setText(Ref.language.getString("label.train") + " " + this.t.getAddress());

        this.name.setText(this.t.getName());

        this.minSpeed.valueProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) -> {
            this.labelMinSpeed.setText(Integer.toString(newVal.intValue()));
            this.maxSpeed.setMin(newVal.intValue() + 1);
            this.normalSpeed.setMin(newVal.intValue());
        });

        this.normalSpeed.valueProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) -> {
            this.labelNormalSpeed.setText(Integer.toString(newVal.intValue()));
        });

        this.maxSpeed.valueProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) -> {
            this.labelMaxSpeed.setText(Integer.toString(newVal.intValue()));
            this.minSpeed.setMax(newVal.intValue() - 1);
            this.normalSpeed.setMax(newVal.intValue());
        });

        this.minSpeed.setValue(this.t.getMinSpeed());

        this.maxSpeed.setValue(this.t.getMaxSpeed());

        this.normalSpeed.setValue(this.t.getNormalSpeed());

        this.standardDirection.setSelected(this.t.standardForward());

        ValidationSupport support = new ValidationSupport();

        Validator<String> hasInput = (c, str) -> ValidationResult.fromErrorIf(c, Ref.language.getString("invalid.no_input"),
                str == null || str.isBlank() || Util.isNameInvalid(str, this.t));

        support.registerValidator(this.name, true, hasInput);

        set.disableProperty().bind(support.invalidProperty());

        GuiEditTrain.train = null;
    }
}
