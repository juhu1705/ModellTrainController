package de.noisruker.railroad;

import de.noisruker.loconet.LocoNet;
import de.noisruker.railroad.DijkstraRailroad.SensorNode;
import de.noisruker.railroad.elements.AbstractRailroadElement;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.railroad.elements.Switch;
import de.noisruker.util.Ref;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TrainSwitchController {

    private final HashMap<Switch, Boolean> addedSwitches = new HashMap<>();
    private final HashMap<Sensor, HashMap<Switch, Boolean>> switchesBySensors = new HashMap<>();
    private Sensor actualHandledSensor = null;

    public TrainSwitchController() {

    }

    public void setActualHandledSensor(Sensor s) {
        if(s == null)
            return;

        if(this.actualHandledSensor != null) {
            this.switchesBySensors.put(actualHandledSensor, (HashMap<Switch, Boolean>) this.addedSwitches.clone());
            this.addedSwitches.clear();
        }

        this.actualHandledSensor = s;
    }

    public void addSwitch(Switch s, boolean direction) {
        Ref.LOGGER.info("Added switch (" + s.toString() + ") for sensor (" + this.actualHandledSensor + ") with direction " + direction);
        this.addedSwitches.put(s, direction);
    }

    public void activateSwitches(Sensor s) {
        Ref.LOGGER.info("Activate switches for Sensor " + s.toString());

        if(this.switchesBySensors.containsKey(s))
            this.switchesBySensors.remove(s).forEach(Switch::setAndUpdateState);
        else if(this.actualHandledSensor != null && this.actualHandledSensor.equals(s)) {
            this.addedSwitches.forEach(Switch::setAndUpdateState);
            this.addedSwitches.clear();
            this.actualHandledSensor = null;
        }
    }
}
