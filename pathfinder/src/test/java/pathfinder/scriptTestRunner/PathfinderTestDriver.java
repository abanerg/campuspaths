
package pathfinder.scriptTestRunner;

import graph.Graph;
import pathfinder.DijkstraSearch;
import pathfinder.datastructures.Path;

import java.io.*;
import java.util.*;

/**
 * This class implements a test driver that uses a script file format
 * to test an implementation of Dijkstra's algorithm on a graph.
 */

public class PathfinderTestDriver {

    // ***************************
    // ***  JUnit Test Driver  ***
    // ***************************
    /**
     * String -> Graph: maps the names of graphs to the actual graph
     **/

    private final Map<String, Graph<String, Double>> graphs = new HashMap<>();
    private final PrintWriter output;
    private final BufferedReader input;

    /**
     * @spec.requires r != null && w != null
     * @spec.effects Creates a new GraphTestDriver which reads command from
     * {@code r} and writes results to {@code w}
     **/
    // Leave this constructor public
    public PathfinderTestDriver(Reader r, Writer w) {
        input = new BufferedReader(r);
        output = new PrintWriter(w);
    }

    /**
     * @throws IOException if the input or output sources encounter an IOException
     * @spec.effects Executes the commands read from the input and writes results to the output
     **/
    // Leave this method public
    public void runTests() throws IOException {
        String inputLine;
        while((inputLine = input.readLine()) != null) {
            if((inputLine.trim().length() == 0) ||
                    (inputLine.charAt(0) == '#')) {
                // echo blank and comment lines
                output.println(inputLine);
            } else {
                // separate the input line on white space
                StringTokenizer st = new StringTokenizer(inputLine);
                if(st.hasMoreTokens()) {
                    String command = st.nextToken();

                    List<String> arguments = new ArrayList<>();
                    while(st.hasMoreTokens()) {
                        arguments.add(st.nextToken());
                    }

                    executeCommand(command, arguments);
                }
            }
            output.flush();
        }
    }

    private void executeCommand(String command, List<String> arguments) {
        try {
            switch(command) {
                case "CreateGraph":
                    createGraph(arguments);
                    break;
                case "AddNode":
                    addNode(arguments);
                    break;
                case "AddEdge":
                    addEdge(arguments);
                    break;
                case "ListNodes":
                    listNodes(arguments);
                    break;
                case "ListChildren":
                    listChildren(arguments);
                    break;
                case "FindPath":
                    findPath(arguments);
                    break;
                default:
                    output.println("Unrecognized command: " + command);
                    break;
            }
        } catch(Exception e) {
            String formattedCommand = command;
            formattedCommand += arguments.stream().reduce("", (a, b) -> a + " " + b);
            output.println("Exception while running command: " + formattedCommand);
            e.printStackTrace(output);
        }
    }
    private void createGraph(List<String> arguments) {
        if(arguments.size() != 1) {
            throw new CommandException("Bad arguments to CreateGraph: " + arguments);
        }

        String graphName = arguments.get(0);
        createGraph(graphName);
    }
    private void createGraph(String graphName) {
        graphs.put(graphName, new Graph<String, Double>());
        output.println("created graph "+graphName);
    }
    private void addNode(List<String> arguments) {
        if(arguments.size() != 2) {
            throw new CommandException("Bad arguments to AddNode: " + arguments);
        }

        String graphName = arguments.get(0);
        String nodeName = arguments.get(1);

        addNode(graphName, nodeName);
    }
    private void addNode(String graphName, String nodeName) {
        Graph<String, Double> g = graphs.get(graphName);
        g.addNode(nodeName);
        output.println("added node "+nodeName+" to "+graphName);
    }
    private void addEdge(List<String> arguments) {
        if(arguments.size() != 4) {
            throw new CommandException("Bad arguments to AddEdge: " + arguments);
        }

        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        String childName = arguments.get(2);
        String edgeLabel = arguments.get(3);

        addEdge(graphName, parentName, childName, edgeLabel);
    }

    private void addEdge(String graphName, String parentName, String childName,
                         String edgeLabel) {
        Graph<String, Double> g = graphs.get(graphName);
        // Turn string into Double for graph
        g.addEdge(parentName, childName, Double.parseDouble(edgeLabel));
        output.println("added edge "+String.format("%.3f",Double.parseDouble(edgeLabel))+" from "+parentName+" to "+childName+" in "+graphName);
    }
    private void listNodes(List<String> arguments) {
        if(arguments.size() != 1) {
            throw new CommandException("Bad arguments to ListNodes: " + arguments);
        }

        String graphName = arguments.get(0);
        listNodes(graphName);
    }

    private void listNodes(String graphName) {
        Graph<String, Double> g = graphs.get(graphName);
        String ret = graphName+" contains:";
        // Get nodes as a list from nodes iterator
        Iterator<String> nodes = g.nodeIterator();
        List<String> list = new ArrayList<>();
        while (nodes.hasNext()) {
            list.add(nodes.next());
        }
        // Sort
        Collections.sort(list,new Comparator<String>() {
            public int compare(String node, String otherNode) {
                return node.compareTo(otherNode);
            }
        });
        // Add nodes to return string
        for (String i : list) ret += " "+i;
        output.println(ret);
    }

    private void listChildren(List<String> arguments) {
        if(arguments.size() != 2) {
            throw new CommandException("Bad arguments to ListChildren: " + arguments);
        }

        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        listChildren(graphName, parentName);
    }

    private void listChildren(String graphName, String parentName) {
        Graph<String, Double> g = graphs.get(graphName);
        Map<String, List<Double>> map = g.childrenOf(parentName);
        // Start output string
        String ret = "the children of "+parentName+" in "+graphName+" are:";
        // Make sorted list of nodes from map
        List<String> nodes = new ArrayList<>(map.keySet());
        Collections.sort(nodes,new Comparator<String>() {
            public int compare(String node, String otherNode) {
                return node.compareTo(otherNode);
            }
        });
        // Sort edges of each child node numerically
        for (String i : map.keySet()) {
            Collections.sort(map.get(i),new Comparator<Double>() {
                public int compare(Double edge, Double otherEdge) {
                    return edge.compareTo(otherEdge);
                }
            });
        }
        // Add each node edge pair to output
        for (String n : nodes) {
            for (Double e : map.get(n)) {
                ret += " "+n+"("+String.format("%.3f",e)+")";
            }
        }
        output.println(ret);
    }

    private void findPath(List<String> arguments) {
        if(arguments.size() != 3) {
            throw new CommandException("Bad arguments to FindPath: " + arguments);
        }
        String graphName = arguments.get(0);
        String startNode = arguments.get(1);
        String endNode = arguments.get(2);
        findPath(graphName, startNode, endNode);
    }

    private void findPath(String graphName, String startNode, String endNode) {
        boolean valid = true;
        // Get graph
        Graph <String, Double> g = graphs.get(graphName);
        if (!g.containsNode(startNode)) {
            // If invalid node, print out and make path invalid
            output.println("unknown: "+startNode);
            valid = false;
        }
        if (!g.containsNode(endNode)) {
            // If invalid node, print out and make path invalid
            output.println("unknown: "+endNode);
            valid = false;
        }
        if (valid) {
            // Search for path
            Path<String> path = DijkstraSearch.findPath(g, startNode, endNode);
            // Init return string
            String ret = "path from "+startNode+" to "+endNode+":";
            // If path was found
            if (path != null) {
                // Iterate through the segments of the path
                for (Path<String>.Segment segment : path) {
                    // Add segments information to output
                    ret += "\n" + segment.getStart() + " to " + segment.getEnd() + " with weight " +
                            String.format("%.3f", segment.getCost());
                }
                // Add total cost
                ret += "\ntotal cost: "+String.format("%.3f", path.getCost());
            }
            // If path was not found
            else ret += "\nno path found";
            output.println(ret);
        }
    }
    /**
     * This exception results when the input file cannot be parsed properly
     **/
    static class CommandException extends RuntimeException {

        public CommandException() {
            super();
        }

        public CommandException(String s) {
            super(s);
        }

        public static final long serialVersionUID = 3495;
    }
}
