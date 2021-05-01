package de.noisruker.railroad.DijkstraRailroad;

import de.noisruker.railroad.Position;
import de.noisruker.railroad.Train;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class DijkstraNode implements Comparable<DijkstraNode> {

    protected final int nodeID;
    protected final HashMap<DijkstraNode, Integer> nodes = new HashMap<>();
    private final NodePosition position;
    private DijkstraNode duplicate = null;
    private Train reservateFor;

    public DijkstraNode(NodePosition position) {
        this.nodeID = DijkstraRailroad.getInstance().getNextID();
        DijkstraRailroad.getInstance().NODES.put(this.nodeID, this);
        this.position = position;
    }

    public void setDuplicate(DijkstraNode duplicate) {
        this.duplicate = duplicate;
        this.duplicate.duplicate = this;
    }

    public NodePosition getPosition() {
        return this.position;
    }

    public boolean addNode(int nodeID, int wayLength) {
        return this.addNode(DijkstraRailroad.getInstance().NODES.get(nodeID), wayLength);
    }

    public boolean addNode(DijkstraNode node, int wayLength) {
        if(node == null || wayLength < 0)
            return false;
        this.nodes.put(node, wayLength);
        return true;
    }

    public boolean isOneDirectional() {
        return this.nodes.size() < 2 && !this.isEnd();
    }

    public boolean isEnd() {
        return this.nodes.size() < 1;
    }

    public SensorNode clacNextSensorForSensor(Train t) {
        if(t == null)
            return null;

        if(this.nodes.size() == 0)
            return null;

        if(this.nodes.size() == 1) {
            for(Map.Entry<DijkstraNode, Integer> node: this.nodes.entrySet()) {
                return node.getKey().calcNextSensor(t);
            }
        }

        DijkstraNode chosenWay = t.getChosenWay(this);

        if(chosenWay == null)
            return null;
        else if(this.nodes.containsKey(chosenWay))
            return chosenWay.calcNextSensor(t);

        return null;
    }

    private SensorNode calcNextSensor(Train t) {
        if(t == null)
            return null;

        if(this instanceof SwitchNode)
            return null;

        if(this instanceof SensorNode)
            return (SensorNode) this;

        if(this.nodes.size() == 0)
            return null;

        if(this.nodes.size() == 1) {
            for(Map.Entry<DijkstraNode, Integer> node: this.nodes.entrySet()) {
                return node.getKey().calcNextSensor(t);
            }
        }

        return null;
    }

    public SensorNode getNextSensor(Train t) {
        if(t == null)
            return null;

        if(this.nodes.size() == 0)
            return null;

        if(this.nodes.size() == 1) {
            for(Map.Entry<DijkstraNode, Integer> node: this.nodes.entrySet()) {
                return node.getKey().getNextSensorCalc(t);
            }
        }

        DijkstraNode chosenWay = t.getChosenWay(this);

        if(chosenWay == null)
            return null;
        else if(this.nodes.containsKey(chosenWay))
            return chosenWay.getNextSensorCalc(t);

        return null;
    }

    private SensorNode getNextSensorCalc(Train t) {
        if(t == null)
            return null;

        if(this instanceof SensorNode)
            return (SensorNode) this;

        if(this.nodes.size() == 0)
            return null;

        if(this instanceof SwitchNode && NodePosition.switchablePositions.contains(this.position)) {
            t.getTrainSwitchController().addSwitch(((SwitchNode) this).getSwitch(),
                    this.getPosition().equals(NodePosition.IN_TRUE) || this.getPosition().equals(NodePosition.OUT_TRUE));
        }

        if(this.nodes.size() == 1) {
            for(Map.Entry<DijkstraNode, Integer> node: this.nodes.entrySet()) {
                return node.getKey().getNextSensorCalc(t);
            }
        }

        DijkstraNode chosenWay = t.getChosenWay(this);

        if(chosenWay == null)
            return null;
        else if(this.nodes.containsKey(chosenWay))
            return chosenWay.getNextSensorCalc(t);

        return null;
    }

    public boolean hasDuplicate() {
        return this.duplicate != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DijkstraNode that = (DijkstraNode) o;
        return nodeID == that.nodeID;
    }

    @Override
    public int compareTo(DijkstraNode o) {
        return this.nodeID - o.nodeID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeID);
    }

    @Override
    public String toString() {
        StringBuilder childs = new StringBuilder();
        for(Map.Entry<DijkstraNode, Integer> entry: this.nodes.entrySet()) {
            childs.append("Node: ").append(entry.getKey().nodeID).append("; ");
        }

        return "DijkstraNode{" +
                "nodeID=" + nodeID +
                ", duplicate=" + duplicate.nodeID +
                ", position=" + position +
                ", nodes=" + childs.toString() +
                '}';
    }
}
