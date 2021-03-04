package de.noisruker.railroad.DijkstraRailroad;

import de.noisruker.railroad.Position;
import de.noisruker.railroad.RailRotation;
import de.noisruker.railroad.elements.AbstractRailroadElement;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.railroad.elements.Switch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static de.noisruker.railroad.DijkstraRailroad.SwitchNodePosition.*;

public class DijkstraRailroad {

    private static DijkstraRailroad instance;

    public static DijkstraRailroad getInstance() {
        return DijkstraRailroad.instance != null ? DijkstraRailroad.instance :
                (DijkstraRailroad.instance = new DijkstraRailroad());
    }

    public void convertRailroad(AbstractRailroadElement[][] railroad) {
        DijkstraNode.reset();

        ArrayList<Switch> switches = new ArrayList<>();

        for(AbstractRailroadElement[] elements: railroad)
            for(AbstractRailroadElement element: elements)
                if(element instanceof Switch)
                    switches.add((Switch) element);

        final HashMap<Switch, ArrayList<DijkstraNode>> nodesBySwitches = new HashMap<>();

        switches.forEach(s -> nodesBySwitches.put(s, this.createNodesFor(s)));

        nodesBySwitches.forEach((aSwitch, dijkstraNodes) ->
            dijkstraNodes.forEach(dijkstraNode -> {
                if(!dijkstraNode.isInput())
                    this.calculateMatches(dijkstraNode, nodesBySwitches, railroad);
            }));


    }

    private void recheckSensors(AbstractRailroadElement[][] railroad, HashMap<Switch, ArrayList<DijkstraNode>> nodesBySwitches) {
        ArrayList<Sensor> allSensors = new ArrayList<>();

        for(AbstractRailroadElement[] elements: railroad) {
            for (AbstractRailroadElement element : elements)
                if (element instanceof Sensor)
                    allSensors.add((Sensor) element);
        }

        nodesBySwitches.forEach((aSwitch, dijkstraNodes) ->
                dijkstraNodes.forEach(dijkstraNode -> {
                    for(int i = 0; i < allSensors.size();) {
                        if(dijkstraNode.containsSensor(allSensors.get(i)))
                            allSensors.remove(i);
                        else
                            i++;
                    }
                }));

        allSensors.forEach(sensor -> new DijkstraNode(null, WAY_BOTH).addSensors(sensor));

    }

    private void calculateMatches(DijkstraNode from, HashMap<Switch, ArrayList<DijkstraNode>> nodesBySwitches,
                                  AbstractRailroadElement[][] railroad) {
        if(from == null)
            return;

        Switch start = from.getSwitch();
        Position last = start.getPosition();
        Position actual = this.getWayToGo(start, from.getOutputDirection());

        if(actual == null)
            return;

        AbstractRailroadElement actualElement = this.getElement(actual, railroad);

        do {
            if (actualElement == null)
                return;

            if (actualElement instanceof Sensor)
                from.addSensors((Sensor) actualElement);

            if (actualElement instanceof Switch) {
                Switch end = (Switch) actualElement;

                this.setupConnection(from, end, last, nodesBySwitches.get(end));

                return;
            }

            actualElement = this.getElement(actualElement.getToPos(last), railroad);
            last = actual;
            actual = actualElement.getPosition();
        } while (true);
    }

    private void setupConnection(DijkstraNode from, Switch end, Position last, ArrayList<DijkstraNode> nodes) {
        SwitchNodePosition nodeToConnect = this.calculateFromWhere(end, last);

        if(!nodeToConnect.equals(INPUT)) {
            DijkstraNode node = getNodeByPosition(nodeToConnect, nodes);

            if(node != null) {
                from.addToTrue(node.getToTrue());
                from.addToFalse(node.getToFalse());
            }
        }
    }

    private DijkstraNode getNodeByPosition(SwitchNodePosition position, ArrayList<DijkstraNode> nodes) {
        for(DijkstraNode node: nodes) {
            if(node.getOutputDirection().equals(position))
                return node;
        }
        return null;
    }

    private SwitchNodePosition calculateFromWhere(Switch s, Position prev) {
        switch (s.getSwitchType()) {
            case LEFT -> {
                switch (s.getRotation()) {
                    case NORTH -> {
                        if(prev.equals(s.getPosition().north()))
                            return WAY_BOTH;
                        else if(prev.equals(s.getPosition().south()))
                            return s.getNormalDirectional() ? WAY_TRUE : WAY_FALSE;
                        else if(prev.equals(s.getPosition().east()))
                            return s.getNormalDirectional() ? WAY_FALSE : WAY_TRUE;
                    }
                    case WEST -> {
                        if(prev.equals(s.getPosition().west()))
                            return WAY_BOTH;
                        else if(prev.equals(s.getPosition().east()))
                            return s.getNormalDirectional() ? WAY_TRUE : WAY_FALSE;
                        else if(prev.equals(s.getPosition().north()))
                            return s.getNormalDirectional() ? WAY_FALSE : WAY_TRUE;
                    }
                    case EAST -> {
                        if(prev.equals(s.getPosition().east()))
                            return WAY_BOTH;
                        else if(prev.equals(s.getPosition().west()))
                            return s.getNormalDirectional() ? WAY_TRUE : WAY_FALSE;
                        else if(prev.equals(s.getPosition().south()))
                            return s.getNormalDirectional() ? WAY_FALSE : WAY_TRUE;
                    }
                    case SOUTH -> {
                        if(prev.equals(s.getPosition().south()))
                            return WAY_BOTH;
                        else if(prev.equals(s.getPosition().north()))
                            return s.getNormalDirectional() ? WAY_TRUE : WAY_FALSE;
                        else if(prev.equals(s.getPosition().west()))
                            return s.getNormalDirectional() ? WAY_FALSE : WAY_TRUE;
                    }
                }
            }
            case RIGHT -> {
                switch (s.getRotation()) {
                    case NORTH -> {
                        if(prev.equals(s.getPosition().north()))
                            return WAY_BOTH;
                        else if(prev.equals(s.getPosition().south()))
                            return s.getNormalDirectional() ? WAY_TRUE : WAY_FALSE;
                        else if(prev.equals(s.getPosition().west()))
                            return s.getNormalDirectional() ? WAY_FALSE : WAY_TRUE;
                    }
                    case WEST -> {
                        if(prev.equals(s.getPosition().west()))
                            return WAY_BOTH;
                        else if(prev.equals(s.getPosition().east()))
                            return s.getNormalDirectional() ? WAY_TRUE : WAY_FALSE;
                        else if(prev.equals(s.getPosition().south()))
                            return s.getNormalDirectional() ? WAY_FALSE : WAY_TRUE;
                    }
                    case EAST -> {
                        if(prev.equals(s.getPosition().east()))
                            return WAY_BOTH;
                        else if(prev.equals(s.getPosition().west()))
                            return s.getNormalDirectional() ? WAY_TRUE : WAY_FALSE;
                        else if(prev.equals(s.getPosition().north()))
                            return s.getNormalDirectional() ? WAY_FALSE : WAY_TRUE;
                    }
                    case SOUTH -> {
                        if(prev.equals(s.getPosition().south()))
                            return WAY_BOTH;
                        else if(prev.equals(s.getPosition().north()))
                            return s.getNormalDirectional() ? WAY_TRUE : WAY_FALSE;
                        else if(prev.equals(s.getPosition().east()))
                            return s.getNormalDirectional() ? WAY_FALSE : WAY_TRUE;
                    }
                }
            }
            case LEFT_RIGHT -> {
                switch (s.getRotation()) {
                    case NORTH -> {
                        if(prev.equals(s.getPosition().north()))
                            return WAY_BOTH;
                        else if(prev.equals(s.getPosition().east()))
                            return s.getNormalDirectional() ? WAY_TRUE : WAY_FALSE;
                        else if(prev.equals(s.getPosition().west()))
                            return s.getNormalDirectional() ? WAY_FALSE : WAY_TRUE;
                    }
                    case WEST -> {
                        if(prev.equals(s.getPosition().west()))
                            return WAY_BOTH;
                        else if(prev.equals(s.getPosition().north()))
                            return s.getNormalDirectional() ? WAY_TRUE : WAY_FALSE;
                        else if(prev.equals(s.getPosition().south()))
                            return s.getNormalDirectional() ? WAY_FALSE : WAY_TRUE;
                    }
                    case EAST -> {
                        if(prev.equals(s.getPosition().east()))
                            return WAY_BOTH;
                        else if(prev.equals(s.getPosition().south()))
                            return s.getNormalDirectional() ? WAY_TRUE : WAY_FALSE;
                        else if(prev.equals(s.getPosition().north()))
                            return s.getNormalDirectional() ? WAY_FALSE : WAY_TRUE;
                    }
                    case SOUTH -> {
                        if(prev.equals(s.getPosition().south()))
                            return WAY_BOTH;
                        else if(prev.equals(s.getPosition().west()))
                            return s.getNormalDirectional() ? WAY_TRUE : WAY_FALSE;
                        else if(prev.equals(s.getPosition().east()))
                            return s.getNormalDirectional() ? WAY_FALSE : WAY_TRUE;
                    }
                }
            }
        }
        return INPUT;
    }

    private AbstractRailroadElement getElement(Position p, AbstractRailroadElement[][] railroad) {
        return railroad[p.getX()][p.getY()];
    }

    private Position getWayToGo(Switch start, SwitchNodePosition outputDirection) {
        Switch.SwitchType type = start.getSwitchType();
        RailRotation rotation = start.getRotation();
        Position from = start.getPosition();
        boolean normalPosition = start.getNormalDirectional();

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

        return new ArrayList<>(Arrays.asList(inSwitch, inSwitch1, inSwitch2, outSwitch, outSwitch1, outSwitch2));
    }


}
