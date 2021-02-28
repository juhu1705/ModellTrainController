package de.noisruker.railroad.DijkstraRailroad;

import de.noisruker.railroad.elements.Sensor;
import de.noisruker.railroad.elements.Switch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class DijkstraNode {

    private static int lastID = 0;
    private static final HashMap<Integer, DijkstraNode> NODES = new HashMap<>();

    private static int getNextID() {
        return DijkstraNode.lastID++;
    }

    protected static void reset() {
        DijkstraNode.NODES.clear();
        lastID = 0;
    }


    private final int nodeID;
    private final ArrayList<Sensor> containedSensors;
    private final Switch aSwitch;
    private final SwitchNodePosition directionPosition;
    private int toTrue = -1, toFalse = -1;

    public DijkstraNode(Switch aSwitch, SwitchNodePosition position) {
        this.directionPosition = position;
        this.aSwitch = aSwitch;
        this.nodeID = DijkstraNode.getNextID();
        this.containedSensors = new ArrayList<>();
        DijkstraNode.NODES.put(this.nodeID, this);
    }

    public void addSensors(Sensor... sensors) {
        this.containedSensors.addAll(Arrays.asList(sensors));
    }

    public void addToTrue(int id) {
        this.toTrue = id;
    }

    public void addToTrue(DijkstraNode node) {
        this.toTrue = node.nodeID;
    }

    public void addToFalse(int id) {
        this.toFalse = id;
    }

    public void addToFalse(DijkstraNode node) {
        this.toFalse = node.nodeID;
    }

    public boolean containsSensor(Sensor sensor) {
        return this.containedSensors.contains(sensor);
    }

    public DijkstraNode getToTrue() {
        return this.toTrue > -1 ? DijkstraNode.NODES.get(toTrue) : null;
    }

    public DijkstraNode getToFalse() {
        return this.toFalse > -1 ? DijkstraNode.NODES.get(toFalse) : null;
    }

    public DijkstraNode getOneDirection() {
        DijkstraNode node = this.getToTrue();
        return node == null ? this.getToFalse() : node;
    }

    public boolean isOneDirectional() {
        return this.toTrue < 0 || this.toFalse < 0 && !this.isEnd();
    }

    public boolean isEnd() {
        return this.toTrue < 0 && this.toFalse < 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DijkstraNode that = (DijkstraNode) o;
        return nodeID == that.nodeID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeID);
    }
}
