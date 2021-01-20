package de.noisruker.railroad.conditions;

import de.noisruker.railroad.TrainStationManager;
import javafx.scene.layout.VBox;

public abstract class AbstractDrivingCondition {

    protected boolean isInCheck = false;

    public void setChecking(boolean checking) {
        this.isInCheck = checking;
    }

    public abstract boolean isConditionTrue();

    public abstract void updateCondition();

    public abstract void start();

    public abstract void addToGui(VBox box, TrainStationManager.TrainStation station);

}
