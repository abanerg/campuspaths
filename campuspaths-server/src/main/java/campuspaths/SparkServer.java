
package campuspaths;

import campuspaths.utils.CORSFilter;
import com.google.gson.Gson;
import pathfinder.CampusMap;
import pathfinder.datastructures.Path;
import pathfinder.datastructures.Point;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;


public class SparkServer {

    public static void main(String[] args) {
        CORSFilter corsFilter = new CORSFilter();
        corsFilter.apply();
        // The above two lines help set up some settings that allow the
        // React application to make requests to the Spark server, even though it
        // comes from a different server.
        // You should leave these two lines at the very beginning of main().
        CampusMap campusMap = new CampusMap();
        Gson gson = new Gson();
        Spark.get("/find-path", new Route(){
            @Override
            public Object handle(Request request, Response response) throws Exception {
                String startBuilding = request.queryParams("start");
                String endBuilding = request.queryParams("end");
                Path<Point> path = campusMap.findShortestPath(startBuilding, endBuilding);
                return gson.toJson(path);
            }
        });
        Spark.get("/get-valid-buildings", new Route(){
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return gson.toJson(campusMap.buildingNames());
            }
        });
    }
}
