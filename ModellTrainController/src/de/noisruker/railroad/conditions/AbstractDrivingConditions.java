package de.noisruker.railroad.conditions;

import javafx.scene.layout.VBox;

public abstract class AbstractDrivingConditions {

    protected boolean isInCheck = false;

    public void setChecking(boolean checking) {
        this.isInCheck = checking;
    }

    public abstract boolean isConditionTrue();

    public abstract void updateCondition();

    public abstract void start();

    public abstract void addToGui(VBox box);

}
