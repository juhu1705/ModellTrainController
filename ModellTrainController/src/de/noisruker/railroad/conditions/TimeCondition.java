package de.noisruker.railroad.conditions;

import de.noisruker.railroad.TrainStationManager;
import de.noisruker.util.Ref;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import org.kordamp.ikonli.javafx.FontIcon;

public class TimeCondition extends AbstractDrivingCondition {

    private int timeToWait, time;
    HBox condition = new HBox();

    public TimeCondition(int timeToWait) {
        this.timeToWait = timeToWait;
        this.time = this.timeToWait * 2;
    }

    @Override
    public boolean isConditionTrue() {
        return time == 0;
    }

    @Override
    public void updateCondition() {
        if (time > 0)
            time--;
        else
            time = 0;
        Platform.runLater(this::updateGui);
    }

    private void updateGui() {
        if (super.isInCheck) {
            if (this.time > 0 && !condition.getStyleClass().contains("in-progress"))
                condition.getStyleClass().add("in-progress");
            else if (this.time == 0 && !condition.getStyleClass().contains("checked"))
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
    public void addToGui(VBox box, TrainStationManager.TrainStation station) {
        this.condition.getChildren().clear();
        this.condition.setPadding(new Insets(10));
        this.condition.setSpacing(20);
        Button waitingTime = new Button(timeToWait + " s");

        Spinner<Integer> spinner = new Spinner<>();
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, this.timeToWait));
        spinner.setEditable(true);
        spinner.getValueFactory().valueProperty().addListener((o, oldValue, newValue) -> {
            waitingTime.setText(newValue.toString() + " s");
            timeToWait = newValue;
            if (time != 0)
                time = timeToWait * 2;
        });

        VBox popOverBox = new VBox(spinner);
        popOverBox.setSpacing(20);
        popOverBox.setPadding(new Insets(10));
        PopOver popOver = new PopOver(popOverBox);

        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

        waitingTime.setOnAction(event -> popOver.show(waitingTime));
        waitingTime.setMinWidth(100);

        Label passed = new Label(Ref.language.getString("label.time_passed"));

        HBox.setHgrow(passed, Priority.ALWAYS);

        Button delete = new Button();
        delete.setOnAction(event -> {
            station.deleteCondition(this);
        });
        delete.setGraphic(new FontIcon("fas-trash"));

        passed.setMaxWidth(1.7976931348623157E308);
        passed.setMaxHeight(1.7976931348623157E308);
        condition.setMaxHeight(1.7976931348623157E308);
        condition.setMaxWidth(1.7976931348623157E308);

        condition.getChildren().addAll(waitingTime, passed, delete);
        condition.getStyleClass().add("plan-area");

        box.getChildren().add(condition);
    }
}
