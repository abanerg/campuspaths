package pathfinder.junitTests;

import graph.Graph;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import pathfinder.DijkstraSearch;
import pathfinder.datastructures.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

/**
 * This class contains test cases used for testing the implementation of the specifications
 * of the {@link DijkstraSearch} class.
 *
 * <p>
 */
public class TestDijkstraSearch {
    @Rule
    public Timeout globalTimeout = Timeout.seconds(10); // 10 seconds max per method tested

    // Graph to do searches on
    private static Graph<String, Double> graph;
    /**
     * Initialize graph to do search tests on
     */
    @BeforeClass
    public static void createTestGraph() {
        graph = new Graph<>();
        graph.addEdge("A", "B", 10.0);
        graph.addEdge("B", "A", 10.0);
        graph.addEdge("B", "C", 10.0);
        graph.addEdge("B", "B2", 10.0);
        graph.addEdge("C", "B", 10.0);
        graph.addEdge("B2", "Z", 4.0);
        graph.addEdge("W","Q", 3.0);
    }

    /**
     * Test that a bidirectional path is searched correctly, as both should be the same (reversed) lowest-cost
     * path.
     */
    @Test
    public void testBidirectionality () {
        Path<String> path1 = DijkstraSearch.findPath(graph, "A", "C");
        Path<String> path2 = DijkstraSearch.findPath(graph, "C", "A");
        List<Path<String>.Segment> list1 = new ArrayList<>();
        List<Path<String>.Segment> list2 = new ArrayList<>();
        for (Path<String>.Segment segment : path1) list1.add(segment);
        for (Path<String>.Segment segment : path2) list2.add(segment);
        Collections.reverse(list2);
        for (int i = 0; i < list1.size(); i++) {
            assertEquals(list1.get(i).getStart(), list2.get(i).getEnd());
            assertEquals(list1.get(i).getEnd(), list2.get(i).getStart());
            assertTrue(((Double)list1.get(i).getCost()).equals((Double)list2.get(i).getCost()));
        }
    }

    /**
     * Test search correctly returns null when Path is not on graph
     */
    @Test
    public void testNoPathNull () {
        assertNull(DijkstraSearch.findPath(graph, "A", "Q"));
    }
}
