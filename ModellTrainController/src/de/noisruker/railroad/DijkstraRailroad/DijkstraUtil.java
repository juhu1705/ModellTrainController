package de.noisruker.railroad.DijkstraRailroad;

import java.util.*;
import de.noisruker.railroad.elements.Sensor;
import de.noisruker.util.Ref;

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

            Ref.LOGGER.info("Calculate for node: " + node);

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
                    Ref.LOGGER.info("Shortest Path already found");
                    continue;
                }

                // Calculate total distance from start to neighbor via current node
                int distance = neighborSet.getValue();
                int totalDistance = nodeWrapper.getTotalDistance() + distance;

                Ref.LOGGER.info("Add new Node with distance " + totalDistance);

                // Neighbor not yet discovered?
                DijkstraNodeWrapper<N> neighborWrapper = nodeWrappers.get(neighbor);
                if (neighborWrapper == null) {
                    Ref.LOGGER.info("Create new NodeWrapper");
                    neighborWrapper = new DijkstraNodeWrapper<>(neighbor, totalDistance, nodeWrapper);
                    nodeWrappers.put(neighbor, neighborWrapper);
                    if(!queue.isEmpty())
                        Ref.LOGGER.info("" + Objects.equals(neighborWrapper, queue.first()));

                    Ref.LOGGER.info("Add " + neighborWrapper);
                    Ref.LOGGER.info("BEFORE " + queue);
                    // TODO: Rewrite queue so it does not return false on adding a same length element
                    queue.add(neighborWrapper);
                    Ref.LOGGER.info("AFTER " + queue);
                }

                // Neighbor discovered, but total distance via current node is shorter?
                // --> Update total distance and predecessor
                else if (totalDistance < neighborWrapper.getTotalDistance()) {
                    Ref.LOGGER.info("Override exciting NodeWrapper");
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
            Ref.LOGGER.info(queue.toString());
            Ref.LOGGER.info("Node finished skip to next node");
        }

        // All nodes were visited but the target was not found
        return null;
    }

    private static <N extends DijkstraNode> List<N> buildPath(DijkstraNodeWrapper<N> nodeWrapper) {
        List<N> path = new ArrayList<>();
        while (nodeWrapper != null) {
            Ref.LOGGER.info("Node: " + nodeWrapper.getNode());
            if(nodeWrapper.getNode() instanceof SensorNode) {
                Ref.LOGGER.info("---> Sensor: " + ((SensorNode) nodeWrapper.getNode()).getSensor());
            }
            path.add(nodeWrapper.getNode());
            nodeWrapper = nodeWrapper.getPredecessor();
        }
        Collections.reverse(path);
        return path;
    }
}
