
package pathfinder;

import graph.Graph;
import pathfinder.datastructures.Path;
import pathfinder.datastructures.Point;
import pathfinder.parser.CampusBuilding;
import pathfinder.parser.CampusPath;
import pathfinder.parser.CampusPathsParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CampusMap holds a map of buildings on the University of Washington campus and allows for {@link Path}s to be
 * found between them.
 */
public class CampusMap implements ModelAPI {
    /*  AF:
        Graph<Point, Double> map represents a directed labeled map of the University of Washington Campus where
        the nodes are the locations of specific coordinates on campus (using Points) with edges having Double values
        representing the length of the edge. Map<String, Point> shortNameToPoint maps the "short name" (i.e. CSE) of
        buildings to the coordinate location of that building on the UW campus (using Points). Map<String, String>
        shortToLongName represents the mapping of these "short names" to the longer, official name of the building on
        the UW campus.
     */
    /*  RI:
        map != null &&
        shortNameToPoint != null &&
        shortToLongName != null &&
        forall i such that i is a node in map, i is one of the points from "campus_paths.csv" &&
        forall i such that i is a key in shortNameToPoint and shortToLongName, i is one of the short names contained in
        "campus_buildings.csv" and i's value in the map is either a valid point in "campus_paths.csv" or long building
        name in "campus_buildings.csv" respectively
     */

    private static final boolean DEBUG = false;  // Debug variable

    private Graph<Point, Double> map;
    // Map of short names of buildings to Points of those buildings
    private Map<String, Point> shortNameToPoint;
    // Map of short names of buildings to long names
    private Map<String, String> shortToLongName;


    /**
     * Constructs a new {@link CampusMap} of data on the University of Washington campus.
     * @spec.effects create a new {@link CampusMap}
     */
    public CampusMap() {
        // Initialize data structures
        map = new Graph<>();
        shortNameToPoint = new HashMap<>();
        shortToLongName = new HashMap<>();
        // Build graph
        buildGraph("campus_paths.csv", "campus_buildings.csv");
        checkRep();
    }

    /**
     Throw error if representation invariant is violated

     */
    private void checkRep() {
        if (DEBUG) {
            assert ((map != null) && (shortNameToPoint != null) && (shortToLongName != null)) : "null fields.";
        }
    }

    /**
     * Builds internal graph from the data at the files given
     *
     * @param pathsFile File name of where graph data of paths are located
     * @param buildingsFile File name of where graph data of buildings are located
     * @spec.requires filename, buildingsFile != null
     * @spec.modifies {@link Graph<String, String>} map
     * @spec.effects Populates {@code map} with node and edge data from files given
     *
     */
    private void buildGraph(String pathsFile, String buildingsFile) {
        // Parse paths and buildings data
        List<CampusPath> campusPathsList = CampusPathsParser.parseCampusPaths(pathsFile);
        List<CampusBuilding> campusBuildingList = CampusPathsParser.parseCampusBuildings(buildingsFile);
        for (CampusPath path : campusPathsList) {
            Point start = new Point(path.getX1(), path.getY1());
            Point end = new Point(path.getX2(), path.getY2());
            map.addEdge(start, end, path.getDistance());
        }
        for (CampusBuilding building : campusBuildingList) {
            Point location = new Point(building.getX(), building.getY());
            shortNameToPoint.put(building.getShortName(), location);
            shortToLongName.put(building.getShortName(), building.getLongName());
        }
    }
    // Javadocs should be inherited
    @Override
    public boolean shortNameExists(String shortName) {
        checkRep();
        return shortNameToPoint.containsKey(shortName);
    }

    @Override
    public String longNameForShort(String shortName) {
        checkRep();
        String ret;
        if (!shortToLongName.containsKey(shortName)) throw new IllegalArgumentException("Short name provided does not exist");
        else {
            ret = shortToLongName.get(shortName);
            checkRep();
            return ret;
        }
    }

    @Override
    public Map<String, String> buildingNames() {
        checkRep();
        return new HashMap<String, String>(shortToLongName);
    }

    @Override
    public Path<Point> findShortestPath(String startShortName, String endShortName) {
        checkRep();
        if (startShortName == null || endShortName == null ) throw new IllegalArgumentException("Short name is null");
        if (!shortNameToPoint.containsKey(startShortName) || !shortNameToPoint.containsKey(endShortName))
            throw new IllegalArgumentException("Short name does not exist");
        Point start = shortNameToPoint.get(startShortName);
        Point end = shortNameToPoint.get(endShortName);
        checkRep();
        return DijkstraSearch.findPath(map, start, end);
    }

}
