package de.noisruker.railroad.DijkstraRailroad;

import de.noisruker.railroad.elements.Switch;

public class SwitchNode extends DijkstraNode {

    private final Switch aSwitch;
    private DijkstraNode wayTrue = null;
    private DijkstraNode wayFalse = null;

    public SwitchNode(Switch s, NodePosition position) {
        super(position);
        this.aSwitch = s;
    }

    public Switch getSwitch() {
        return this.aSwitch;
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
        return this.isWayFalse(DijkstraRailroad.getInstance().NODES.get(id));
    }

    public boolean isWayFalse(DijkstraNode node) {
        if(node != null)
            return node.equals(this.wayFalse);
        return false;
    }

    public boolean isWayTrue(int id) {
        return this.isWayTrue(DijkstraRailroad.getInstance().NODES.get(id));
    }

    public boolean isWayTrue(DijkstraNode node) {
        if(node != null)
            return node.equals(this.wayTrue);
        return false;
    }


}
