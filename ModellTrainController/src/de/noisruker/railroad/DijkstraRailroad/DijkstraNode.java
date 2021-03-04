package de.noisruker.railroad.DijkstraRailroad;

import de.noisruker.railroad.elements.AbstractRailroadElement;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.railroad.elements.Switch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import static de.noisruker.railroad.DijkstraRailroad.SwitchNodePosition.*;

public abstract class DijkstraNode {

    private static int lastID = 0;
    protected static final HashMap<Integer, DijkstraNode> NODES = new HashMap<>();

    private static int getNextID() {
        return DijkstraNode.lastID++;
    }

    protected static void reset() {
        DijkstraNode.NODES.clear();
        lastID = 0;
    }


    protected final int nodeID;
    protected final HashMap<DijkstraNode, Integer> nodes = new HashMap<>();

    public DijkstraNode() {
        this.nodeID = DijkstraNode.getNextID();
        DijkstraNode.NODES.put(this.nodeID, this);
    }

    public void addNode(int nodeID, int wayLength) {
        this.addNode(NODES.get(nodeID), wayLength);
    }

    public void addNode(DijkstraNode node, int wayLength) {
        if(node == null || wayLength < 0)
            return;
        this.nodes.put(node, wayLength);
    }

    public boolean isOneDirectional() {
        return this.nodes.size() < 2 && !this.isEnd();
    }

    public boolean isEnd() {
        return this.nodes.size() < 1;
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
