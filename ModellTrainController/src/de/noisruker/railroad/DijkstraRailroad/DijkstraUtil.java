package de.noisruker.railroad.DijkstraRailroad;

import java.util.*;
import de.noisruker.railroad.elements.Sensor;

public class DijkstraUtil {

    public static <N extends DijkstraNode> List<N> findShortestPath(Collection<N> graph, N source, Sensor target) {
        Map<N, DijkstraNodeWrapper<N>> nodeWrappers = new HashMap<>();
        TreeSet<DijkstraNodeWrapper<N>> queue = new TreeSet<>();
        Set<N> shortestPathFound = new HashSet<>();

        // Add source to queue
        DijkstraNodeWrapper<N> sourceWrapper = new DijkstraNodeWrapper<>(source, 0, null);
        nodeWrappers.put(source, sourceWrapper);
        queue.add(sourceWrapper);

        while (!queue.isEmpty()) {
            DijkstraNodeWrapper<N> nodeWrapper = queue.pollFirst();
            N node = nodeWrapper.getNode();
            shortestPathFound.add(node);

            // Have we reached the target? --> Build and return the path
            if(node instanceof SensorNode) {
                if (((SensorNode) node).getSensor().equals(target)) {
                    //TODO: CHECK
                    return buildPath(nodeWrapper);
                }
            }

            // Iterate over all neighbors
            Set<Map.Entry<DijkstraNode, Integer>> neighbors = node.nodes.entrySet();
            for (Map.Entry<DijkstraNode, Integer> neighborSet : neighbors) {
                if(neighborSet.getKey() == null)
                    continue;

                N neighbor = (N) neighborSet.getKey();

                // Ignore neighbor if shortest path already found
                if (shortestPathFound.contains(neighbor)) {
                    continue;
                }

                // Calculate total distance from start to neighbor via current node
                int distance = neighborSet.getValue();
                int totalDistance = nodeWrapper.getTotalDistance() + distance;

                // Neighbor not yet discovered?
                DijkstraNodeWrapper<N> neighborWrapper = nodeWrappers.get(neighbor);
                if (neighborWrapper == null) {
                    neighborWrapper = new DijkstraNodeWrapper<>(neighbor, totalDistance, nodeWrapper);
                    nodeWrappers.put(neighbor, neighborWrapper);
                    queue.add(neighborWrapper);
                }

                // Neighbor discovered, but total distance via current node is shorter?
                // --> Update total distance and predecessor
                else if (totalDistance < neighborWrapper.getTotalDistance()) {
                    // The position in the TreeSet won't change automatically;
                    // we have to remove and reinsert the node.
                    // Because TreeSet uses compareTo() to identity a node to remove,
                    // we have to remove it *before* we change the total distance!
                    queue.remove(neighborWrapper);

                    neighborWrapper.setTotalDistance(totalDistance);
                    neighborWrapper.setPredecessor(nodeWrapper);

                    queue.add(neighborWrapper);
                }
            }
        }

        // All nodes were visited but the target was not found
        return null;
    }

    private static <N extends DijkstraNode> List<N> buildPath(DijkstraNodeWrapper<N> nodeWrapper) {
        List<N> path = new ArrayList<>();
        while (nodeWrapper != null) {
            path.add(nodeWrapper.getNode());
            nodeWrapper = nodeWrapper.getPredecessor();
        }
        Collections.reverse(path);
        return path;
    }
}
