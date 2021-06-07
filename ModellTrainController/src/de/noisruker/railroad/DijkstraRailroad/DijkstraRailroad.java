package de.noisruker.railroad.DijkstraRailroad;

import de.noisruker.railroad.Position;
import de.noisruker.railroad.RailRotation;
import de.noisruker.railroad.elements.AbstractRailroadElement;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.railroad.elements.Switch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static de.noisruker.railroad.DijkstraRailroad.NodePosition.*;
import static de.noisruker.railroad.DijkstraRailroad.NodePosition.IN_BOTH;

public class DijkstraRailroad {

    private static DijkstraRailroad instance;

    /**
     * @return The actual DijkstraRailroad element
     */
    public static DijkstraRailroad getInstance() {
        return DijkstraRailroad.instance != null ? DijkstraRailroad.instance :
                (DijkstraRailroad.instance = new DijkstraRailroad());
    }

    private int lastID = 0;
    protected final HashMap<Integer, DijkstraNode> NODES = new HashMap<>();
    private final HashMap<Position, ArrayList<DijkstraNode>> NODES_BY_POSITION = new HashMap<>();

    private DijkstraRailroad() {}

    /**
     * @return The next free id for a DijkstraNode
     */
    protected int getNextID() {
        return this.lastID++;
    }

    /**
     * Resets the actual Railroad
     */
    protected void resetRailroad() {
        this.NODES.clear();
        this.lastID = 0;
    }

    /**
     * Converts the 2 dimensional Array of {@link AbstractRailroadElement}s in this DijkstraRailroad.
     * @param railroad The array to convert.
     */
    public void convertRailroad(AbstractRailroadElement[][] railroad) {
        this.resetRailroad();

        ArrayList<Switch> switches = new ArrayList<>();
        ArrayList<Sensor> sensors = new ArrayList<>();

        for(AbstractRailroadElement[] elements: railroad)
            for(AbstractRailroadElement element: elements) {
                if (element instanceof Switch)
                    switches.add((Switch) element);
                if (element instanceof Sensor)
                    sensors.add((Sensor) element);
            }


        NODES_BY_POSITION.clear();

        switches.forEach(s -> NODES_BY_POSITION.put(s.getPosition(), this.createNodesFor(s)));
        sensors.forEach(s -> NODES_BY_POSITION.put(s.getPosition(), this.createNodesFor(s)));

        NODES_BY_POSITION.forEach((element, dijkstraNodes) ->
            dijkstraNodes.forEach(dijkstraNode -> this.calculateMatches(dijkstraNode, NODES_BY_POSITION, railroad)));
    }

    private void calculateMatches(DijkstraNode from,
                                  HashMap<Position, ArrayList<DijkstraNode>> nodesBySwitches,
                                  AbstractRailroadElement[][] railroad) {
        if(from instanceof SensorNode)
            this.calculateSensorMatches((SensorNode) from, nodesBySwitches, railroad);
        else if(from instanceof SwitchNode)
            this.calculateSwitchMatches((SwitchNode) from, nodesBySwitches, railroad);
    }

    private void calculateSensorMatches(SensorNode from,
                                        HashMap<Position, ArrayList<DijkstraNode>> nodesBySwitches,
                                        AbstractRailroadElement[][] railroad) {
        Sensor start = from.getSensor();
        Position last = start.getPosition();
        Position actual = this.getWayToGo(start, from.getPosition());

        if(actual == null)
            return;

        AbstractRailroadElement actualElement = this.getElement(actual, railroad);
        int length = 1;

        do {
            if (actualElement == null)
                return;

            if (actualElement instanceof Sensor end) {

                this.setupConnection(from, end, last, nodesBySwitches.get(end.getPosition()), length);

                return;
            }

            if (actualElement instanceof Switch end) {

                this.setupConnection(from, end, last, nodesBySwitches.get(end.getPosition()), length);

                return;
            }

            actualElement = this.getElement(actualElement.getToPos(last), railroad);
            last = actual;
            actual = actualElement.getPosition();
            length++;
        } while (true);
    }

    private void calculateSwitchMatches(SwitchNode from,
                                        HashMap<Position, ArrayList<DijkstraNode>> nodesBySwitches,
                                        AbstractRailroadElement[][] railroad) {
        if(from.getPosition().equals(IN_TRUE) || from.getPosition().equals(IN_FALSE) ||
                from.getPosition().equals(IN_BOTH))
            return;

        Switch start = from.getSwitch();
        Position last = start.getPosition();
        Position actual = this.getWayToGo(start, from.getPosition());

        if(actual == null)
            return;

        AbstractRailroadElement actualElement = this.getElement(actual, railroad);
        int length = 1;

        do {
            if (actualElement == null)
                return;

            if (actualElement instanceof Sensor end) {

                this.setupConnection(from, end, last, nodesBySwitches.get(end.getPosition()), length);

                return;
            }

            if (actualElement instanceof Switch end) {

                this.setupConnection(from, end, last, nodesBySwitches.get(end.getPosition()), length);

                return;
            }

            actualElement = this.getElement(actualElement.getToPos(last), railroad);
            last = actual;
            actual = actualElement.getPosition();
            length++;
        } while (true);
    }
    
    private void setupConnection(DijkstraNode from, Sensor end, Position last, ArrayList<DijkstraNode> nodes, int length) {
        if(end.getRotation().equals(RailRotation.NORTH) || end.getRotation().equals(RailRotation.SOUTH)) {
            if(last.equals(end.getPosition().south()))
                for(DijkstraNode node: nodes)
                    if(node.getPosition().equals(IN_BOTH)) {
                        from.addNode(node, length);
                        return;
                    }
            if(last.equals(end.getPosition().north()))
                for(DijkstraNode node: nodes)
                    if(node.getPosition().equals(OUT_BOTH)) {
                        from.addNode(node, length);
                        return;
                    }
        }
        if(end.getRotation().equals(RailRotation.EAST) || end.getRotation().equals(RailRotation.WEST)) {
            if(last.equals(end.getPosition().west()))
                for(DijkstraNode node: nodes)
                    if(node.getPosition().equals(IN_BOTH)) {
                        from.addNode(node, length);
                        return;
                    }
            if(last.equals(end.getPosition().east()))
                for(DijkstraNode node: nodes)
                    if(node.getPosition().equals(OUT_BOTH)) {
                        from.addNode(node, length);
                        return;
                    }
        }
    }

    private void setupConnection(DijkstraNode from, Switch end, Position last, ArrayList<DijkstraNode> nodes, int length) {
        NodePosition nodeToConnect = this.calculateFromWhere(end, last);

        if(!nodeToConnect.equals(OUT_BOTH)) {
            DijkstraNode node = getNodeByPosition(nodeToConnect, nodes);

            if(node != null) {
                from.addNode(node, length);
            }
        }
    }

    private DijkstraNode getNodeByPosition(NodePosition position, ArrayList<DijkstraNode> nodes) {
        for(DijkstraNode node: nodes) {
            if(node.getPosition().equals(position))
                return node;
        }
        return null;
    }

    private NodePosition calculateFromWhere(Switch s, Position prev) {
        switch (s.getSwitchType()) {
            case LEFT -> {
                switch (s.getRotation()) {
                    case NORTH -> {
                        if(prev.equals(s.getPosition().north()))
                            return IN_BOTH;
                        else if(prev.equals(s.getPosition().south()))
                            return s.getNormalDirectional() ? IN_TRUE : IN_FALSE;
                        else if(prev.equals(s.getPosition().east()))
                            return s.getNormalDirectional() ? IN_FALSE : IN_TRUE;
                    }
                    case WEST -> {
                        if(prev.equals(s.getPosition().west()))
                            return IN_BOTH;
                        else if(prev.equals(s.getPosition().east()))
                            return s.getNormalDirectional() ? IN_TRUE : IN_FALSE;
                        else if(prev.equals(s.getPosition().north()))
                            return s.getNormalDirectional() ? IN_FALSE : IN_TRUE;
                    }
                    case EAST -> {
                        if(prev.equals(s.getPosition().east()))
                            return IN_BOTH;
                        else if(prev.equals(s.getPosition().west()))
                            return s.getNormalDirectional() ? IN_TRUE : IN_FALSE;
                        else if(prev.equals(s.getPosition().south()))
                            return s.getNormalDirectional() ? IN_FALSE : IN_TRUE;
                    }
                    case SOUTH -> {
                        if(prev.equals(s.getPosition().south()))
                            return IN_BOTH;
                        else if(prev.equals(s.getPosition().north()))
                            return s.getNormalDirectional() ? IN_TRUE : IN_FALSE;
                        else if(prev.equals(s.getPosition().west()))
                            return s.getNormalDirectional() ? IN_FALSE : IN_TRUE;
                    }
                }
            }
            case RIGHT -> {
                switch (s.getRotation()) {
                    case NORTH -> {
                        if(prev.equals(s.getPosition().north()))
                            return IN_BOTH;
                        else if(prev.equals(s.getPosition().south()))
                            return s.getNormalDirectional() ? IN_TRUE : IN_FALSE;
                        else if(prev.equals(s.getPosition().west()))
                            return s.getNormalDirectional() ? IN_FALSE : IN_TRUE;
                    }
                    case WEST -> {
                        if(prev.equals(s.getPosition().west()))
                            return IN_BOTH;
                        else if(prev.equals(s.getPosition().east()))
                            return s.getNormalDirectional() ? IN_TRUE : IN_FALSE;
                        else if(prev.equals(s.getPosition().south()))
                            return s.getNormalDirectional() ? IN_FALSE : IN_TRUE;
                    }
                    case EAST -> {
                        if(prev.equals(s.getPosition().east()))
                            return IN_BOTH;
                        else if(prev.equals(s.getPosition().west()))
                            return s.getNormalDirectional() ? IN_TRUE : IN_FALSE;
                        else if(prev.equals(s.getPosition().north()))
                            return s.getNormalDirectional() ? IN_FALSE : IN_TRUE;
                    }
                    case SOUTH -> {
                        if(prev.equals(s.getPosition().south()))
                            return IN_BOTH;
                        else if(prev.equals(s.getPosition().north()))
                            return s.getNormalDirectional() ? IN_TRUE : IN_FALSE;
                        else if(prev.equals(s.getPosition().east()))
                            return s.getNormalDirectional() ? IN_FALSE : IN_TRUE;
                    }
                }
            }
            case LEFT_RIGHT -> {
                switch (s.getRotation()) {
                    case NORTH -> {
                        if(prev.equals(s.getPosition().north()))
                            return IN_BOTH;
                        else if(prev.equals(s.getPosition().east()))
                            return s.getNormalDirectional() ? IN_TRUE : IN_FALSE;
                        else if(prev.equals(s.getPosition().west()))
                            return s.getNormalDirectional() ? IN_FALSE : IN_TRUE;
                    }
                    case WEST -> {
                        if(prev.equals(s.getPosition().west()))
                            return IN_BOTH;
                        else if(prev.equals(s.getPosition().north()))
                            return s.getNormalDirectional() ? IN_TRUE : IN_FALSE;
                        else if(prev.equals(s.getPosition().south()))
                            return s.getNormalDirectional() ? IN_FALSE : IN_TRUE;
                    }
                    case EAST -> {
                        if(prev.equals(s.getPosition().east()))
                            return IN_BOTH;
                        else if(prev.equals(s.getPosition().south()))
                            return s.getNormalDirectional() ? IN_TRUE : IN_FALSE;
                        else if(prev.equals(s.getPosition().north()))
                            return s.getNormalDirectional() ? IN_FALSE : IN_TRUE;
                    }
                    case SOUTH -> {
                        if(prev.equals(s.getPosition().south()))
                            return IN_BOTH;
                        else if(prev.equals(s.getPosition().west()))
                            return s.getNormalDirectional() ? IN_TRUE : IN_FALSE;
                        else if(prev.equals(s.getPosition().east()))
                            return s.getNormalDirectional() ? IN_FALSE : IN_TRUE;
                    }
                }
            }
        }
        return OUT_BOTH;
    }

    private AbstractRailroadElement getElement(Position p, AbstractRailroadElement[][] railroad) {
        return railroad[p.getX()][p.getY()];
    }

    private Position getWayToGo(Switch start, NodePosition outputDirection) {
        RailRotation rotation = start.getRotation();
        Position from = start.getPosition();

        Position to = null;

        switch (outputDirection) {
            case OUT_BOTH -> {
                switch (rotation) {
                    case NORTH -> to = from.north();
                    case SOUTH -> to = from.south();
                    case EAST -> to = from.east();
                    case WEST -> to = from.west();
                }
            }
            case OUT_TRUE -> {
                switch (rotation) {
                    case NORTH -> to = start.getNextPositionSwitchSpecial(from.north(), true);
                    case SOUTH -> to = start.getNextPositionSwitchSpecial(from.south(), true);
                    case EAST -> to = start.getNextPositionSwitchSpecial(from.east(), true);
                    case WEST -> to = start.getNextPositionSwitchSpecial(from.west(), true);
                }
            }
            case OUT_FALSE -> {
                switch (rotation) {
                    case NORTH -> to = start.getNextPositionSwitchSpecial(from.north(), false);
                    case SOUTH -> to = start.getNextPositionSwitchSpecial(from.south(), false);
                    case EAST -> to = start.getNextPositionSwitchSpecial(from.east(), false);
                    case WEST -> to = start.getNextPositionSwitchSpecial(from.west(), false);
                }
            }
        }

        return to;
    }

    private Position getWayToGo(Sensor start, NodePosition outputDirection) {
        RailRotation rotation = start.getRotation();
        Position from = start.getPosition();

        Position to = null;

        switch (outputDirection) {
            case OUT_BOTH -> {
                switch (rotation) {
                    case NORTH, SOUTH -> to = from.south();
                    case EAST, WEST -> to = from.west();
                }
            }
            case IN_BOTH -> {
                switch (rotation) {
                    case NORTH, SOUTH -> to = from.north();
                    case EAST, WEST -> to = from.east();
                }
            }
        }

        return to;
    }

    private ArrayList<DijkstraNode> createNodesFor(Switch s) {
        SwitchNode inSwitch = new SwitchNode(s, IN_BOTH);
        SwitchNode outSwitch1 = new SwitchNode(s, OUT_TRUE);
        SwitchNode outSwitch2 = new SwitchNode(s, OUT_FALSE);

        inSwitch.addToTrue(outSwitch1, 0);
        inSwitch.addToFalse(outSwitch2, 0);

        SwitchNode outSwitch = new SwitchNode(s, OUT_BOTH);
        SwitchNode inSwitch1 = new SwitchNode(s, IN_TRUE);
        SwitchNode inSwitch2 = new SwitchNode(s, IN_FALSE);

        inSwitch1.addToTrue(outSwitch, 0);
        inSwitch2.addToFalse(outSwitch, 0);

        inSwitch.setDuplicate(outSwitch);
        outSwitch1.setDuplicate(inSwitch1);
        outSwitch2.setDuplicate(inSwitch2);

        return new ArrayList<>(Arrays.asList(inSwitch, inSwitch1, inSwitch2, outSwitch, outSwitch1, outSwitch2));
    }

    private ArrayList<DijkstraNode> createNodesFor(Sensor s) {
        SensorNode sensorUp = new SensorNode(s, IN_BOTH);
        SensorNode sensorDown = new SensorNode(s, OUT_BOTH);

        sensorUp.setDuplicate(sensorDown);

        return new ArrayList<>(Arrays.asList(sensorUp, sensorDown));
    }

    public DijkstraNode getNodeByPosition(Sensor s, Position prev) {
        return this.getNodeByPosition(s, s.getPosition().south().equals(prev) || s.getPosition().west().equals(prev));
    }

    /**
     * Calculate the DijkstraNode from a given Sensor and direction
     *
     * @param s The sensor for which the Node is searched
     * @param direction The direction the train drives.
     *                  true: If the train comes from south or west
     *                  false: If the train comes from north or east
     * @return The node for the given sensor or null if no node is found
     */
    public DijkstraNode getNodeByPosition(Sensor s, boolean direction) {
        ArrayList<DijkstraNode> sensorNodes = this.NODES_BY_POSITION.get(s);
        if(sensorNodes == null || sensorNodes.isEmpty())
            return null;

        for(DijkstraNode node: sensorNodes) {
            if(direction && node.getPosition().equals(IN_BOTH))
                return node;
            else if(!direction && node.getPosition().equals(OUT_BOTH))
                return node;
        }

        return null;
    }

    public List<DijkstraNode> getShortestPath(DijkstraNode from, Sensor to) {
        return DijkstraUtil.findShortestPath(this.NODES.values(), from, to);
    }

}
