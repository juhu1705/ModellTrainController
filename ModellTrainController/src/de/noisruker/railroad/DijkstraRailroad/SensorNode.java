package de.noisruker.railroad.DijkstraRailroad;

import de.noisruker.railroad.Position;
import de.noisruker.railroad.elements.Sensor;

public class SensorNode extends DijkstraNode {

    private final Sensor sensor;

    public SensorNode(Sensor sensor, NodePosition position) {
        super(position);
        if(sensor == null)
            throw new NullPointerException("Sensor could not be null!");
        this.sensor = sensor;
    }

    @Override
    public boolean addNode(DijkstraNode node, int wayLength) {
        if(node == null || wayLength < 0)
            return false;

        super.nodes.clear();
        super.nodes.put(node, wayLength);
        return true;
    }

    public Sensor getSensor() {
        return this.sensor;
    }

    public boolean isSensor(Sensor s) {
        if(this.sensor != null)
            return this.sensor.equals(s);
        return false;
    }

    public Position getPrevPosition() {
        return switch (this.sensor.getRotation()) {
            case NORTH, SOUTH -> this.getPosition().equals(NodePosition.IN_BOTH) ? this.sensor.getPosition().south() : this.sensor.getPosition().north();
            case EAST, WEST -> this.getPosition().equals(NodePosition.IN_BOTH) ? this.sensor.getPosition().west() : this.sensor.getPosition().east();
        };
    }

    @Override
    public String toString() {
        return "SensorNode{" +
                "sensor=" + sensor.getAddress() +
                super.toString() +
                '}';
    }
}
