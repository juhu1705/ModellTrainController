package de.noisruker.railroad;

import de.noisruker.railroad.elements.Sensor;
import de.noisruker.railroad.elements.Switch;

import java.util.HashMap;
import java.util.Map.Entry;

public class Railway {

    private HashMap<Integer, Sensor> sensorList = new HashMap<>();
    private HashMap<Entry<Integer, Sensor>, Switch> switchList = new HashMap<>();

    public Railway(Position from, Position to, Position dir) {

    }

    public void findSensor(Sensor s) {

    }

    public void findSwitch(Switch s) {

    }

}
