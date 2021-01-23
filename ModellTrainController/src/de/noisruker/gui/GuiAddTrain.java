package de.noisruker.gui;

import de.noisruker.gui.tables.BasicTrains;
import de.noisruker.util.Ref;
import de.noisruker.util.Util;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.ResourceBundle;

import static de.noisruker.loconet.LocoNet.getInstance;

public class GuiAddTrain implements Initializable {

    public static GuiAddTrain actual = null;

    @FXML
    public Spinner<Integer> address;

    @FXML
    public Label messages;

    @FXML
    public Label labelMinSpeed, labelNormalSpeed, labelMaxSpeed, error;

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

        if (error.getText().isBlank()) {
            getInstance().addSavedTrain(address.getValue().byteValue(), name.getText(), null, (byte) maxSpeed.getValue(), (byte) normalSpeed.getValue(), (byte) minSpeed.getValue(), standardDirection.isSelected());

            GuiMain.getInstance().updateTrains();
            ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

        this.minSpeed.setValue(35);

        this.maxSpeed.setValue(124);

        this.normalSpeed.setValue(80);

        this.standardDirection.setSelected(true);

        ValidationSupport support = new ValidationSupport();

        Validator<String> hasInput = (c, str) -> ValidationResult.fromErrorIf(c, Ref.language.getString("invalid.no_input"),
                str == null || str.isBlank() || Util.isNameInvalid(str, this.address.getValue()));

        support.registerValidator(this.name, true, hasInput);

        set.disableProperty().bind(support.invalidProperty());

        GuiEditTrain.train = null;

        this.address.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 255, 1));
        actual = this;
    }

    public void close(WindowEvent event) {
        Util.runNext(() -> {

            BasicTrains.getInstance().setTrains(getInstance().getTrains());
        });
        actual = null;
    }

    public void onClose(ActionEvent event) {
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
        this.close(null);
    }

}
