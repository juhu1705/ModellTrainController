package de.noisruker.railroad;

import java.util.ArrayList;

public class Section {
    public boolean left = false;
    int forCalculation;

    Train reservated;

    int address;
    boolean state;

    public int trainFrom;

    ArrayList<Railroad.Connection> nodeConnections;

    public Section(int address, boolean state) {
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
        for(Railroad.Connection node: nodeConnections) {
            if(to == node.to && from == node.from)
                node.activate();
        }
    }

    @Override
    public String toString() {
        return "Section{" +
                "address=" + address +
                ", state=" + state +
                '}';
    }
}
