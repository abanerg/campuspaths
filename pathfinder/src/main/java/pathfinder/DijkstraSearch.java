package pathfinder;

import graph.Graph;
import pathfinder.datastructures.Path;

import java.util.*;

/**
 * DijkstraSearch is a static class that allows a {@link Path} to be found between nodes on a {@link Graph} using a
 * least-cost algorithm (Dijkstra's algorithm).
 */
public class DijkstraSearch {
    /**
     * Returns the least-cost {@link Path} between two given nodes on a given graph. Returns null if no such path
     * exists.
     *
     * @spec.requires graph, startNode, endNode != null
     *                graph.contains(startNode)
     *                graph.contains(endNode)
     *                All edge labels in graph must be greater or equal to 0
     * @param graph Graph to be searched on
     * @param startNode Starting node of path
     * @param endNode End node of path
     * @param <N> Node type of graph to be searched
     * @return a {@link Path} object representing the least-cost path between the given starting and ending nodes, and
     * null if no such path exists.
     */
    public static <N> Path<N> findPath (Graph<N, Double> graph, N startNode, N endNode) { // Assume graph has edge values
        N start = startNode;                                                              // of doubles
        N end = endNode;
        // Priority queue for Dijkstra's, implements an in-line comparator for the costs of the paths
        Queue<Path<N>> active = new PriorityQueue<>(new Comparator<Path<N>>() {
            @Override
            public int compare(Path<N> o1, Path<N> o2) {
                Double a = (Double) o1.getCost();
                Double b = (Double) o2.getCost();
                return a.compareTo(b);
            }
        });
        Set<N> finished = new HashSet<>();
        // Add starting node to queue
        active.add(new Path<>(start));
        while (!active.isEmpty()) {
            // Take lowest-cost path from queue
            Path<N> minPath = active.remove();
            // Take ending node of this lowest-cost path
            N minDest = minPath.getEnd();
            // End has been found, return path to it
            if (minDest.equals(end)) return minPath;
            // If there's a path to minDest, continue
            if (!finished.contains(minDest)) {
                // Children of minDest
                Map<N, List<Double>> childrenMap = graph.childrenOf(minDest);
                // Children nodes of minDest
                List<N> nodeChildren = new ArrayList<>(childrenMap.keySet());
                for (N child : nodeChildren) {
                    // If node has not already been encountered
                    if (!finished.contains(child)) {
                        // Get all edges that get to this child
                        List<Double> allEdges = childrenMap.get(child);
                        // Sort so we only use the lowest-cost edge to get to this child
                        Collections.sort(allEdges);
                        // Get lowest-cost edge, which should be at index 0
                        double edgeCost = allEdges.get(0);
                        // Extend path and add to active
                        Path<N> newPath = minPath.extend(child, edgeCost);
                        active.add(newPath);
                    }
                }
                // Add node to finished
                finished.add(minDest);
            }
        }
        // No path was found
        return null;
    }
}
