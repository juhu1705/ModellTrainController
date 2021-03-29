package de.noisruker.railroad.DijkstraRailroad;

import de.noisruker.railroad.Train;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class DijkstraNode {

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

    public boolean reservateWithNext(Train t) {
        if(this.reservateFor != null)
            return false;

        if(this.position.equals(NodePosition.IN_TRUE) || this.position.equals(NodePosition.IN_FALSE)) {
            return this.setReservateFor(t);
        }

        if(this.nodes.size() == 0) {
            return this.setReservateFor(t);
        }

        if(this.nodes.size() == 1) {
            for(Map.Entry<DijkstraNode, Integer> node: this.nodes.entrySet()) {
                if(node.getKey().reservateWithNext(t))
                    return this.setReservateFor(t);
                else
                    return false;
            }
        }

        DijkstraNode chosenWay = t.getChosenWay(this);

        if(chosenWay == null)
            return this.setReservateFor(t);
        else if(this.nodes.containsKey(chosenWay)) {
            if(chosenWay.reservateWithNext(t))
                return this.setReservateFor(t);
            else
                return false;
        }

        return false;
    }

    private boolean setReservateFor(Train t) {
        if(this.reservateFor != null)
            return false;
        if(!this.reservateDuplicate(t))
            return false;
        this.reservateFor = t;

        return true;
    }

    private boolean setReservateForDuplicate(Train t) {
        if(this.reservateFor != null)
            return false;
        this.reservateFor = t;
        return true;
    }

    public boolean reservateDuplicate(Train t) {
        if(this.hasDuplicate())
            return this.duplicate.setReservateForDuplicate(t);
        return true;
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
