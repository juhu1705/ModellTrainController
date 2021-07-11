package de.noisruker.railroad.DijkstraRailroad;

import java.util.Objects;

public class DijkstraNodeWrapper<N> implements Comparable<DijkstraNodeWrapper<N>> {

    private final N node;
    private int totalDistance;
    private DijkstraNodeWrapper<N> predecessor;

    public DijkstraNodeWrapper(N node, int totalDistance, DijkstraNodeWrapper<N> predecessor) {
        this.node = node;
        this.totalDistance = totalDistance;
        this.predecessor = predecessor;
    }

    public N getNode() {
        return this.node;
    }

    public int getTotalDistance() {
        return this.totalDistance;
    }

    public DijkstraNodeWrapper<N> getPredecessor() {
        return this.predecessor;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    public void setPredecessor(DijkstraNodeWrapper<N> predecessor) {
        this.predecessor = predecessor;
    }

    @Override
    public int compareTo(DijkstraNodeWrapper<N> o) {
        return Integer.compare(this.totalDistance, o.totalDistance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DijkstraNodeWrapper<?> that = (DijkstraNodeWrapper<?>) o;
        return node.equals(that.node);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node);
    }

    @Override
    public String toString() {
        return "DijkstraNodeWrapper{" +
                "node=" + node +
                ", totalDistance=" + totalDistance +
                '}';
    }
}
