package de.noisruker.railroad.DijkstraRailroad;

import de.noisruker.railroad.elements.Sensor;

public class SensorNode extends DijkstraNode {

    private final Sensor sensor;

    public SensorNode(Sensor sensor, NodePosition position) {
        super(position);
        this.sensor = sensor;
    }

    @Override
    public void addNode(DijkstraNode node, int wayLength) {
        if(node == null || wayLength < 0)
            return;

        super.nodes.clear();
        super.nodes.put(node, wayLength);
    }

    public Sensor getSensor() {
        return this.sensor;
    }

    public boolean isSensor(Sensor s) {
        if(this.sensor != null)
            return this.sensor.equals(s);
        return false;
    }
}
