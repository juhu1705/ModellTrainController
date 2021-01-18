package de.noisruker.railroad.conditions;

import de.noisruker.util.Ref;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

public class TimeCondition extends AbstractDrivingConditions {

    private int timeToWait, time;
    HBox condition = new HBox();

    public TimeCondition(int timeToWait) {
        this.timeToWait = timeToWait;
        this.time = this.timeToWait;
    }

    @Override
    public boolean isConditionTrue() {
        return time == 0;
    }

    @Override
    public void updateCondition() {
        if(timeToWait > 0)
            time--;
        else
            time = 0;
        Platform.runLater(this::updateGui);
    }

    private void updateGui() {
        if(super.isInCheck) {
            Ref.LOGGER.info("Time left " + this.time);
            if(this.time > 0 && !condition.getStyleClass().contains("in-progress"))
                condition.getStyleClass().add("in-progress");
            else if(this.time == 0 && !condition.getStyleClass().contains("checked"))
                condition.getStyleClass().add("checked");
        } else {
            condition.getStyleClass().remove("in-progress");
            condition.getStyleClass().remove("checked");
        }

    }

    @Override
    public void start() {
        time = timeToWait * 2;
    }

    @Override
    public void addToGui(VBox box) {
        this.condition.getChildren().clear();
        this.condition.setPadding(new Insets(10));
        Button waitingTime = new Button(timeToWait + " s");

        Spinner<Integer> spinner = new Spinner<>();
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, this.timeToWait));
        spinner.setEditable(true);
        spinner.getValueFactory().valueProperty().addListener((o, oldValue, newValue) -> {
            waitingTime.setText(newValue.toString() + " s");
            timeToWait = newValue;
            if(time != 0)
                time = timeToWait * 2;
        });

        VBox popOverBox = new VBox(spinner);
        popOverBox.setSpacing(20);
        popOverBox.setPadding(new Insets(10));
        PopOver popOver = new PopOver(popOverBox);

        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

        waitingTime.setOnAction(event -> popOver.show(waitingTime));

        Label passed = new Label(Ref.language.getString("label.time_passed"));

        passed.setMaxWidth(1.7976931348623157E308);
        passed.setMaxHeight(1.7976931348623157E308);

        condition.getChildren().addAll(waitingTime, passed);
        condition.getStyleClass().add("plan-area");

        box.getChildren().add(condition);
    }
}
