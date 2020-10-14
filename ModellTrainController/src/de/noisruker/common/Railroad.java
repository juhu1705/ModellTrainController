package de.noisruker.common;

import de.noisruker.common.messages.SensorMessage;
import de.noisruker.common.messages.SwitchMessage;
import de.noisruker.server.loconet.LocoNet;
import de.noisruker.server.loconet.LocoNetMessageReciever;
import de.noisruker.util.Ref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Railroad {

    public ArrayList<RailroadNode> nodes;
    public HashMap<RailroadNode, Integer> way = null;
    public Train drive = null;

    public int destination;

    public Railroad() {
        nodes = new ArrayList<RailroadNode>();

        this.createRailroad();
    }

    public void createRailroad() {
        nodes.addAll(Arrays.asList(
                new RailroadNode(6, false),
                new RailroadNode(11, false),
                new RailroadNode(7, false),
                new RailroadNode(10, false),
                new RailroadNode(15, false),
                new RailroadNode(14, false)));

        nodes.get(0).nodeConnections.addAll(Arrays.asList(
                new Connection(14, 7),
                new Connection(15, 10),
                new Connection(15, 7),
                new Connection(14, 10),
                new Connection(7, 14),
                new Connection(10, 15),
                new Connection(7, 15),
                new Connection(10, 14)
        ));
        nodes.get(0).nodeConnections.get(0).addCommand((byte) 16, false);
        nodes.get(0).nodeConnections.get(0).addCommand((byte) 17, false);
        nodes.get(0).nodeConnections.get(1).addCommand((byte) 16, false);
        nodes.get(0).nodeConnections.get(1).addCommand((byte) 17, false);
        nodes.get(0).nodeConnections.get(2).addCommand((byte) 16, true);
        nodes.get(0).nodeConnections.get(2).addCommand((byte) 17, false);
        nodes.get(0).nodeConnections.get(3).addCommand((byte) 16, false);
        nodes.get(0).nodeConnections.get(3).addCommand((byte) 17, true);

        nodes.get(1).nodeConnections.addAll(Arrays.asList(
                new Connection(7, 14),
                new Connection(10, 15),
                new Connection(7, 15),
                new Connection(10, 14),
                new Connection(14, 7),
                new Connection(15, 10),
                new Connection(15, 7),
                new Connection(14, 10)
        ));
        nodes.get(1).nodeConnections.get(0).addCommand((byte) 8, true);
        nodes.get(1).nodeConnections.get(0).addCommand((byte) 9, true);
        nodes.get(1).nodeConnections.get(1).addCommand((byte) 8, false);
        nodes.get(1).nodeConnections.get(1).addCommand((byte) 9, false);
        nodes.get(1).nodeConnections.get(2).addCommand((byte) 8, false);
        nodes.get(1).nodeConnections.get(2).addCommand((byte) 9, true);
        nodes.get(1).nodeConnections.get(3).addCommand((byte) 8, true);
        nodes.get(1).nodeConnections.get(3).addCommand((byte) 9, false);
        nodes.get(1).nodeConnections.get(4).addCommand((byte) 8, true);
        nodes.get(1).nodeConnections.get(4).addCommand((byte) 9, true);
        nodes.get(1).nodeConnections.get(5).addCommand((byte) 8, false);
        nodes.get(1).nodeConnections.get(5).addCommand((byte) 9, false);
        nodes.get(1).nodeConnections.get(6).addCommand((byte) 8, false);
        nodes.get(1).nodeConnections.get(6).addCommand((byte) 9, true);
        nodes.get(1).nodeConnections.get(7).addCommand((byte) 8, true);
        nodes.get(1).nodeConnections.get(7).addCommand((byte) 9, false);

        nodes.get(2).nodeConnections.add(new Connection(6, 11));
        nodes.get(3).nodeConnections.add(new Connection(6, 11));
        nodes.get(4).nodeConnections.add(new Connection(11, 6));
        nodes.get(5).nodeConnections.add(new Connection(11, 6));

        nodes.get(2).nodeConnections.add(new Connection(11, 6));
        nodes.get(3).nodeConnections.add(new Connection(11, 6));
        nodes.get(4).nodeConnections.add(new Connection(6, 11));
        nodes.get(5).nodeConnections.add(new Connection(6, 11));
    }


    public void init() {
        LocoNetMessageReciever.getInstance().registerListener(l -> {

            if (l instanceof SensorMessage) {
                SensorMessage s = (SensorMessage) l;

                if(s.getState())
                    trainEnter(s.getAddress());
                else
                    trainLeft(s.getAddress());
            }

        });
    }

    public int trainsWithDestination() {
        int counter = 0;
        for(Train t: LocoNet.getInstance().getTrains()) {
            if(t.destination != -1 && way == null)
                counter++;
        }
        return counter;
    }

    int last = -1;
    boolean stop = false;

    public void trainEnter(final int nodeAddress) {
        LocoNet.getInstance().getTrains().forEach(t -> t.trainEnter(nodeAddress));
    }

    public void trainLeft(final int nodeAddress) {
        LocoNet.getInstance().getTrains().forEach(t -> t.trainLeft(nodeAddress));
    }

    public void update() {
        LocoNet.getInstance().getTrains().forEach(Train::update);
    }

    public RailroadNode getNodeByAddress(int address) {
        for(RailroadNode node: this.nodes) {
            if(node.address == address)
                return node;
        }
        return null;
    }

    public void calculateRailway(int tFrom, int from, int to, Train t) {
        RailroadNode rFrom = getNodeByAddress(from);
        RailroadNode rTo = getNodeByAddress(to);
        if(rFrom == null || rTo == null)
            return;

        t.destination = to;

        int state = 0;

        rFrom.forCalculation = state;

        state++;

        RailroadNode actual = rFrom;

        int aFrom = actual.address;

        initCalculation();

        HashMap<RailroadNode, Integer> connection = calculateFromTo(aFrom, actual.nodeConnections.get(getWayFor(tFrom, actual)).to, to, state, t);

        if(connection == null) {
            t.destination = -1;
            return;
        }

        t.way = connection;
        t.startWay = getWayFor(tFrom, actual);
        t.startPos = actual.address;


        Ref.LOGGER.info("Found way: " + t.way.toString());
    }

    public void initCalculation() {
        nodes.forEach(node -> {
            node.forCalculation = 100;
        });
    }

    public int getWayFor(int from, RailroadNode node) {
        for(int i = 0; i < node.nodeConnections.size(); i++) {
            if (from == node.nodeConnections.get(i).from) {
                if (getNodeByAddress(node.nodeConnections.get(i).to).reservated == null) {
                    boolean wayIsFree = true;
                    int lastAddress = node.nodeConnections.get(i).from;
                    RailroadNode toCheck = getNodeByAddress(node.nodeConnections.get(i).to);


                    while(toCheck.nodeConnections.size() == 2 &&
                            toCheck.nodeConnections.get(0).to == toCheck.nodeConnections.get(1).from &&
                            toCheck.nodeConnections.get(1).to == toCheck.nodeConnections.get(0).from) {
                        if (getNodeByAddress(toCheck.nodeConnections.get(lastAddress == toCheck.nodeConnections.get(0).from ? 0 : 1).to).reservated != null)
                            wayIsFree = false;

                        if(!wayIsFree)
                            break;

                        toCheck = getNodeByAddress(toCheck.nodeConnections.get(lastAddress == toCheck.nodeConnections.get(0).from ? 0 : 1).to);
                    }
                    if(wayIsFree)
                        return i;
                }
            }
        }
        return 0;
    }

    public boolean checkRailway(RailroadNode node, int wayUsed) {
        boolean wayIsFree = true;
        int lastAddress = node.nodeConnections.get(wayUsed).from;

        RailroadNode toCheck = getNodeByAddress(node.nodeConnections.get(wayUsed).to);


        while(toCheck.nodeConnections.size() == 2 &&
                toCheck.nodeConnections.get(0).to == toCheck.nodeConnections.get(1).from &&
                toCheck.nodeConnections.get(1).to == toCheck.nodeConnections.get(0).from) {
            if (getNodeByAddress(toCheck.nodeConnections.get(lastAddress == toCheck.nodeConnections.get(0).from ? 0 : 1).to).reservated != null)
                wayIsFree = false;

            if(!wayIsFree)
                break;

            lastAddress = toCheck.address;
            toCheck = getNodeByAddress(toCheck.nodeConnections.get(lastAddress == toCheck.nodeConnections.get(0).from ? 0 : 1).to);
        }

        return wayIsFree;
    }

    public HashMap<RailroadNode, Integer> calculateFromTo(int from, int actual, int to, int value, Train t) {
        if(actual == to) {
            HashMap<RailroadNode, Integer> way = new HashMap<RailroadNode, Integer>();

            way.put(getNodeByAddress(actual), getWayFor(from, getNodeByAddress(actual)));

            if(!checkRailway(getNodeByAddress(actual), getWayFor(from, getNodeByAddress(actual))))
                return null;

            return way;
        }

        RailroadNode rActual = getNodeByAddress(actual), rTo = getNodeByAddress(to);

        if(rActual == null || rTo == null || rActual.forCalculation <= value)
            return null;

        rActual.forCalculation = value;
        value++;

        HashMap<RailroadNode, Integer> nodes = null;

        int connection = 0;

        for(int i = 0; i < rActual.nodeConnections.size(); i++) {
            Connection c = rActual.nodeConnections.get(i);
            if(c.from == from && (rActual.reservated == null || rActual.reservated == t)) {
                nodes = calculateFromTo(rActual.address, c.to, to, value, t);
                if(nodes != null) {
                    connection = i;
                    break;
                }
            }
        }

        if(nodes == null)
            return null;

        nodes.put(rActual, connection);

        return nodes;
    }

    public void manageConflict(Train conflict1, Train conflict2, RailroadNode position) {
        conflict2.setBreakSpeed();
        conflict2.stopNext = true;

        if(conflict2.lastPosition == position.address) {
            conflict2.setMaxSpeed();
            conflict2.stopNext = false;
            return;
        }


        RailroadNode node = getNodeByAddress(conflict2.actualPosition);
        int to = node.nodeConnections.get(conflict2.way.get(node)).to;
        if(conflict1.actualPosition == to) {
            RailroadNode node1 = getNodeByAddress(conflict1.actualPosition);

            for(RailroadNode n: this.nodes) {
                if(n.reservated == conflict1 && (conflict1.actualPosition != n.address || conflict1.lastPosition != n.address))
                    n.reservated = null;
            }

            this.calculateRailway(conflict1.lastPosition, conflict1.actualPosition, conflict1.destination, conflict1);

            if(conflict1.way == null) {
                conflict1.destination = -1;

                for(RailroadNode n: this.nodes) {
                    if(n.reservated == conflict2 && (conflict2.actualPosition != n.address || conflict2.lastPosition != n.address))
                        n.reservated = null;
                }

                this.calculateRailway(conflict2.lastPosition, conflict2.actualPosition, conflict2.destination, conflict2);

                if(conflict2.way == null) {
                    for(RailroadNode n: this.nodes) {
                        if(n.reservated == conflict1 && (conflict1.actualPosition != n.address || conflict1.lastPosition != n.address))
                            n.reservated = null;
                        if(n.reservated == conflict2 && (conflict2.actualPosition != n.address || conflict2.lastPosition != n.address))
                            n.reservated = null;
                    }

                    conflict1.way = null;
                    conflict1.destination = -1;

                    conflict2.way = null;
                    conflict2.destination = -1;
                } else {
                    conflict2.stopNext = false;
                    conflict2.setMaxSpeed();
                }
            } else {
                conflict2.stopNext = false;
                conflict2.setMaxSpeed();
            }
        } else {
            conflict2.stopNext = false;
            conflict2.setMaxSpeed();
        }
    }

    public ArrayList<RailroadNode> getNodes() {
        return this.nodes;
    }

    public class RailroadNode {
        int forCalculation;

        Train reservated;

        int address;
        boolean state;

        public int trainFrom;

        ArrayList<Connection> nodeConnections;

        public RailroadNode(int address, boolean state) {
            this.address = address;
            this.state = state;
            trainFrom = -1;
            nodeConnections = new ArrayList<>();
        }

        public void trainEnter(int from) {
            this.trainFrom = from;
        }

        public void trainLeft() {
            this.trainFrom = -1;
        }

        public Train reservate(Train t) {
            if(reservated == null)
                reservated = t;
            return reservated;
        }

        public Train getReservated() {
            return reservated;
        }

        public int getAddress() {
            return this.address;
        }

        public void activateWayTo(int from, int to) {
            for(Connection node: nodeConnections) {
                if(to == node.to && from == node.from)
                    node.activate();
            }
        }

        @Override
        public String toString() {
            return "RailroadNode{" +
                    "address=" + address +
                    ", state=" + state +
                    '}';
        }
    }

    public class Connection {
        int from, to;
        ArrayList<SwitchMessage> states;

        public Connection(int to, int from) {
            this.to = to;
            this.from = from;
            states = new ArrayList<>();
        }

        public void addCommand(byte address, boolean state) {
            this.states.add(new SwitchMessage(address, state));
        }

        public void activate() {
            this.states.forEach(state -> {
                try {
                    Ref.LOGGER.info(state.toString());

                    state.send();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {

                    }
                } catch (IOException e) {
                    Ref.LOGGER.warning("No open server found!");
                }
            });
        }
    }

}
