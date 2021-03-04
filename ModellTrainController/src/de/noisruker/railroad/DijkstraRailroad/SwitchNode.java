package de.noisruker.railroad.DijkstraRailroad;

import de.noisruker.railroad.elements.Switch;

import java.util.HashMap;

public class SwitchNode extends DijkstraNode {

    private final Switch aSwitch;
    private final SwitchNodePosition position;
    private DijkstraNode wayTrue = null;
    private DijkstraNode wayFalse = null;

    public SwitchNode(Switch s, SwitchNodePosition position) {
        super();
        this.aSwitch = s;
        this.position = position;
    }

    public void addToTrue(DijkstraNode node, int wayLength) {
        this.wayTrue = node;
        super.addNode(node, wayLength);
    }

    public void addToFalse(DijkstraNode node, int wayLength) {
        this.wayFalse = node;
        super.addNode(node, wayLength);
    }

    public boolean isWayFalse(int id) {
        return this.isWayFalse(NODES.get(id));
    }

    public boolean isWayFalse(DijkstraNode node) {
        if(node != null)
            return node.equals(this.wayFalse);
        return false;
    }

    public boolean isWayTrue(int id) {
        return this.isWayTrue(NODES.get(id));
    }

    public boolean isWayTrue(DijkstraNode node) {
        if(node != null)
            return node.equals(this.wayTrue);
        return false;
    }


}
